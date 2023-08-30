package com.bayraktar.healthybackandneck.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.utils.CalendarUtils
import com.bayraktar.healthybackandneck.R
import java.time.LocalDate


class CalenderAdapter(
     val days: ArrayList<LocalDate>,
     val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalenderAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(
        itemView: View,
        private val onItemListener: OnItemListener,
        days: ArrayList<LocalDate>
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val days: ArrayList<LocalDate>
        val parentView: View
        val dayOfMonth: TextView

        init {
            parentView = itemView.findViewById(R.id.parentView)
            dayOfMonth = itemView.findViewById(R.id.cellDayText)
            itemView.setOnClickListener(this)
            this.days = days
        }

        override fun onClick(view: View) {
            onItemListener.onItemClick(adapterPosition, days[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell,parent,false)
        val layoutParams = inflater.layoutParams
        if (days.size > 15) //month view
            layoutParams.height = (parent.height * 0.166666666).toInt() else  // week view
            layoutParams.height = parent.height
        return CalendarViewHolder(inflater, onItemListener, days)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.dayOfMonth.text = date.dayOfMonth.toString()
        if (date == CalendarUtils.selectedDate) holder.parentView.setBackgroundColor(Color.LTGRAY)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }
}