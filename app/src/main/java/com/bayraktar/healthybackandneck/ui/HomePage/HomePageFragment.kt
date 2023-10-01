package com.bayraktar.healthybackandneck.ui.HomePage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Adapters.OnBoardingItemsAdapter
import com.bayraktar.healthybackandneck.Models.Exercise.AbsModel
import com.bayraktar.healthybackandneck.Models.Exercise.ArmModel
import com.bayraktar.healthybackandneck.Models.Exercise.FixPostureModel
import com.bayraktar.healthybackandneck.Models.Exercise.LegButtModel
import com.bayraktar.healthybackandneck.Models.Exercise.NeckBackModel
import com.bayraktar.healthybackandneck.Models.Exercise.WarmUpModel
import com.bayraktar.healthybackandneck.Models.HomeItems
import com.bayraktar.healthybackandneck.Models.OnBoardingItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentHomePageBinding
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.AbsAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.ArmAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.FixPostureAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.LegButtAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.NeckBackAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.WarmUpAdapter
import com.bayraktar.healthybackandneck.ui.HomeActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    val binding get() = _binding!!
    private lateinit var homeAdapter: HomeViewpagerAdapter

    private var warmupList = ArrayList<WarmUpModel>()
    private lateinit var warmAdapter: WarmUpAdapter

    //FixPosture
    private var postureList = ArrayList<FixPostureModel>()
    private lateinit var postureAdapter: FixPostureAdapter

    //BackNeck
    private var neckList = ArrayList<NeckBackModel>()
    private lateinit var neckAdapter: NeckBackAdapter

    //BackNeck
    private var armList = ArrayList<ArmModel>()
    private lateinit var armAdapter: ArmAdapter

    //Abs
    private var absList = ArrayList<AbsModel>()
    private lateinit var absAdapter: AbsAdapter

    //LegButt
    private var legList = ArrayList<LegButtModel>()
    private lateinit var legAdapter: LegButtAdapter


    val sliderHandler = Handler()


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


        warmUpViewPager()
        fixPostureViewPager()
        backNeckViewPager()
        armViewPager()
        absViewPager()
        legViewPager()


        goDietDetail()
        goBreath()
        setUpTransfer()


    }

    private fun setOnBoardingItems() = with(binding) {
        homeAdapter = HomeViewpagerAdapter(
            listOf(
                HomeItems(
                    imageMain = R.drawable.new1_removed,
                    title = getString(R.string.hometitle1),
                    desc = getString(R.string.easy_desc),
                    progress = (2 / 100 * 28),
                    dayOfProgram = 2
                ),
                HomeItems(
                    imageMain = R.drawable.new_3,
                    title = getString(R.string.hometitle2),
                    desc = getString(R.string.middle_desc),
                    progress = (6 * 100 / 28),
                    dayOfProgram = 6
                ),
                HomeItems(
                    imageMain = R.drawable.new5_removed,
                    title = getString(R.string.hometitle3),
                    desc = getString(R.string.hard_desc),
                    progress = (12 * 100 / 28),
                    dayOfProgram = 12
                ),
                HomeItems(
                    imageMain = R.drawable.removed4,
                    title = getString(R.string.create_plan),
                    desc = getString(R.string.create_plan_desc),
                    progress = (15 * 100 / 28),
                    dayOfProgram = 12
                ),
            ), pageerHome
        ) { position ->
            // Handle item click based on the position
            when (position) {
                0 -> {
                    // Navigate to Fragment 1
                    val action =
                        HomePageFragmentDirections.actionIdHomepageFragmentToExerciseDetailFirstFragment()
                    findNavController().navigate(action)
                }

                1 -> {
                    // Navigate to Fragment 2
                    val action =
                        HomePageFragmentDirections.actionIdHomepageFragmentToExerciseDetailSecondFragment()
                    findNavController().navigate(action)
                }

                2 -> {
                    // Navigate to Fragment 3
                    val action =
                        HomePageFragmentDirections.actionIdHomepageFragmentToExerciseDetailThirdFragment()
                    findNavController().navigate(action)
                }

                else -> {
                    // Handle other positions or provide a default behavior
                }
            }
        }
        pageerHome.clipToPadding = false
        pageerHome.clipChildren = false
        pageerHome.offscreenPageLimit = 2
        pageerHome.adapter = homeAdapter

        pageerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (pageerHome.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_ALWAYS

    }

    private fun setUpTransfer() = with(binding) {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.viewpager_margin)
        val transfer = CompositePageTransformer()
        transfer.addTransformer(MarginPageTransformer(pageMarginPx))
        transfer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = r
        }
        pageerHome.setPageTransformer(transfer)
    }

    private fun setUpIndicators() = with(binding) {
        val indicators = arrayOfNulls<ImageView>(homeAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        layoutParams.setMargins(5, 0, 5, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_inactive_home
                    )
                )
                it.layoutParams = layoutParams
                it.layoutParams.width = resources.getDimensionPixelSize(R.dimen.indicator_active_width) // Set the width here
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



    private fun warmUpViewPager() {
        binding.apply {
            warmAdapter = WarmUpAdapter(warmupList, warmUpPager)

            warmupList.add(
                WarmUpModel(
                    id = 1,
                    title = getString(R.string.warmup_desc1),
                    imageWarmUp = R.drawable.warmup1
                )
            )
            warmupList.add(
                WarmUpModel(
                    id = 2,
                    title = getString(R.string.warmup_desc2),
                    imageWarmUp = R.drawable.isinma2
                )
            )
            warmupList.add(
                WarmUpModel(
                    id = 3,
                    title = getString(R.string.warmup_desc3),
                    imageWarmUp = R.drawable.isinma1
                )
            )

            warmUpPager.adapter = warmAdapter

            warmUpPager.clipChildren = false
            warmUpPager.clipToPadding = false
            warmUpPager.offscreenPageLimit = 2
            warmUpPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compost = CompositePageTransformer()
            compost.addTransformer(MarginPageTransformer(20))
            compost.addTransformer(object : ViewPager2.PageTransformer {
                override fun transformPage(page: View, position: Float) {
                    val r = 1 - abs(position)
                    page.scaleY = 0.95f + r * 0.15f

                }

            })
            warmUpPager.setPageTransformer(compost)
        }
    }
    private fun fixPostureViewPager() = with(binding) {
        postureAdapter = FixPostureAdapter(postureList, fixPosturePager)

        postureList.add(
            FixPostureModel(
                id = 1,
                title = getString(R.string.posture_desc1),
                imagePosture = R.drawable.durus1
            )
        )
        postureList.add(
            FixPostureModel(
                id = 2,
                title = getString(R.string.posture_desc2),
                imagePosture = R.drawable.durusduzeltme4
            )
        )
        postureList.add(
            FixPostureModel(
                id = 3,
                title = getString(R.string.posture_desc3),
                imagePosture = R.drawable.durusduzeltme3
            )
        )

        fixPosturePager.adapter = postureAdapter

        fixPosturePager.clipChildren = false
        fixPosturePager.clipToPadding = false
        fixPosturePager.offscreenPageLimit = 2
        fixPosturePager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compost = CompositePageTransformer()
        compost.addTransformer(MarginPageTransformer(20))
        compost.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.95f + r * 0.15f

            }

        })
        fixPosturePager.setPageTransformer(compost)
    }
    private fun backNeckViewPager() = with(binding) {
        neckAdapter = NeckBackAdapter(neckList, neckBackViewPager)

        neckList.add(
            NeckBackModel(
                id = 1,
                title = getString(R.string.back_desc1),
                imageNeckBack = R.drawable.back2
            )
        )
        neckList.add(
            NeckBackModel(
                id = 2,
                title = getString(R.string.back_desc2),
                imageNeckBack = R.drawable.back1
            )
        )
        neckList.add(
            NeckBackModel(
                id = 3,
                title = getString(R.string.neck_desc1),
                imageNeckBack = R.drawable.neck1
            )
        )
        neckList.add(
            NeckBackModel(
                id = 4,
                title = getString(R.string.neck_desc2),
                imageNeckBack = R.drawable.neck2
            )
        )
        neckBackViewPager.adapter = neckAdapter

        neckBackViewPager.clipChildren = false
        neckBackViewPager.clipToPadding = false
        neckBackViewPager.offscreenPageLimit = 2
        neckBackViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compost = CompositePageTransformer()
        compost.addTransformer(MarginPageTransformer(20))
        compost.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.95f + r * 0.15f

            }

        })
        neckBackViewPager.setPageTransformer(compost)
    }
    private fun armViewPager() = with(binding) {
        armAdapter = ArmAdapter(armList, armViewPager)

        armList.add(
            ArmModel(
                id = 1,
                title = getString(R.string.arm_desc1),
                imageArm = R.drawable.kol11
            )
        )
        armList.add(
            ArmModel(
                id = 2,
                title = getString(R.string.arm_desc2),
                imageArm = R.drawable.kol2
            )
        )
        armList.add(
            ArmModel(
                id = 3,
                title = getString(R.string.arm_desc3),
                imageArm = R.drawable.kol4
            )
        )

        armViewPager.adapter = armAdapter

        armViewPager.clipChildren = false
        armViewPager.clipToPadding = false
        armViewPager.offscreenPageLimit = 2
        armViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compost = CompositePageTransformer()
        compost.addTransformer(MarginPageTransformer(20))
        compost.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.95f + r * 0.15f

            }

        })
        armViewPager.setPageTransformer(compost)
    }
    private fun absViewPager() = with(binding) {
        absAdapter = AbsAdapter(absList, absViewPager)

        absList.add(
            AbsModel(
                id = 1,
                title = getString(R.string.abs_desc1),
                imageAbs = R.drawable.abs1
            )
        )
        absList.add(
            AbsModel(
                id = 2,
                title = getString(R.string.abs_desc2),
                imageAbs = R.drawable.abs2
            )
        )
        absList.add(
            AbsModel(
                id = 3,
                title = getString(R.string.abs_desc3),
                imageAbs = R.drawable.abs3
            )
        )

        absViewPager.adapter = absAdapter

        absViewPager.clipChildren = false
        absViewPager.clipToPadding = false
        absViewPager.offscreenPageLimit = 2
        absViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compost = CompositePageTransformer()
        compost.addTransformer(MarginPageTransformer(20))
        compost.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.95f + r * 0.15f

            }

        })
        absViewPager.setPageTransformer(compost)
    }
    private fun legViewPager() = with(binding) {
        legAdapter = LegButtAdapter(legList, legViewPager)

        legList.add(
            LegButtModel(
                id = 1,
                title = getString(R.string.leg_desc1),
                imageLeg = R.drawable.bacak2
            )
        )
        legList.add(
            LegButtModel(
                id = 2,
                title = getString(R.string.leg_desc2),
                imageLeg = R.drawable.popo2
            )
        )
        legList.add(
            LegButtModel(
                id = 3,
                title = getString(R.string.leg_desc3),
                imageLeg = R.drawable.popo1
            )
        )
        legList.add(
            LegButtModel(
                id = 4,
                title = getString(R.string.leg_desc4),
                imageLeg = R.drawable.pop2
            )
        )

        legViewPager.adapter = legAdapter

        legViewPager.clipChildren = false
        legViewPager.clipToPadding = false
        legViewPager.offscreenPageLimit = 2
        legViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compost = CompositePageTransformer()
        compost.addTransformer(MarginPageTransformer(20))
        compost.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.95f + r * 0.15f

            }

        })
        legViewPager.setPageTransformer(compost)
    }


    private fun goDietDetail() {
        binding.constraintDiet.setOnClickListener {
            val action =
                HomePageFragmentDirections.actionIdHomepageFragmentToDietDetailFragment()
            view?.findNavController()?.navigate(action)
        }
    }

    private fun goBreath() {
        binding.constraintNutrition.setOnClickListener {
            val action =
                HomePageFragmentDirections.actionIdHomepageFragmentToBreathFragment()
            view?.findNavController()?.navigate(action)
        }
    }
    private fun calendarOfWeek() = with(binding) {
        // Get the current day of the month
        val calendar = Calendar.getInstance()
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val currentWeekDay =
            calendar.get(Calendar.DAY_OF_WEEK) // 1 (Sunday) to 7 (Saturday)

        // Set the current month and year
        val currentMonthYear =
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
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