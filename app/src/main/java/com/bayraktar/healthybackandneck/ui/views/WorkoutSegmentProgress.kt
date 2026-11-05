package com.bayraktar.healthybackandneck.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.bayraktar.healthybackandneck.R

/** Keeps ProgressBar semantics while drawing one pill per movement. */
class WorkoutSegmentProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas: Canvas) {
        val count = max.coerceAtLeast(1)
        val available = (width - paddingLeft - paddingRight).toFloat()
        val gap = (3 * resources.displayMetrics.density).coerceAtMost(available / (count * 3))
        val segment = ((available - gap * (count - 1)) / count).coerceAtLeast(0f)
        val top = paddingTop.toFloat()
        val bottom = (height - paddingBottom).toFloat()
        for (index in 0 until count) {
            paint.color = ContextCompat.getColor(context, if (index < progress) R.color.fit_primary else R.color.workout_track)
            val visualIndex = if (layoutDirection == LAYOUT_DIRECTION_RTL) count - 1 - index else index
            val left = paddingLeft + visualIndex * (segment + gap)
            canvas.drawRoundRect(left, top, left + segment, bottom, (bottom - top) / 2, (bottom - top) / 2, paint)
        }
    }
}
