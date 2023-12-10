package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFavouriteMainBinding
import com.bayraktar.healthybackandneck.ui.Food.FoodAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteMainFragment : Fragment() {

    private var _binding: FragmentFavouriteMainBinding? = null
    val binding get() = _binding!!
    private lateinit var viewPagerAdapter: FavouriteMainAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayoutViewPager()
    }

    @SuppressLint("MissingInflatedId")
    private fun tabLayoutViewPager() {


        val viewPager = binding.viewP
        val tabLayout = binding.tabLayout


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewP.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.viewP.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

        val tabAdapter =
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                val customTabView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tablayoutfavourite,null)
                val tabText: TextView = customTabView.findViewById(R.id.tab_text_favourite)

                when (position) {
                    0 -> {
                        tabText.text = (getString(R.string.abs))
                    }

                    1 -> {
                        tabText.text = (getString(R.string.arm))
                    }

                    2 -> {
                        tabText.text = (getString(R.string.back_back))
                    }

                    3 -> {
                        tabText.text = (getString(R.string.leg))
                    }
                    4 -> {
                        tabText.text = (getString(R.string.strech))
                    }
                }
                tab.customView = customTabView
            }
        viewPagerAdapter = FavouriteMainAdapter(childFragmentManager, lifecycle)

        binding.viewP.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager, tabAdapter).attach()

    }

}