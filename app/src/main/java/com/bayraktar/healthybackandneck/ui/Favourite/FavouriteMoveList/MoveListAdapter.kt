package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.ItemSubdetaildayBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class MoveListAdapter(
    private var lsMenu: List<SubExerciseDayExercise>,
    private val rclClickListener: RecyclerViewClickListener,
) : RecyclerView.Adapter<MoveListAdapter.ItemHolder>() {


    inner class ItemHolder(
        val binding: ItemSubdetaildayBinding,
        private val recyclerViewClickListener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val mRecyclerViewClickListener: RecyclerViewClickListener =
            recyclerViewClickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mRecyclerViewClickListener.recyclerviewListClicked(p0!!, adapterPosition)
            val position = adapterPosition

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            ItemSubdetaildayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding, rclClickListener)
    }

    //update old list with new list
    fun setData(list: List<SubExerciseDayExercise>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model = lsMenu[position]

        holder.binding.apply {
            val imageDrawable = ContextCompat.getDrawable(root.context, model.image)
            roundedImageView.setImageDrawable(imageDrawable)
            labelDay.text = model.exerciseName
        }
    }

    override fun getItemCount(): Int = lsMenu.size
}