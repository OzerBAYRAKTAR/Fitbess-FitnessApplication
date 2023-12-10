package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Abs.AbsFragment
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Arm.ArmFragment
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Back.BackFragment
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.LegButt.LegButtFragment
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Strech.StrechFragment


class FavouriteMainAdapter(
    fm: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> AbsFragment()

            1 -> ArmFragment()

            2 -> BackFragment()

            3 -> LegButtFragment()

            4 -> StrechFragment()

            else -> throw IllegalArgumentException("Invalid position: $position")
        }

    }
}