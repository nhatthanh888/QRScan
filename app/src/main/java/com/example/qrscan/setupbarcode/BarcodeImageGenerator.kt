package com.example.qrscan.setupbarcode

import android.graphics.*
import com.example.qrscan.App
import com.example.qrscan.data.model.BarcodeDb
import com.example.qrscan.util.FileUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder


object BarcodeImageGenerator {
    private val encoder = BarcodeEncoder()
    private val writer = MultiFormatWriter()

    fun generateBitmap(
        barcodeDb: BarcodeDb,
        width: Int,
        height: Int,
        margin: Int = 0,
        isCustom: Boolean = true
    ): Bitmap? {
        try {
            val matrix = encoder.encode(
                barcodeDb.text,
                barcodeDb.format,
                width,
                height,
                createHints(barcodeDb.errorCorrectionLevel, margin)
            )
            val needPad = BarcodeParser.checkIsProductCode(barcodeDb.format)

            if (isCustom) {
                return createBitmap(
                    barcodeDb,
                    matrix,
                    barcodeDb.color,
                    barcodeDb.colorBackground,
                    needPad,
                    FileUtils.getBitmapAsserts(App.appContext(), barcodeDb.logo),
                    FileUtils.getBitmapAsserts(App.appContext(), barcodeDb.backgroundTemplate),
                    barcodeDb.isGradient,
                    barcodeDb.colorTop,
                    barcodeDb.colorBottom
                )
            } else {
                return createBitmap(
                    barcodeDb,
                    matrix,
                    Color.BLACK,
                    Color.WHITE,
                    needPad,
                    null,
                    null,
                    false,
                    null,
                    null
                )
            }
        } catch (ex: Exception) {
            return null
        }
    }

    fun Bitmap.addOverlayToCenter(overlayBitmap: Bitmap): Bitmap {
        val bitmap2Width = overlayBitmap.width
        val bitmap2Height = overlayBitmap.height
        val marginLeft = (this.width * 0.5 - bitmap2Width * 0.5).toFloat()
        val marginTop = (this.height * 0.5 - bitmap2Height * 0.5).toFloat()
        val canvas = Canvas(this)
        canvas.drawBitmap(this, Matrix(), null)
        canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null)
        return this
    }

    private fun addBackground(
        bitmap: Bitmap,
        bitmapBackground: Bitmap,
        paddingBitmap: Int = 80
    ): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val bmBackground = Bitmap.createScaledBitmap(bitmapBackground, w, h, false)
        val bitmapNew =
            Bitmap.createScaledBitmap(bitmap, w - paddingBitmap, h - paddingBitmap, false)

        val canvas = Canvas(bmBackground)
        canvas.drawBitmap(bmBackground, Matrix(), null)
        val alphaPaint = Paint()
        alphaPaint.alpha = 210
        canvas.drawBitmap(
            bitmapNew,
            (paddingBitmap / 2).toFloat(),
            (paddingBitmap / 2).toFloat(),
            alphaPaint
        )
        return bmBackground
    }

    private fun createHints(errorCorrectionLevel: String?, margin: Int): Map<EncodeHintType, Any> {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.MARGIN to margin
        )

        if (errorCorrectionLevel != null) {
            hints.plus(EncodeHintType.ERROR_CORRECTION to errorCorrectionLevel)
        }

        return hints
    }

    private fun createSvg(width: Int, height: Int, matrix: BitMatrix): String {
        val result = StringBuilder()
            .append("<svg width=\"$width\" height=\"$height\"")
            .append(" viewBox=\"0 0 $width $height\"")
            .append(" xmlns=\"http://www.w3.org/2000/svg\">\n")

        val w = matrix.width
        val h = matrix.height
        val xf = width.toFloat() / w
        val yf = height.toFloat() / h

        for (y in 0 until h) {
            for (x in 0 until w) {
                if (matrix.get(x, y)) {
                    val ox = x * xf
                    val oy = y * yf
                    result.append("<rect x=\"$ox\" y=\"$oy\"")
                    result.append(" width=\"$xf\" height=\"$yf\"/>\n")
                }
            }
        }

        result.append("</svg>\n")

        return result.toString()
    }

    private fun createBitmap(
        barcodeDb: BarcodeDb,
        matrix: BitMatrix,
        codeColor: Int,
        backgroundColor: Int,
        needPad: Boolean = false,
        logo: Bitmap? = null,
        backgroundImage: Bitmap?,
        isGradient: Boolean,
        codeTopColor: Int?,
        codeBottomColor: Int?
    ): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        val pixels1 = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) codeColor else Color.TRANSPARENT
            }
        }

        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }


        if (isGradient && codeBottomColor != null && codeTopColor != null) {
            bitmap = addGradient(bitmap, codeTopColor, codeBottomColor)
        }

        if (backgroundImage == null) {
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels1[offset + x] = backgroundColor
                }
            }

            val bitmapBackgroundWhite =
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                    setPixels(pixels1, 0, width, 0, 0, width, height)
                }
            bitmap = addBackground(bitmap, bitmapBackgroundWhite, 0)
        }

        if (needPad) {
            var padX = 60
            var padY = 60
            if (barcodeDb.format == BarcodeFormat.DATA_MATRIX) {
                padX = 20
                padY = 20
            }
            pad(bitmap, padX, padY)?.let {
                bitmap = it
            }
        }
        logo?.let {
            val myLogo = Bitmap.createScaledBitmap(it, width / 8, width / 8, false)
            bitmap.addOverlayToCenter(myLogo)
        }

        if (backgroundImage != null) {
            bitmap = addBackground(bitmap, backgroundImage)
        }

        return bitmap

    }

    private fun addGradient(src: Bitmap, color1: Int, color2: Int): Bitmap {
        val w = src.width
        val h = src.height
        val result = src
        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0f, 0f, null)
        val paint = Paint()
        val shader = LinearGradient(0F, 0F, 0F, h.toFloat(), color1, color2, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        return result
    }

    private fun addGradient(color1: Int, color2: Int): Int {
        val paint = Paint()
        val shader = LinearGradient(0F, 0F, 0F, 0F, color1, color2, Shader.TileMode.CLAMP)
        paint.shader = shader
        return paint.color
    }

    fun pad(src: Bitmap, padding_x: Int, padding_y: Int): Bitmap? {
        val outputimage = Bitmap.createBitmap(
            src.width + padding_x,
            src.height + padding_y,
            Bitmap.Config.ARGB_8888
        )
        val can = Canvas(outputimage)
        can.drawColor(Color.WHITE)
        can.drawBitmap(src, padding_x.toFloat(), padding_y.toFloat(), null)

        val output = Bitmap.createBitmap(
            outputimage.width + padding_x,
            outputimage.height + padding_y,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(outputimage, 0f, 0f, null)
        return output
    }

}