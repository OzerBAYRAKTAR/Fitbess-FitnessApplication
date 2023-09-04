package com.bayraktar.healthybackandneck.ui.Exercise

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Models.WarmUpModel
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseBinding
import kotlin.math.abs


class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    val binding get() = _binding!!

    private var warmupList = ArrayList<WarmUpModel>()
    private lateinit var warmAdapter: ExerciseAdapter
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


        binding.apply {


            warmAdapter = ExerciseAdapter(warmupList, warmUpPager)


            warmupList.add(
                WarmUpModel(
                    title = getString(R.string.warmup_desc1),
                    imageWarmUp = R.drawable.isinma1
                )
            )
            warmupList.add(
                WarmUpModel(
                    title = getString(R.string.warmup_desc2),
                    imageWarmUp = R.drawable.isinma2
                )
            )
            warmupList.add(
                WarmUpModel(
                    title = getString(R.string.warmup_desc3),
                    imageWarmUp = R.drawable.isinma3
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
            warmUpPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                }
            })
        }
    }


}