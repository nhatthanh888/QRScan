package com.example.qrscan.customcamera

import android.content.Context
import android.graphics.Rect
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import me.dm7.barcodescanner.core.DisplayUtils
import me.dm7.barcodescanner.core.ViewFinderView
import java.util.*

open class ViewScan(context: Context?) : BarcodeScanner(context) {

    private var currentScale: Float = 0f

    private var zoomScale: Float = 0f

    private lateinit var mMultiFormatReader: MultiFormatReader

    private val formatList = arrayListOf<BarcodeFormat>()

    private var mResultHandler: ResultHandler? = null

    private var camera: Camera? = null

    open fun setResultHandler(resultHandler: ResultHandler?) {
        mResultHandler = resultHandler
    }

    init {
        initMultiFormatReader()
    }

    private fun initMultiFormatReader() {
        formatList.add(BarcodeFormat.AZTEC)
        formatList.add(BarcodeFormat.CODABAR)
        formatList.add(BarcodeFormat.CODE_39)
        formatList.add(BarcodeFormat.CODE_93)
        formatList.add(BarcodeFormat.CODE_128)
        formatList.add(BarcodeFormat.DATA_MATRIX)
        formatList.add(BarcodeFormat.EAN_8)
        formatList.add(BarcodeFormat.EAN_13)
        formatList.add(BarcodeFormat.ITF)
        formatList.add(BarcodeFormat.MAXICODE)
        formatList.add(BarcodeFormat.PDF_417)
        formatList.add(BarcodeFormat.QR_CODE)
        formatList.add(BarcodeFormat.RSS_14)
        formatList.add(BarcodeFormat.RSS_EXPANDED)
        formatList.add(BarcodeFormat.UPC_A)
        formatList.add(BarcodeFormat.UPC_E)
        formatList.add(BarcodeFormat.UPC_EAN_EXTENSION)

        val hints: MutableMap<DecodeHintType, Any?> = EnumMap(DecodeHintType::class.java)
        hints[DecodeHintType.POSSIBLE_FORMATS] = formatList
        mMultiFormatReader = MultiFormatReader()
        mMultiFormatReader.setHints(hints)
    }

    override fun onPreviewFrame(dataB: ByteArray?, camera: Camera) {
        var data = dataB
        this.camera = camera
        if (currentScale != zoomScale) {
            if (currentScale in 0.0..3.0) {
                doZoom(zoomScale)
            }
        }
        if (mResultHandler != null) {
            try {
                val parameters = camera.parameters
                val size = parameters.previewSize
                var width = size.width
                var height = size.height
                if (DisplayUtils.getScreenOrientation(this.context) == 1) {
                    val rotationCount = this.rotationCount
                    if (rotationCount == 1 || rotationCount == 3) {
                        val tmp = width
                        width = height
                        height = tmp
                    }
                    data = getRotatedData(data, camera)
                }
                var rawResult: Result? = null
                val source = buildLuminanceSource(data, width, height)
                if (source != null) {
                    var bitmap = BinaryBitmap(HybridBinarizer(source))
                    try {
                        rawResult = mMultiFormatReader.decodeWithState(bitmap)
                    } catch (var29: ReaderException) {
                    } catch (var30: NullPointerException) {
                    } catch (var31: ArrayIndexOutOfBoundsException) {
                    } finally {
                        mMultiFormatReader.reset()
                    }
                    if (rawResult == null) {
                        val invertedSource = source.invert()
                        bitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                        try {
                            rawResult = mMultiFormatReader.decodeWithState(bitmap)
                        } catch (var27: NotFoundException) {
                        } finally {
                            mMultiFormatReader.reset()
                        }
                    }
                }
                if (rawResult != null) {
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        val handleResult = mResultHandler
                        mResultHandler = null
                        stopCameraPreview()
                        handleResult?.handleResult(rawResult)
                    }
                } else {
                    camera.setOneShotPreviewCallback(this)
                }
            } catch (e: RuntimeException) {

            }
        }
    }

    fun resumeCameraPreview(resultHandler: ResultHandler?) {
        mResultHandler = resultHandler
        super.resumeCameraPreview()
    }

    private fun buildLuminanceSource(
        data: ByteArray?,
        width: Int,
        height: Int
    ): PlanarYUVLuminanceSource? {
        val rect = getFramingRectInPreview(width, height)
        return if (rect == null) {
            null
        } else {
            var source: PlanarYUVLuminanceSource? = null
            try {
                source = PlanarYUVLuminanceSource(
                    data,
                    width,
                    height,
                    rect.left,
                    rect.top,
                    rect.width(),
                    rect.height(),
                    false
                )
            } catch (var7: Exception) {
            }
            source
        }
    }


    fun doZoom(percent: Float): Int {
        currentScale = percent
        zoomScale = currentScale
        var currentZoom = 0

        try {
            if (camera == null) {
                return 0
            }
            camera?.parameters?.let {
                val parameters: Camera.Parameters = it
                if (!parameters.isZoomSupported) {
                    return currentZoom
                }
                val maxZoom = parameters.maxZoom
                val newZoom = (maxZoom * percent).toInt()
                currentZoom = newZoom

                parameters.zoom = currentZoom
                camera?.parameters = parameters
            }

        } catch (e: Exception) {

        }
        return currentZoom
    }

    fun putZoomScale(scaleZoom: Float) {
        this.zoomScale = scaleZoom
    }

    fun checkSupportFlash(): Boolean {
        val parameters = camera?.parameters
        val focusModes = parameters?.supportedFocusModes
        return focusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) == true
    }

    //change rectangle scan
    inner class CustomViewFinderView(context: Context?) : ViewFinderView(context) {
        override fun getFramingRect(): Rect {
            val originalRect = super.getFramingRect();
            return Rect(
                originalRect.left + 15,
                originalRect.top - 100,
                originalRect.right - 15,
                originalRect.bottom - 20
            )
        }
    }

    interface ResultHandler {
        fun handleResult(var1: Result)
    }

}