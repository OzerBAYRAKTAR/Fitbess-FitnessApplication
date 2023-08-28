package com.bayraktar.healthybackandneck.ui.HomePage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Adapters.OnBoardingItemsAdapter
import com.bayraktar.healthybackandneck.Models.HomeItems
import com.bayraktar.healthybackandneck.Models.OnBoardingItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentHomePageBinding
import com.bayraktar.healthybackandneck.ui.HomeActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    val binding get() = _binding!!
    private lateinit var homeAdapter: HomeViewpagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBoardingItems()
        setCurrentIndicator(0)
        setUpIndicators()
        calendarOfWeek()
    }

    private fun setOnBoardingItems() = with(binding) {
        homeAdapter = HomeViewpagerAdapter(
            listOf(
                HomeItems(
                    imageMain = R.drawable.new1_removed,
                    title = getString(R.string.hometitle1),
                    energy1 = R.drawable.energy,
                    energy2 = R.drawable.energy_empty,
                    energy3 = R.drawable.energy_empty,
                    progress = (2 / 100 * 28),
                    dayOfProgram = 2
                ),
                HomeItems(
                    imageMain = R.drawable.new_3,
                    title = getString(R.string.hometitle2),
                    energy1 = R.drawable.energy,
                    energy2 = R.drawable.energy,
                    energy3 = R.drawable.energy_empty,
                    progress = (6 * 100 / 28),
                    dayOfProgram = 6
                ),
                HomeItems(
                    imageMain = R.drawable.new5_removed,
                    title = getString(R.string.hometitle3),
                    energy1 = R.drawable.energy,
                    energy2 = R.drawable.energy,
                    energy3 = R.drawable.energy,
                    progress = (12 * 100 / 28),
                    dayOfProgram = 12
                ),
                HomeItems(
                    imageMain = R.drawable.new5_removed,
                    title = getString(R.string.hometitle4),
                    energy1 = R.drawable.energy,
                    energy2 = R.drawable.energy_empty,
                    energy3 = R.drawable.energy_empty,
                    progress = 0,
                    dayOfProgram = 0
                ),

                )
        )
        pageerHome.adapter = homeAdapter
        pageerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (pageerHome.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    private fun setUpIndicators() = with(binding) {
        val indicators = arrayOfNulls<ImageView>(homeAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT

            )
        layoutParams.setMargins(15, 0, 15, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_inactive_home
                    )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) = with(binding) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_active_home
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_inactive_home
                    )
                )
            }
        }
    }

    private fun calendarOfWeek() = with(binding) {
        // Get the current day of the month
        val calendar = Calendar.getInstance()
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK) // 1 (Sunday) to 7 (Saturday)

        // Set the current month and year
        val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
        binding.monthYearTV.text = currentMonthYear

        // Calculate the starting day of the week (Sunday) of the current week
        val daysToSubtract = currentWeekDay - Calendar.SUNDAY
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract)

        // Set background based on the current day and week
        val days = listOf(
            binding.buttonSunday,
            binding.buttonMonday,
            binding.buttonTuesday,
            binding.buttonWednesday,
            binding.buttonThursday,
            binding.buttonFriday,
            binding.buttonSaturday
        )

        for ((index, button) in days.withIndex()) {
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val isCurrentMonth =
                calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)

            val backgroundDrawable = if (isCurrentMonth) {
                if (dayOfMonth == currentDayOfMonth) {
                    R.drawable.calender_active
                    //ContextCompat.getColor(requireContext(), R.color.orange)
                    //ContextCompat.getDrawable(requireContext(),R.drawable.calender_active)
                } else if (dayOfMonth > currentDayOfMonth) {
                    R.drawable.calender_circle
                    //ContextCompat.getColor(requireContext(), R.color.gray)
                    //ContextCompat.getDrawable(requireContext(),R.drawable.calender_circle)
                } else {
                    R.drawable.calender_circle
                    //ContextCompat.getColor(requireContext(), R.color.gray)
                    //ContextCompat.getDrawable(requireContext(),R.drawable.calender_circle)
                }
            } else {
                R.drawable.calender_circle
                //ContextCompat.getColor(requireContext(), R.color.gray)
                //ContextCompat.getDrawable(requireContext(),R.drawable.calender_circle)
            }

            button.text = "$dayOfMonth"
            button.setBackgroundResource(backgroundDrawable)
            calendar.add(Calendar.DAY_OF_MONTH, 1)

            // Reset calendar to the next Sunday to start the next week
            if (index == 6) {
                val daysToAdd = Calendar.SATURDAY - currentWeekDay + 2
                calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
            }
        }
    }


}