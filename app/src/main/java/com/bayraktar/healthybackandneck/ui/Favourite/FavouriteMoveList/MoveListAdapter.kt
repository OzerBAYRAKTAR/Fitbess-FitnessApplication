package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.ItemSubdetaildayBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.Interfaces.OnDeleteClicked

class MoveListAdapter(
    var lsMenu: MutableList<ExerciseDayExercise>,
    private val rclClickListener: RecyclerViewClickListener,
    private val onDeleteClicked: OnDeleteClicked,
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
            val position = adapterPosition
            if (p0 != null && position != RecyclerView.NO_POSITION) {
                mRecyclerViewClickListener.recyclerviewListClicked(p0, position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            ItemSubdetaildayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding, rclClickListener)
    }

    //update old list with new list
    fun setData(list: List<ExerciseDayExercise>) {
        this.lsMenu.clear()
        this.lsMenu.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model = lsMenu[position]

        holder.binding.apply {
            val imageDrawable = ContextCompat.getDrawable(root.context, model.image)
            roundedImageView.setImageDrawable(imageDrawable)
            labelDay.text = model.exerciseName

            deleteBtn.setOnClickListener {
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onDeleteClicked.onDeleteClicked(currentPosition)
                }
            }
        }

    }

    override fun getItemCount(): Int = lsMenu.size
}
