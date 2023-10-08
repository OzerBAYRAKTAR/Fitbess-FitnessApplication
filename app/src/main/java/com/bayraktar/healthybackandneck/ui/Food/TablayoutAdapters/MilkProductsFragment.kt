package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentMeetBinding
import com.bayraktar.healthybackandneck.databinding.FragmentMilkProductsBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class MilkProductsFragment : Fragment(),RecyclerViewClickListener {
    private var _binding: FragmentMilkProductsBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMilkProductsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()

        println("freshh")

        foodList = listOf(
            FoodItems(
                id = 1,
                title = getString(R.string.sut),
                calori = 700,
                imageFood = R.drawable.sut
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.yogurt),
                calori = 700,
                imageFood = R.drawable.yogurt
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.taze_peynir),
                calori = 700,
                imageFood = R.drawable.peynir
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.ayran),
                calori = 700,
                imageFood = R.drawable.biber
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.kefir),
                calori = 700,
                imageFood = R.drawable.biber
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.tereyagi),
                calori = 700,
                imageFood = R.drawable.biber
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.lor_peynir),
                calori = 700,
                imageFood = R.drawable.biber
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.yumurta),
                calori = 700,
                imageFood = R.drawable.biber
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }
    private fun setRecyclerview() = with(binding) {
        rvMealList.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = FoodTablayoutAdapter(foodList,this@MilkProductsFragment)
        rvMealList.adapter = foodAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        TODO("Not yet implemented")
    }


}