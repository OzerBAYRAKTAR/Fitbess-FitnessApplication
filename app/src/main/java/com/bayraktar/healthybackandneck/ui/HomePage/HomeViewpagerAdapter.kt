package com.bayraktar.healthybackandneck.ui.HomePage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem
import com.bayraktar.healthybackandneck.databinding.ItemHomeListBinding
import com.bayraktar.healthybackandneck.utils.OnFavouriteButtonClickListener

class HomeViewpagerAdapter(
    private val context: Context,
    private val onboardList: List<HomeItem>,
    val viewPager2: ViewPager2,
    val onItemclick: (Int) -> Unit,
) :
    RecyclerView.Adapter<HomeViewpagerAdapter.OnBoardingsItemAdapter>() {


    inner class OnBoardingsItemAdapter(val binding: ItemHomeListBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingsItemAdapter {
        val itemBinding =
            ItemHomeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingsItemAdapter(itemBinding)
    }

    override fun getItemCount(): Int {
        return onboardList.size
    }

    override fun onBindViewHolder(holder: OnBoardingsItemAdapter, position: Int) {
        val model = onboardList[position]

        holder.binding.apply {
            imageMain.setImageResource(model.imageMain)
            labelTitle.text = model.desc
            progressHome.progress = model.progress
            txtTitle.text = model.title

            newFavouriteBtn.setOnClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemclick(position)
                }
            }

            if (position == 3) {
                newFavouriteBtn.text = context.getString(R.string.lbl_my_favourite)
            } else {
                newFavouriteBtn.text = context.getString(R.string.lbl_start)
            }

        }
    }

}