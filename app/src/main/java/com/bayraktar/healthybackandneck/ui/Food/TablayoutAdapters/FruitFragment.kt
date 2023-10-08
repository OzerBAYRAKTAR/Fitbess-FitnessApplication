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
import com.bayraktar.healthybackandneck.databinding.FragmentFruitBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class FruitFragment : Fragment(),RecyclerViewClickListener {

    private var _binding: FragmentFruitBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFruitBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()


        foodList = listOf(
            FoodItems(
                id = 1,
                title = getString(R.string.muz),
                calori = 700,
                imageFood = R.drawable.muz
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.elma),
                calori = 700,
                imageFood = R.drawable.elma
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.armut),
                calori = 700,
                imageFood = R.drawable.armut
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.cilek),
                calori = 700,
                imageFood = R.drawable.cilek
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.karpuz),
                calori = 700,
                imageFood = R.drawable.karpuz
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.kavun),
                calori = 700,
                imageFood = R.drawable.kavun
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.seftali),
                calori = 700,
                imageFood = R.drawable.seftali
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.kayisi),
                calori = 700,
                imageFood = R.drawable.kayisi
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.erik),
                calori = 700,
                imageFood = R.drawable.erik
            ),
            FoodItems(
                id = 10,
                title = getString(R.string.incir),
                calori = 700,
                imageFood = R.drawable.incir
            ),
            FoodItems(
                id = 11,
                title = getString(R.string.uzum),
                calori = 700,
                imageFood = R.drawable.uzum
            ),
            FoodItems(
                id = 12,
                title = getString(R.string.kiraz),
                calori = 700,
                imageFood = R.drawable.kiraz
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }
    private fun setRecyclerview() = with(binding) {
        rvMealList.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = FoodTablayoutAdapter(foodList,this@FruitFragment)
        rvMealList.adapter = foodAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        TODO("Not yet implemented")
    }


}