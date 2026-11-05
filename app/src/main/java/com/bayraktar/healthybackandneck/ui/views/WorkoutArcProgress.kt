package com.bayraktar.healthybackandneck.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.bayraktar.healthybackandneck.R

/** A semicircular countdown, using the existing timer's max/progress values. */
class WorkoutArcProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
    override fun onDraw(canvas: Canvas) {
        val stroke = 10 * resources.displayMetrics.density
        paint.strokeWidth = stroke
        val diameter = (width - stroke).coerceAtMost((height - stroke / 2) * 2).coerceAtLeast(0f)
        val left = (width - diameter) / 2
        val oval = RectF(left, stroke / 2, left + diameter, stroke / 2 + diameter)
        paint.color = ContextCompat.getColor(context, R.color.workout_track)
        canvas.drawArc(oval, 180f, 180f, false, paint)
        paint.color = ContextCompat.getColor(context, R.color.fit_primary)
        canvas.drawArc(oval, 180f, 180f * progress / max.coerceAtLeast(1), false, paint)
    }
}
