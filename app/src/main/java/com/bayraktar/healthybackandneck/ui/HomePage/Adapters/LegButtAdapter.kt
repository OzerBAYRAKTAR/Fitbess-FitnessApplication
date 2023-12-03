package com.bayraktar.healthybackandneck.ui.HomePage.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.data.Models.Exercise.LegButtModel
import com.bayraktar.healthybackandneck.data.Models.Exercise.NeckBackModel
import com.bayraktar.healthybackandneck.databinding.SliderLayoutBinding

class LegButtAdapter(private val legList: List<LegButtModel>, val viewPager2: ViewPager2,private val onItemClick: (Int) -> Unit):
    RecyclerView.Adapter<LegButtAdapter.OnBoardingsItemAdapter>(){


    inner class OnBoardingsItemAdapter(val binding: SliderLayoutBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingsItemAdapter {
        val itemBinding = SliderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OnBoardingsItemAdapter(itemBinding)
    }

    override fun onBindViewHolder(holder: OnBoardingsItemAdapter, position: Int) {
        val model = legList[position]

        holder.binding.apply {
            sliderImage.setImageResource(model.imageLeg)
            titleWarmUp.text = model.title
        }

        holder.itemView.setOnClickListener{
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return legList.size
    }


}