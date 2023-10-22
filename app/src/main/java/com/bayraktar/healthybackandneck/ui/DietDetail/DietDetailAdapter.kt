package com.bayraktar.healthybackandneck.ui.DietDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.data.Models.DietItems
import com.bayraktar.healthybackandneck.databinding.ItemDietDetailBinding

class DietDetailAdapter(private val onboardList: List<DietItems>, val viewPager2: ViewPager2):
    RecyclerView.Adapter<DietDetailAdapter.OnBoardingsItemAdapter>(){


    inner class OnBoardingsItemAdapter(val binding: ItemDietDetailBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingsItemAdapter {
        val itemBinding = ItemDietDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OnBoardingsItemAdapter(itemBinding)
    }

    override fun getItemCount(): Int {
        return onboardList.size
    }

    override fun onBindViewHolder(holder: OnBoardingsItemAdapter, position: Int) {
        val model = onboardList[position]

        holder.binding.apply {
            imageDiet.setImageResource(model.imageDetail)
            labelTitle.text = model.title
            txtDesc.text = model.description
        }
    }
}