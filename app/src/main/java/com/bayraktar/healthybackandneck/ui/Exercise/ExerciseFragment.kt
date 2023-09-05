package com.bayraktar.healthybackandneck.ui.Exercise

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Models.Exercise.AbsModel
import com.bayraktar.healthybackandneck.Models.Exercise.ArmModel
import com.bayraktar.healthybackandneck.Models.Exercise.FixPostureModel
import com.bayraktar.healthybackandneck.Models.Exercise.LegButtModel
import com.bayraktar.healthybackandneck.Models.Exercise.NeckBackModel
import com.bayraktar.healthybackandneck.Models.Exercise.WarmUpModel
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseBinding
import com.bayraktar.healthybackandneck.ui.Exercise.Adapters.AbsAdapter
import com.bayraktar.healthybackandneck.ui.Exercise.Adapters.ArmAdapter
import com.bayraktar.healthybackandneck.ui.Exercise.Adapters.FixPostureAdapter
import com.bayraktar.healthybackandneck.ui.Exercise.Adapters.LegButtAdapter
import com.bayraktar.healthybackandneck.ui.Exercise.Adapters.NeckBackAdapter
import com.bayraktar.healthybackandneck.ui.Exercise.Adapters.WarmUpAdapter
import kotlin.math.abs


class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    val binding get() = _binding!!

    //WarmUp
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
        _binding = FragmentExerciseBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        warmUpViewPager()
        fixPostureViewPager()
        backNeckViewPager()
        armViewPager()
        absViewPager()
        legViewPager()

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


}