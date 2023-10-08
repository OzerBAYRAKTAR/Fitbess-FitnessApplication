package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFreshBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstAdapter
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener

class FreshFragment : Fragment(),RecyclerViewClickListener {

    private var _binding: FragmentFreshBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFreshBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()


        foodList = listOf(
            FoodItems(
                id = 1,
                title = getString(R.string.patates),
                calori = 700,
                imageFood = R.drawable.patates
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.patlican),
                calori = 700,
                imageFood = R.drawable.patlican
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.marul),
                calori = 700,
                imageFood = R.drawable.marul
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.havuc),
                calori = 700,
                imageFood = R.drawable.havuc
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.roka),
                calori = 700,
                imageFood = R.drawable.roka
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.tere),
                calori = 700,
                imageFood = R.drawable.tere
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.sogan),
                calori = 700,
                imageFood = R.drawable.sogan
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.biber),
                calori = 700,
                imageFood = R.drawable.biber
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.domates),
                calori = 700,
                imageFood = R.drawable.domates
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }
    private fun setRecyclerview() = with(binding) {
        rvMealList.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = FoodTablayoutAdapter(foodList,this@FreshFragment)
        rvMealList.adapter = foodAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        TODO("Not yet implemented")
    }


}