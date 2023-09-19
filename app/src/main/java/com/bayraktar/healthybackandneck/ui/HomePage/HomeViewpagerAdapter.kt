package com.bayraktar.healthybackandneck.ui.HomePage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Models.HomeItems
import com.bayraktar.healthybackandneck.Models.OnBoardingItems
import com.bayraktar.healthybackandneck.databinding.ItemHomeListBinding
import com.bayraktar.healthybackandneck.databinding.ItemOnboardingContainerBinding

    class HomeViewpagerAdapter(private val onboardList: List<HomeItems>,val viewPager2: ViewPager2,val onItemclick: (Int) -> Unit):
    RecyclerView.Adapter<HomeViewpagerAdapter.OnBoardingsItemAdapter>(){


        inner class OnBoardingsItemAdapter(val binding: ItemHomeListBinding): RecyclerView.ViewHolder(binding.root){


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingsItemAdapter {
            val itemBinding = ItemHomeListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return OnBoardingsItemAdapter(itemBinding)
        }

        override fun getItemCount(): Int {
            return onboardList.size
        }

        override fun onBindViewHolder(holder: OnBoardingsItemAdapter, position: Int) {
            val model = onboardList[position]

            holder.binding.apply {
                imageMain.setImageResource(model.imageMain)
                eg1.setImageResource(model.energy1)
                eg2.setImageResource(model.energy2)
                eg3.setImageResource(model.energy3)
                progressHome.progress = model.progress
                txtTitle.text = model.title

                holder.itemView.setOnClickListener{
                    val position = holder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemclick(position)
                    }
                }

            }
        }
    }