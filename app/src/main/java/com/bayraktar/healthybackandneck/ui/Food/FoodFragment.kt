package com.bayraktar.healthybackandneck.ui.Food

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.data.Models.Exercise.AbsModel
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFoodBinding
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.AbsAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs


class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null
    val binding get() = _binding!!
    private lateinit var viewPagerAdapter: FoodAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayoutViewPager()

    }

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
                val customTabView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tablayoutfood,null)
                val tabIcon: ImageView = customTabView.findViewById(R.id.tab_icon)
                val tabText: TextView = customTabView.findViewById(R.id.tab_text)

                when (position) {
                    0 -> {
                        tabIcon.setImageResource(R.drawable.meat)
                        tabText.text = (getString(R.string.meat))
                    }

                    1 -> {
                        tabIcon.setImageResource(R.drawable.milk)
                        tabText.text = (getString(R.string.milkprodct))
                    }

                    2 -> {
                        tabIcon.setImageResource(R.drawable.fresh)
                        tabText.text = (getString(R.string.fresh))
                    }

                    3 -> {
                        tabIcon.setImageResource(R.drawable.fruit)
                        tabText.text = (getString(R.string.fruit))
                    }

                    4 -> {
                        tabIcon.setImageResource(R.drawable.legume)
                        tabText.text = (getString(R.string.legume))
                    }
                    //5 -> {
                    //    tabIcon.setImageResource(R.drawable.other)
                    //    tabText.text = (getString(R.string.other))
                    //}
                    // Add more tabs as needed
                }
                tab.customView = customTabView
            }
        viewPagerAdapter = FoodAdapter(childFragmentManager, lifecycle)

        binding.viewP.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager, tabAdapter).attach()

    }



}