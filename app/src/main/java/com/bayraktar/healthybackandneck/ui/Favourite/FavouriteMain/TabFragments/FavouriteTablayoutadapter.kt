package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.ItemFavouritelistBinding
import com.bayraktar.healthybackandneck.utils.OnFavouriteButtonClickListener
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class FavouriteTablayoutadapter(
    private var lsMenu: List<SubExerciseDayExercise>,
    private val rclClickListener: RecyclerViewClickListener,
    private val onFavouriteButtonClickListener: OnFavouriteButtonClickListener
): RecyclerView.Adapter<FavouriteTablayoutadapter.ItemHolder>() {


    inner class ItemHolder(val binding: ItemFavouritelistBinding, private val recyclerViewClickListener: RecyclerViewClickListener): RecyclerView.ViewHolder(binding.root)
        , View.OnClickListener {

        private val mRecyclerViewClickListener: RecyclerViewClickListener = recyclerViewClickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mRecyclerViewClickListener.recyclerviewListClicked(p0!!,adapterPosition)
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedItem = lsMenu[position]
                clickedItem.isFavourite = !clickedItem.isFavourite

                binding.favoritebtn.isChecked = clickedItem.isFavourite

                notifyItemChanged(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemFavouritelistBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(itemBinding,rclClickListener)
    }

    fun setData(list: List<SubExerciseDayExercise>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model =lsMenu[position]

        holder.binding.apply {

            favoritebtn.isClickable = false
            favoritebtn.isChecked = model.isFavourite

            val imageDrawable = ContextCompat.getDrawable(root.context, model.image)
            roundedImageView.setImageDrawable(imageDrawable)
            labelDay.text = model.exerciseName

            infoBtn.setOnClickListener {
                onFavouriteButtonClickListener.onButtonClicked(position)
            }

        }
    }

    override fun getItemCount(): Int = lsMenu.size
}