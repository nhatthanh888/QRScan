package com.example.qrscan.customcamera

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.qrscan.R
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.ViewfinderView
class ViewFinderOverlay(context: Context, attrs: AttributeSet) : ViewfinderView(context, attrs) {
    private val scrimPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_reticle_background)
    }
    private val eraserPaint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val boxCornerRadius: Float =
        context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius).toFloat()
    private var boxRect: RectF? = null
    fun setViewFinder() {
        invalidate()
    }
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        boxRect?.let {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
            eraserPaint.style = Paint.Style.FILL
            canvas.drawRoundRect(it, boxCornerRadius, boxCornerRadius, eraserPaint)
            eraserPaint.style = Paint.Style.STROKE
            canvas.drawRoundRect(it, boxCornerRadius, boxCornerRadius, eraserPaint)
        }
    }
}