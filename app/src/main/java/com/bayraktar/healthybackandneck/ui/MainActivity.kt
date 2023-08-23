package com.bayraktar.healthybackandneck.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Adapters.OnBoardingItemsAdapter
import com.bayraktar.healthybackandneck.Models.OnBoardingItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var boardAdapter: OnBoardingItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnBoardingItems()
        setCurrentIndicator(0)
        setUpIndicators()

    }

    private fun setOnBoardingItems() = with(binding) {
        boardAdapter = OnBoardingItemsAdapter(
            listOf(
                OnBoardingItems(
                    onBoardingImage = R.drawable.v11,
                    description = getString(R.string.desc1)
                ),
                OnBoardingItems(
                    onBoardingImage = R.drawable.v145,
                    description = getString(R.string.desc2)
                ),
                OnBoardingItems(
                    onBoardingImage = R.drawable.v7,
                    description = getString(R.string.desc3)
                ),
            )
        )
        slideViewPager.adapter = boardAdapter
        slideViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (slideViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        imageNext.setOnClickListener {
            if (slideViewPager.currentItem + 1 < boardAdapter.itemCount) {
                slideViewPager.currentItem += 1
            }else {
                navigateToHomeActivity()
            }
            skip.setOnClickListener {
                navigateToHomeActivity()
            }
            getStart.setOnClickListener {
                navigateToHomeActivity()
            }
        }
    }
    private fun navigateToHomeActivity(){
        startActivity(Intent(this,HomeActivity::class.java))
    }

    private fun setUpIndicators() = with(binding) {
        val indicators = arrayOfNulls<ImageView>(boardAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(15, 0, 15, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_inactive_background
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
                        applicationContext, R.drawable.indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }
}