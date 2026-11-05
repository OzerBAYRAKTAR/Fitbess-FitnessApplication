package com.bayraktar.healthybackandneck.utils

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.google.android.material.button.MaterialButton

/** Presentation target only; access is still checked by the existing day click handler. */
fun continueDayPosition(days: List<ExerciseDay>, completed: Set<Int>, rewarded: Set<String>, adFree: Boolean, resumeDay: Int?): Int {
    fun available(day: ExerciseDay) = day.isCompleted || adFree || "${day.level}:${day.day}" in rewarded
    val resume = days.indexOfFirst { it.day == resumeDay && it.day !in completed && available(it) }
    if (resume >= 0) return resume
    val next = days.indexOfFirst { it.day !in completed && available(it) }
    return if (next >= 0) next else days.indexOfLast(::available)
}

fun bindWorkoutDayControls(
    owner: LifecycleOwner, list: RecyclerView, weeks: List<MaterialButton>, action: MaterialButton,
    target: () -> Pair<Int, Int>?, onContinue: (Int) -> Unit
) {
    val adapter = requireNotNull(list.adapter)
    fun renderWeek(week: Int) {
        weeks.forEachIndexed { index, button ->
            val selected = index == week
            button.isSelected = selected
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(button.context, if (selected) R.color.fit_primary else R.color.workout_cream))
            button.setTextColor(ContextCompat.getColor(button.context, if (selected) android.R.color.white else R.color.fit_text))
        }
    }
    fun renderAction() {
        val next = target()
        action.isEnabled = next != null
        action.text = if (next == null) action.context.getString(R.string.access_loading)
            else action.context.getString(R.string.workout_continue_day, next.second)
    }
    weeks.forEachIndexed { index, button -> button.setOnClickListener {
        (list.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(index * 7, 0)
        renderWeek(index)
    } }
    val scroll = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val position = (list.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
            renderWeek((position.coerceAtLeast(0) / 7).coerceIn(0, 2))
        }
    }
    val observer = object : RecyclerView.AdapterDataObserver() { override fun onChanged() = renderAction() }
    list.addOnScrollListener(scroll)
    adapter.registerAdapterDataObserver(observer)
    action.setOnClickListener { target()?.let { onContinue(it.first) } }
    renderWeek(0)
    renderAction()
    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            list.removeOnScrollListener(scroll)
            adapter.unregisterAdapterDataObserver(observer)
            owner.lifecycle.removeObserver(this)
        }
    })
}
