package com.bayraktar.healthybackandneck.ui.ExerciseDetailThird

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.Models.ExerciseDetailModel.ExerciseDetailModel
import com.bayraktar.healthybackandneck.databinding.ItemExercisedaysofweekBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class ExerciseDetailAdapterThird(
    private var lsMenu: ArrayList<ExerciseDetailModel>,
    private val rclClickListener: RecyclerViewClickListener
): RecyclerView.Adapter<ExerciseDetailAdapterThird.ItemHolder>() {


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
    fun setData(list: ArrayList<ExerciseDetailModel>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model =lsMenu[position]

        holder.binding.apply {
            txtPercent.text = model.percent.toString()
            txtDay.text = model.day
            txtExerciseCount.text = model.exerciseCount
            txtExerciseTime.text = model.exerciseTime
            txtExerciseKcal.text = model.exerciseKcal
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}