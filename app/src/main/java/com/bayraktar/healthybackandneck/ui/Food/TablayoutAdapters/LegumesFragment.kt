package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFruitBinding
import com.bayraktar.healthybackandneck.databinding.FragmentLegumesBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class LegumesFragment : Fragment(),RecyclerViewClickListener {
    private var _binding: FragmentLegumesBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLegumesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()


        foodList = listOf(
            FoodItems(
                id = 1,
                title = getString(R.string.bakla),
                calori = 700,
                imageFood = R.drawable.bakla
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.bamya),
                calori = 700,
                imageFood = R.drawable.bamya
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.barbunya),
                calori = 700,
                imageFood = R.drawable.barbunya
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.bezelye),
                calori = 700,
                imageFood = R.drawable.bezelye
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.borulce),
                calori = 700,
                imageFood = R.drawable.borulce
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.mercimek),
                calori = 700,
                imageFood = R.drawable.mercimek
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.kestane),
                calori = 700,
                imageFood = R.drawable.kestane
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.fasulye),
                calori = 700,
                imageFood = R.drawable.fasulye
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.nohut),
                calori = 700,
                imageFood = R.drawable.nohut
            ),
            FoodItems(
                id = 10,
                title = getString(R.string.ceviz),
                calori = 700,
                imageFood = R.drawable.ceviz
            ),
            FoodItems(
                id = 11,
                title = getString(R.string.yesil_fasulye),
                calori = 700,
                imageFood = R.drawable.yesil_fasulye
            ),
            FoodItems(
                id = 12,
                title = getString(R.string.tofu),
                calori = 700,
                imageFood = R.drawable.tofu
            ),
            FoodItems(
                id = 13,
                title = getString(R.string.badem),
                calori = 700,
                imageFood = R.drawable.badem
            ),
            FoodItems(
                id = 14,
                title = getString(R.string.fistik),
                calori = 700,
                imageFood = R.drawable.fistik
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }
    private fun setRecyclerview() = with(binding) {
        rvMealList.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = FoodTablayoutAdapter(foodList,this@LegumesFragment)
        rvMealList.adapter = foodAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        TODO("Not yet implemented")
    }


}