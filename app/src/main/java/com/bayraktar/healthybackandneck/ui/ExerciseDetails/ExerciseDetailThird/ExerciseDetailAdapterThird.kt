package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailThird

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.databinding.ItemExercisedaysofweekBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.ExerciseItemClickListener
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerClicked

class ExerciseDetailAdapterThird(
    private var lsMenu: List<ExerciseDay>,
    private val exerciseItemClickListener: ExerciseItemClickListener
): RecyclerView.Adapter<ExerciseDetailAdapterThird.ItemHolder>() {

    var completedDays: Set<Int> = emptySet()
    var resumeDay: Int? = null
    var adFree = false
    var rewardedWorkoutDayKeys: Set<String> = emptySet()
    var exerciseCounts: Map<Int, Int>? = null


    inner class ItemHolder(val binding: ItemExercisedaysofweekBinding): RecyclerView.ViewHolder(binding.root) {



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemExercisedaysofweekBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(itemBinding)
    }
    //update old list with new list
    fun setData(list: List<ExerciseDay>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model =lsMenu[position]

        holder.binding.apply {
            val unlocked = model.isCompleted || adFree || "${model.level}:${model.day}" in rewardedWorkoutDayKeys
            val currentDay = lsMenu.getOrNull(continuePosition())?.day
            dayState.setText(when {
                model.day in completedDays -> R.string.workout_day_complete
                !unlocked -> R.string.workout_day_locked_hint
                model.day == currentDay -> R.string.workout_day_continue
                else -> R.string.workout_day_ready
            })
            dayState.setTextColor(androidx.core.content.ContextCompat.getColor(root.context, if (unlocked || model.day in completedDays) R.color.fit_primary else R.color.fit_text))
            cardview1.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(root.context, if (model.day == currentDay) R.color.fit_soft else android.R.color.white))

            lock.imageTintList = if (model.day in completedDays) null else android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(root.context, R.color.fit_primary))
            cardview1.strokeColor = androidx.core.content.ContextCompat.getColor(root.context, if (model.day == currentDay) R.color.fit_primary else R.color.fit_border)
            txtDay.text = model.day.toString()
            txtExerciseCount.text = (exerciseCounts?.get(model.dayId)
                ?: if (exerciseCounts != null) 0 else model.exerciseCount).toString()
            txtExerciseTime.text = model.exerciseTime.toString()
            txtExerciseKcal.text = model.exerciseKcal.toString()

            if (model.isCompleted || adFree || "${model.level}:${model.day}" in rewardedWorkoutDayKeys) {
            lock.visibility = View.VISIBLE
            lock.setImageResource(if (model.day in completedDays) R.drawable.workout_day_complete_badge else R.drawable.discovery_chevron)
            cardview1.setOnClickListener {
                exerciseItemClickListener.onExerciseItemClicked(position, false)

            }
        }else {
            lock.visibility = View.VISIBLE
            lock.setImageResource(R.drawable.fit_lock)
            cardview1.setOnClickListener {
                exerciseItemClickListener.onExerciseItemClicked(position, true)

            }

        }
        }
    }

    fun continuePosition(): Int = com.bayraktar.healthybackandneck.utils.continueDayPosition(lsMenu, completedDays, rewardedWorkoutDayKeys, adFree, resumeDay)
    fun continueTarget(): Pair<Int, Int>? { val position = continuePosition(); return lsMenu.getOrNull(position)?.let { position to it.day } }
    override fun getItemCount(): Int = lsMenu.size
}
