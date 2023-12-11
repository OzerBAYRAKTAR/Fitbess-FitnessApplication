package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.ItemDetaildayBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class DetailDayAdapter(
    private var lsMenu: List<ExerciseDayExercise>,
    private val rclClickListener: RecyclerViewClickListener
): RecyclerView.Adapter<DetailDayAdapter.ItemHolder>() {


    inner class ItemHolder(val binding: ItemDetaildayBinding, recyclerViewClickListener: RecyclerViewClickListener): RecyclerView.ViewHolder(binding.root)
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
        val itemBinding = ItemDetaildayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(itemBinding,rclClickListener)
    }
    //update old list with new list
    fun setData(list: List<ExerciseDayExercise>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model =lsMenu[position]

        holder.binding.apply {
            val imageDrawable = try {
                ContextCompat.getDrawable(root.context, model.image)
            } catch (e: Resources.NotFoundException) {
                ContextCompat.getDrawable(root.context, R.drawable.placehlder)
            }
            roundedImageView.setImageDrawable(imageDrawable)
            labelDay.text = model.exerciseName
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}