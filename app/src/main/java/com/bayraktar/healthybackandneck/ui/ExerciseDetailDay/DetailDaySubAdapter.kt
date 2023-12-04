package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.ItemDetaildayBinding
import com.bayraktar.healthybackandneck.databinding.ItemSubdetaildayBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class DetailDaySubAdapter(
    private var lsMenu: List<SubExerciseDayExercise>,
): RecyclerView.Adapter<DetailDaySubAdapter.ItemHolder>() {


    inner class ItemHolder(val binding: ItemSubdetaildayBinding): RecyclerView.ViewHolder(binding.root)
        {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemSubdetaildayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(itemBinding)
    }
    //update old list with new list
    fun setData(list: List<SubExerciseDayExercise>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model =lsMenu[position]

        holder.binding.apply {
            val imageDrawable = ContextCompat.getDrawable(root.context, model.image)
            roundedImageView.setImageDrawable(imageDrawable)
            labelDay.text = model.exerciseName
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}