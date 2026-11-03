package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.databinding.ItemExercisedaysofweekBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.ExerciseItemClickListener

class ExerciseDetailFirstAdapter(
    private var lsMenu: List<ExerciseDay>,
    private val exerciseItemClickListener: ExerciseItemClickListener
): RecyclerView.Adapter<ExerciseDetailFirstAdapter.ItemHolder>() {

    var completedDays: Set<Int> = emptySet()
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
            val currentDay = lsMenu.firstOrNull { it.day !in completedDays && (it.isCompleted || adFree || "${it.level}:${it.day}" in rewardedWorkoutDayKeys) }?.day
            dayState.setText(when {
                model.day in completedDays -> R.string.workout_day_complete
                !unlocked -> R.string.workout_day_locked_hint
                model.day == currentDay -> R.string.workout_day_continue
                else -> R.string.workout_day_ready
            })
            cardview1.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(root.context, if (model.day == currentDay) R.color.fit_soft else android.R.color.white))
            txtDay.text = model.day.toString()
            txtExerciseCount.text = (exerciseCounts?.get(model.dayId)
                ?: if (exerciseCounts != null) 0 else model.exerciseCount).toString()
            txtExerciseTime.text = model.exerciseTime.toString()
            txtExerciseKcal.text = model.exerciseKcal.toString()


            if (model.isCompleted || adFree || "${model.level}:${model.day}" in rewardedWorkoutDayKeys) {
                lock.visibility = View.GONE
                cardview1.setOnClickListener {
                    exerciseItemClickListener.onExerciseItemClicked(position, false)

                }
            }else {
                lock.visibility = View.VISIBLE
                cardview1.setOnClickListener {
                    exerciseItemClickListener.onExerciseItemClicked(position, true)

                }

            }
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}
