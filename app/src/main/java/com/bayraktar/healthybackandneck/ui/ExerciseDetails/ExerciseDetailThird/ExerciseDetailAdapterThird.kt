package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailThird

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.databinding.ItemExercisedaysofweekBinding
import com.bayraktar.healthybackandneck.utils.RecyclerClicked
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class ExerciseDetailAdapterThird(
    private var lsMenu: List<ExerciseDay>,
    private val rclClickListener: RecyclerClicked
): RecyclerView.Adapter<ExerciseDetailAdapterThird.ItemHolder>() {


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
            txtDay.text = model.day.toString()
            txtExerciseCount.text = model.exerciseCount.toString()
            txtExerciseTime.text = model.exerciseTime.toString()
            txtExerciseKcal.text = model.exerciseKcal.toString()

            if (model.isCompleted) {
                lock.visibility = View.GONE
                cardview1.setOnClickListener {
                    rclClickListener.onItemclicked(position)
                }
            }else {
                lock.visibility = View.VISIBLE
                val context = holder.itemView.context
                val message = context.getString(R.string.lbl_exerciseattention)
                cardview1.setOnClickListener {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}