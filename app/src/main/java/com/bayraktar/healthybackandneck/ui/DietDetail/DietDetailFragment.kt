package com.bayraktar.healthybackandneck.ui.DietDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.ui.Adapters.OnBoardingItemsAdapter
import com.bayraktar.healthybackandneck.data.Models.DietItems
import com.bayraktar.healthybackandneck.data.Models.OnBoardingItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentDietDetailBinding
import kotlin.math.abs


class DietDetailFragment : Fragment() {

    private var _binding: FragmentDietDetailBinding? = null
    val binding get() = _binding!!
    private lateinit var dietAdapter: DietDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDietDetailBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDietItems()
        setUpTransfer()



    }

    private fun setUpTransfer() = with(binding) {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.viewpager_margin)
        val transfer = CompositePageTransformer()
        transfer.addTransformer(MarginPageTransformer(pageMarginPx))
        transfer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY =  r
        }
        viewPagerDetail.setPageTransformer(transfer)
    }

    private fun setDietItems() = with(binding) {
        dietAdapter = DietDetailAdapter(
            listOf(
                DietItems(
                    imageDetail = R.drawable.ogun_atlama,
                    description = getString(R.string.diet_desc1),
                    title = getString(R.string.diet_title1)
                ),
                DietItems(
                    imageDetail = R.drawable.water,
                    description = getString(R.string.diet_desc2),
                    title = getString(R.string.diet_title2)
                ),
                DietItems(
                    imageDetail = R.drawable.fastfood,
                    description = getString(R.string.diet_desc3),
                    title = getString(R.string.diet_title3)
                ),
                DietItems(
                    imageDetail = R.drawable.cesitlilik,
                    description = getString(R.string.diet_desc4),
                    title = getString(R.string.diet_title4)
                ),
                DietItems(
                    imageDetail = R.drawable.coffee,
                    description = getString(R.string.diet_desc5),
                    title = getString(R.string.diet_title5)
                ),
                DietItems(
                    imageDetail = R.drawable.calcium,
                    description = getString(R.string.diet_desc6),
                    title = getString(R.string.diet_title6)
                ),
                DietItems(
                    imageDetail = R.drawable.cvitamin,
                    description = getString(R.string.diet_desc7),
                    title = getString(R.string.diet_title7)
                ),
                DietItems(
                    imageDetail = R.drawable.iron,
                    description = getString(R.string.diet_desc8),
                    title = getString(R.string.diet_title8)
                ),
                DietItems(
                    imageDetail = R.drawable.posali,
                    description = getString(R.string.diet_desc9),
                    title = getString(R.string.diet_title9)
                ),
                DietItems(
                    imageDetail = R.drawable.egzersizjpg,
                    description = getString(R.string.diet_desc10),
                    title = getString(R.string.diet_title10)
                ),
                DietItems(
                    imageDetail = R.drawable.keepweigh,
                    description = getString(R.string.diet_desc11),
                    title = getString(R.string.diet_title11)
                ),
                DietItems(
                    imageDetail = R.drawable.transyag,
                    description = getString(R.string.diet_desc12),
                    title = getString(R.string.diet_title12)
                ),
                DietItems(
                    imageDetail = R.drawable.water,
                    description = getString(R.string.diet_desc13),
                    title = getString(R.string.diet_title13)
                ),
                DietItems(
                    imageDetail = R.drawable.meyve,
                    description = getString(R.string.diet_desc14),
                    title = getString(R.string.diet_title14)
                ),
                DietItems(
                    imageDetail = R.drawable.sutyogurt,
                    description = getString(R.string.diet_desc15),
                    title = getString(R.string.diet_title15)
                ),
                DietItems(
                    imageDetail = R.drawable.fiber,
                    description = getString(R.string.diet_desc16),
                    title = getString(R.string.diet_title16)
                ),
                DietItems(
                    imageDetail = R.drawable.cinko,
                    description = getString(R.string.diet_desc17),
                    title = getString(R.string.diet_title17)
                ),
                ),viewPagerDetail
        )
        viewPagerDetail.clipToPadding = false
        viewPagerDetail.clipChildren = false
        viewPagerDetail.offscreenPageLimit = 2
        viewPagerDetail.adapter = dietAdapter


        (viewPagerDetail.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER


    }

}