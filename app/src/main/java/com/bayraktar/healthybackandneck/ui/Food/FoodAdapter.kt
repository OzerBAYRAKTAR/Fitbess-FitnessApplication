package com.bayraktar.healthybackandneck.ui.Food

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters.FreshFragment
import com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters.FruitFragment
import com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters.LegumesFragment
import com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters.MeetFragment
import com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters.MilkProductsFragment

class FoodAdapter(
    fm: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> return MeetFragment()

            1 -> return MilkProductsFragment()

            2 -> return FreshFragment()

            3 -> return FruitFragment()

            4 -> return LegumesFragment()

            // 5 -> {
            //     return MeetFragment()
            // }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }

    }
}
