package com.bayraktar.healthybackandneck.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.bayraktar.healthybackandneck.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@SuppressLint("InflateParams")
fun showToast(
    context: Context,
    message: String,
    gravity: Int = Gravity.BOTTOM,
    xOffset: Int = 0,
    yOffset: Int = 150
) {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(R.layout.custom_toast_layout, null)

    val text = layout.findViewById<TextView>(R.id.toastText)
    text.text = message

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.setGravity(gravity, xOffset, yOffset)
    toast.show()
}

@SuppressLint("InflateParams")
fun showToastFavourite(
    context: Context,
    message: String,
    gravity: Int = Gravity.BOTTOM,
    xOffset: Int = 0,
    yOffset: Int = 150
) {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(R.layout.custom_toast_favourite_layout, null)

    val text = layout.findViewById<TextView>(R.id.toastText)
    text.text = message

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.setGravity(gravity, xOffset, yOffset)
    toast.show()
}

var selectedDate: LocalDate? = null
private fun sundayForDate(current: LocalDate): LocalDate? {
    var currentDate = current
    val oneWeekAgo = currentDate.minusWeeks(1)
    while (currentDate.isAfter(oneWeekAgo)) {
        if (currentDate.dayOfWeek == DayOfWeek.SUNDAY) return currentDate
        currentDate = currentDate.minusDays(1)
    }
    return null
}
