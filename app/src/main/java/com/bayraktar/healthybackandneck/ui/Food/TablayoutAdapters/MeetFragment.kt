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
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class MeetFragment : Fragment(),RecyclerViewClickListener {

    private var _binding: FragmentMeetBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMeetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()

        println("freshh")

        foodList = listOf(
            FoodItems(
                id = 1,
                title = getString(R.string.antrikot),
                calori = 700,
                imageFood = R.drawable.antrikot
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.biftek),
                calori = 700,
                imageFood = R.drawable.biftek
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.bonfile),
                calori = 700,
                imageFood = R.drawable.bonfile
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.kiyma),
                calori = 700,
                imageFood = R.drawable.kiyma
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.hindi),
                calori = 700,
                imageFood = R.drawable.hindi
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.kuzu),
                calori = 700,
                imageFood = R.drawable.kuzu
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.tavuk),
                calori = 700,
                imageFood = R.drawable.tavuk
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.ordek),
                calori = 700,
                imageFood = R.drawable.ordek
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.ahtapot),
                calori = 700,
                imageFood = R.drawable.ahtapot
            ),
            FoodItems(
                id = 10,
                title = getString(R.string.alabalık),
                calori = 700,
                imageFood = R.drawable.alabalik
            ),
            FoodItems(
                id = 11,
                title = getString(R.string.levrek),
                calori = 700,
                imageFood = R.drawable.levrek
            ),
            FoodItems(
                id = 12,
                title = getString(R.string.kalamar),
                calori = 700,
                imageFood = R.drawable.kalamar
            ),
            FoodItems(
                id = 13,
                title = getString(R.string.somon),
                calori = 700,
                imageFood = R.drawable.somon
            ),
            FoodItems(
                id = 14,
                title = getString(R.string.hamsi),
                calori = 700,
                imageFood = R.drawable.hamsi
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }
    private fun setRecyclerview() = with(binding) {
        rvMealList.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = FoodTablayoutAdapter(foodList,this@MeetFragment)
        rvMealList.adapter = foodAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        TODO("Not yet implemented")
    }


}