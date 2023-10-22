package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailSecond

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.databinding.ItemExercisedaysofweekBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class ExerciseDetailSecondAdapter(
    private var lsMenu: List<ExerciseDay>,
    private val rclClickListener: RecyclerViewClickListener
): RecyclerView.Adapter<ExerciseDetailSecondAdapter.ItemHolder>() {


    inner class ItemHolder(val binding: ItemExercisedaysofweekBinding, recyclerViewClickListener: RecyclerViewClickListener): RecyclerView.ViewHolder(binding.root)
        , View.OnClickListener {

        private val mRecyclerViewClickListener: RecyclerViewClickListener = recyclerViewClickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mRecyclerViewClickListener.recyclerviewListClicked(p0!!,adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemExercisedaysofweekBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(itemBinding,rclClickListener)
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
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}