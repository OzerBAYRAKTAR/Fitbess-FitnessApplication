package com.bayraktar.healthybackandneck.ui.HomePage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.data.Models.Exercise.AbsModel
import com.bayraktar.healthybackandneck.data.Models.Exercise.ArmModel
import com.bayraktar.healthybackandneck.data.Models.Exercise.FixPostureModel
import com.bayraktar.healthybackandneck.data.Models.Exercise.LegButtModel
import com.bayraktar.healthybackandneck.data.Models.Exercise.NeckBackModel
import com.bayraktar.healthybackandneck.data.Models.Exercise.WarmUpModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.CountModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentHomePageBinding
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.AbsAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.ArmAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.FixPostureAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.LegButtAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.NeckBackAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.WarmUpAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@AndroidEntryPoint
class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var mAdView: AdView

    private lateinit var homeAdapter: HomeViewpagerAdapter

    private var warmupList = ArrayList<WarmUpModel>()
    private var warmAdapter: WarmUpAdapter? = null


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

    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private val dayModel : ExerciseDay ? = null
    private val subExerciseDayExercise = mutableListOf<ExerciseDayExercise>()
    var exerciseLevel = ""


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


        //banner  ID => ca-app-pub-4754194669476617/5943389951
        //banner testt ID => ca-app-pub-3940256099942544/9257395921

        //interstealler tes ID => ca-app-pub-3940256099942544/1033173712

        MobileAds.initialize(requireContext()) {}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        setOnBoardingItems()
        setCurrentIndicator(0)
        setUpIndicators()
        calendarOfWeek()

        observeSubExercises()
        backstack()
        viewModel.fetchExerciseDayExercises()

        viewModel.getCountFromRoom()
        observeCount()
        warmUpViewPager()
        // fixPostureViewPager()
        backNeckViewPager()
        armViewPager()
        absViewPager()
        legViewPager()


        goDietDetail()
        goBreath()
        setUpTransfer()

    }

    private fun observeCount() = with(binding) {
        viewModel.getCount.observe(viewLifecycleOwner, Observer { count ->
            if (count != null) {
                if (count.count > 0) {
                    println("dolu")
                }
            }else {
                val id =CountModel(1,0)
                viewModel.insertCounTable(id)
            }

        })
    }

    private fun setOnBoardingItems() = with(binding) {
        homeAdapter = HomeViewpagerAdapter(
            requireContext(),
            listOf(
                HomeItem(
                    imageMain = R.drawable.new1_removed,
                    title = getString(R.string.hometitle1),
                    desc = getString(R.string.easy_desc),
                    progress = (2 / 100 * 28),
                    dayOfProgram = 2
                ),
                HomeItem(
                    imageMain = R.drawable.new_3,
                    title = getString(R.string.hometitle2),
                    desc = getString(R.string.middle_desc),
                    progress = (6 * 100 / 28),
                    dayOfProgram = 6
                ),
                HomeItem(
                    imageMain = R.drawable.new5_removed,
                    title = getString(R.string.hometitle3),
                    desc = getString(R.string.hard_desc),
                    progress = (12 * 100 / 28),
                    dayOfProgram = 12
                ),
                HomeItem(
                    imageMain = R.drawable.removed4,
                    title = getString(R.string.my_fav_list),
                    desc = getString(R.string.create_plan_desc),
                    progress = (15 * 100 / 28),
                    dayOfProgram = 12
                )
            ), pageerHome
        ) { position ->
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

                3 -> {
                    val action =
                        HomePageFragmentDirections.actionIdHomepageFragmentToMoveListFragment()
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
                it.layoutParams.width =
                    resources.getDimensionPixelSize(R.dimen.indicator_active_width) // Set the width here
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
            warmAdapter = WarmUpAdapter(
                listOf(
                    WarmUpModel(
                        id = 1,
                        title = getString(R.string.warmup_desc1),
                        imageWarmUp = R.drawable.warmup1

                    ),
                    WarmUpModel(
                        id = 2,
                        title = getString(R.string.warmup_desc2),
                        imageWarmUp = R.drawable.isinma2
                    ),

                    WarmUpModel(
                        id = 3,
                        title = getString(R.string.warmup_desc3),
                        imageWarmUp = R.drawable.isinma1
                    ),
                ), warmUpPager
            )
            { position ->
                when (position) {
                    0 -> {
                        val titleName = "stretch"
                        exerciseLevel = getString(R.string.warmup_desc1)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 1)
                        observeListWithLevelAndTitle()
                    }

                    1 -> {
                        val titleName = "stretch"
                        exerciseLevel = getString(R.string.warmup_desc2)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 2)
                        observeListWithLevelAndTitle()

                    }

                    2 -> {
                        val titleName = "stretch"
                        exerciseLevel = getString(R.string.warmup_desc3)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 3)
                        observeListWithLevelAndTitle()
                    }

                    else -> {
                        // Handle other positions or provide a default behavior
                    }
                }
            }

            warmUpPager.adapter = warmAdapter
            warmUpPager.clipChildren = false
            warmUpPager.clipToPadding = false
            warmUpPager.offscreenPageLimit = 2


            warmUpPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compost = CompositePageTransformer()
            compost.addTransformer(MarginPageTransformer(20))
            compost.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.95f + r * 0.15f
            }
            warmUpPager.setPageTransformer(compost)

        }
    }

    private fun backNeckViewPager() = with(binding) {
        binding.apply {
            neckAdapter = NeckBackAdapter(
                listOf(
                    NeckBackModel(
                        id = 1,
                        title = getString(R.string.back_desc1),
                        imageNeckBack = R.drawable.back2
                    ),
                    NeckBackModel(
                        id = 2,
                        title = getString(R.string.back_desc2),
                        imageNeckBack = R.drawable.back1
                    ),
                    NeckBackModel(
                        id = 3,
                        title = getString(R.string.neck_desc1),
                        imageNeckBack = R.drawable.neck1
                    ),
                    NeckBackModel(
                        id = 4,
                        title = getString(R.string.neck_desc2),
                        imageNeckBack = R.drawable.neck2
                    ),
                ), neckBackViewPager
            )
            { position ->
                when (position) {
                    0 -> {
                        val titleName = "bellyneck"
                        exerciseLevel = getString(R.string.back_desc1)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 1)
                        observeListWithLevelAndTitle()
                    }

                    1 -> {
                        val titleName = "bellyneck"
                        exerciseLevel = getString(R.string.back_desc2)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 2)
                        observeListWithLevelAndTitle()

                    }

                    2 -> {
                        val titleName = "bellyneck"
                        exerciseLevel = getString(R.string.neck_desc1)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 3)
                        observeListWithLevelAndTitle()
                    }

                    3 -> {
                        val titleName = "bellyneck"
                        exerciseLevel = getString(R.string.neck_desc2)
                        viewModel.getExerciseListWithLevelAndTitle(titleName, 4)
                        observeListWithLevelAndTitle()
                    }

                    else -> {
                        // Handle other positions or provide a default behavior
                    }
                }
            }
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
    }

    private fun armViewPager() = with(binding) {
        armAdapter = ArmAdapter(
            listOf(
                ArmModel(
                    id = 1,
                    title = getString(R.string.arm_desc1),
                    imageArm = R.drawable.kol11
                ),

                ArmModel(
                    id = 2,
                    title = getString(R.string.arm_desc2),
                    imageArm = R.drawable.kol2
                ),

                ArmModel(
                    id = 3,
                    title = getString(R.string.arm_desc3),
                    imageArm = R.drawable.kol4
                ),
            ), armViewPager
        )
        { position ->
            when (position) {
                0 -> {
                    val titleName = "arm"
                    exerciseLevel = getString(R.string.arm_desc1)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 1)
                    observeListWithLevelAndTitle()
                }

                1 -> {
                    val titleName = "arm"
                    exerciseLevel = getString(R.string.arm_desc2)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 2)
                    observeListWithLevelAndTitle()

                }

                2 -> {
                    val titleName = "arm"
                    exerciseLevel = getString(R.string.arm_desc3)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 3)
                    observeListWithLevelAndTitle()
                }

                else -> {
                    // Handle other positions or provide a default behavior
                }
            }
        }
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
        absAdapter = AbsAdapter(
            listOf(
                AbsModel(
                    id = 1,
                    title = getString(R.string.abs_desc1),
                    imageAbs = R.drawable.abs1
                ),

                AbsModel(
                    id = 2,
                    title = getString(R.string.abs_desc2),
                    imageAbs = R.drawable.abs2
                ),
                AbsModel(
                    id = 3,
                    title = getString(R.string.abs_desc3),
                    imageAbs = R.drawable.abs3
                ),
            ), absViewPager
        )
        { position ->
            when (position) {
                0 -> {
                    val titleName = "abs"
                    exerciseLevel = getString(R.string.abs_desc1)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 1)
                    observeListWithLevelAndTitle()
                }

                1 -> {
                    val titleName = "abs"
                    exerciseLevel = getString(R.string.abs_desc2)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 2)
                    observeListWithLevelAndTitle()
                }

                2 -> {
                    val titleName = "abs"
                    exerciseLevel = getString(R.string.abs_desc3)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 3)
                    observeListWithLevelAndTitle()
                }

                else -> {
                    // Handle other positions or provide a default behavior
                }
            }
        }

        absViewPager.adapter = absAdapter

        absViewPager.clipChildren = false
        absViewPager.clipToPadding = false
        absViewPager.offscreenPageLimit = 2
        absViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compost = CompositePageTransformer()
        compost.addTransformer(MarginPageTransformer(20))
        compost.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.95f + r * 0.15f
        }
        absViewPager.setPageTransformer(compost)
    }

    private fun legViewPager() = with(binding) {
        legAdapter = LegButtAdapter(
            listOf(
                LegButtModel(
                    id = 1,
                    title = getString(R.string.leg_desc1),
                    imageLeg = R.drawable.bacak2
                ),
                LegButtModel(
                    id = 2,
                    title = getString(R.string.leg_desc2),
                    imageLeg = R.drawable.popo2
                ),
                LegButtModel(
                    id = 3,
                    title = getString(R.string.leg_desc3),
                    imageLeg = R.drawable.popo1
                ),
                LegButtModel(
                    id = 4,
                    title = getString(R.string.leg_desc4),
                    imageLeg = R.drawable.pop2
                ),
            ), legViewPager
        )
        { position ->
            when (position) {
                0 -> {
                    val titleName = "legbutt"
                    exerciseLevel = getString(R.string.leg_desc1)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 1)
                    observeListWithLevelAndTitle()
                }

                1 -> {
                    val titleName = "legbutt"
                    exerciseLevel = getString(R.string.leg_desc2)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 2)
                    observeListWithLevelAndTitle()
                }

                2 -> {
                    val titleName = "legbutt"
                    exerciseLevel = getString(R.string.leg_desc3)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 3)
                    observeListWithLevelAndTitle()
                }

                3 -> {
                    val titleName = "legbutt"
                    exerciseLevel = getString(R.string.leg_desc4)
                    viewModel.getExerciseListWithLevelAndTitle(titleName, 4)
                    observeListWithLevelAndTitle()
                }

                else -> {
                    // Handle other positions or provide a default behavior
                }
            }
        }

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

    private fun observeSubExercises() {
        viewModel.exerciseDayExercises.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExercises()
            } else {
                println("liste var")
            }
        })
    }

    private fun observeListWithLevelAndTitle() {
        viewModel.getExerciseListWithLevelAndTitle.observe(
            viewLifecycleOwner,
            Observer { exercise ->
                if (exercise != null) {
                    exerciseListSend.clear()
                    exerciseListSend.addAll(exercise)
                }
                val action =
                    HomePageFragmentDirections.actionIdHomepageFragmentToDetailDayFragment(
                        exerciseListSend.toTypedArray(), dayModel, exerciseLevel
                    )
                view?.findNavController()?.navigate(action)
            })
    }

    @SuppressLint("DiscouragedApi")
    private fun addToRoomExercises() {
        try {
            val inputStream = context?.assets?.open("subExercises.json")
            val size = inputStream?.available() ?: 0
            val buffer = ByteArray(size)
            inputStream?.read(buffer)
            inputStream?.close()
            val jsonString = String(buffer, Charset.defaultCharset())

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val exerciseDescriptionId = resources.getIdentifier(
                    item.optString("exerciseDescription"),
                    "string",
                    requireContext().packageName
                )
                val exerciseDescription = if (exerciseDescriptionId != 0) {
                    getString(exerciseDescriptionId)
                } else {
                    getString(R.string.not_found)
                }
                val exercise = ExerciseDayExercise(
                    id = item.optInt("id"),
                    exerciseId = item.optInt("exerciseId"),
                    titleName = item.optString("titleName"),
                    exerciseName = item.optString("exerciseName"),
                    exerciseDescription = exerciseDescription,
                    image = resources.getIdentifier(
                        item.optString("image"),
                        "drawable",
                        requireContext().packageName
                    ),
                    level = item.optInt("level"),
                    step = 0,
                    dayId = 0,
                    isExerciseCompleted = false
                )
                subExerciseDayExercise.add(exercise)

            }
            viewModel.insertExerciseDayExercise(subExerciseDayExercise)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.attention))
                    .setMessage(getString(R.string.alertmessage))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        requireActivity().finishAffinity()
                    }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
}