package com.bayraktar.healthybackandneck.ui.Statistics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.text.NumberFormat

/** Axis and grid share the exact 128dp plot height used by the native, selectable columns. */
class ActivityChartGrid @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    var maximumMinutes: Double = 1.0
        set(value) { field = value; invalidate() }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val d = resources.displayMetrics.density
        for (step in 0..5) {
            val y = (32 + step * 25.6f) * d
            paint.color = Color.rgb(225, 229, 220); paint.strokeWidth = d
            canvas.drawLine(30*d, y, width.toFloat(), y, paint)
            paint.color = Color.rgb(91, 102, 94); paint.textSize = 10 * resources.displayMetrics.scaledDensity
            paint.textAlign = Paint.Align.RIGHT
            val label = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 1 }.format(maximumMinutes * (5-step) / 5)
            canvas.drawText(label, 26*d, y - (paint.ascent()+paint.descent())/2, paint)
        }
    }
}
