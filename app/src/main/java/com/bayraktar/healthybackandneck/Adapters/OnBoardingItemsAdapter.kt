package com.bayraktar.healthybackandneck.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.Models.OnBoardingItems
import com.bayraktar.healthybackandneck.databinding.ItemOnboardingContainerBinding

class OnBoardingItemsAdapter(private val onboardList: List<OnBoardingItems>):
RecyclerView.Adapter<OnBoardingItemsAdapter.OnBoardingsItemAdapter>(){


    inner class OnBoardingsItemAdapter(val binding: ItemOnboardingContainerBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingsItemAdapter {
        val itemBinding = ItemOnboardingContainerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OnBoardingsItemAdapter(itemBinding)
    }

    override fun getItemCount(): Int {
       return onboardList.size
    }

    override fun onBindViewHolder(holder: OnBoardingsItemAdapter, position: Int) {
        val model = onboardList[position]

        holder.binding.apply {
            onbardingImageView.setImageResource(model.onBoardingItems)
            txtTitle.text = model.title
            txtDescription.text = model.description
        }
    }
}