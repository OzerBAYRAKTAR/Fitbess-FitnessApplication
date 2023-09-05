package com.bayraktar.healthybackandneck.ui.Exercise.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Models.Exercise.FixPostureModel
import com.bayraktar.healthybackandneck.Models.Exercise.NeckBackModel
import com.bayraktar.healthybackandneck.databinding.SliderLayoutBinding

class NeckBackAdapter(private val neckList: List<NeckBackModel>, val viewPager2: ViewPager2):
    RecyclerView.Adapter<NeckBackAdapter.OnBoardingsItemAdapter>(){


    inner class OnBoardingsItemAdapter(val binding: SliderLayoutBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingsItemAdapter {
        val itemBinding = SliderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OnBoardingsItemAdapter(itemBinding)
    }

    override fun onBindViewHolder(holder: OnBoardingsItemAdapter, position: Int) {
        val model = neckList[position]

        holder.binding.apply {
            sliderImage.setImageResource(model.imageNeckBack)
            titleWarmUp.text = model.title
        }
    }

    override fun getItemCount(): Int {
        return neckList.size
    }


}