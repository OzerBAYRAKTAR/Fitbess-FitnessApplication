package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.data.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentMeetBinding
import com.bayraktar.healthybackandneck.ui.FoodDetail.FoodDetailFragment
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.google.gson.Gson


class MeetFragment : Fragment(),RecyclerViewClickListener {

    private var _binding: FragmentMeetBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()
    lateinit var context: AppCompatActivity


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
                calori = "291 calorie",
                imageFood = R.drawable.antrikot,
                protein = "24 gr",
                carb = "0 gr",
                yag = "22 gr",
                vitaminC = "0 mg",
                sodium = "54 mg",
                calsium = "11 mg",
                potasium = "260 mg",
                magnesium = "22 mg",
                iron = "2.2 mg"
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.biftek),
                calori = "133 calorie",
                imageFood = R.drawable.biftek,
                protein = "21 gr",
                carb = "0 gr",
                yag = "5 gr",
                vitaminC = "0 mg",
                sodium = "55 mg",
                calsium = "5 mg",
                potasium = "375 mg",
                magnesium = "25 mg",
                iron = "2 mg"
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.bonfile),
                calori = "324 calorie",
                imageFood = R.drawable.bonfile,
                protein = "24 gr",
                carb = "0 gr",
                yag = "25 gr",
                vitaminC = "0 mg",
                sodium = "57 mg",
                calsium = "9 mg",
                potasium = "331 mg",
                magnesium = "22 mg",
                iron = "3.1 mg"
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.kiyma),
                calori = "332 calorie",
                imageFood = R.drawable.kiyma,
                protein = "14 gr",
                carb = "0 gr",
                yag = "30 gr",
                vitaminC = "0 mg",
                sodium = "67 mg",
                calsium = "24 mg",
                potasium = "218 mg",
                magnesium = "14 mg",
                iron = "1.6 mg"
            ),

            FoodItems(
                id = 5,
                title = getString(R.string.hindi),
                calori = "188 calorie",
                imageFood = R.drawable.hindi,
                protein = "29 gr",
                carb = "0 gr",
                yag = "7 gr",
                vitaminC = "0 mg",
                sodium = "103 mg",
                calsium = "14 mg",
                potasium = "239 mg",
                magnesium = "30 mg",
                iron = "1ç1 mg"
            ),

            FoodItems(
                id = 6,
                title = getString(R.string.kuzu),
                calori = "294 calorie",
                imageFood = R.drawable.kuzu,
                protein = "25 gr",
                carb = "0 gr",
                yag = "21 gr",
                vitaminC = "0 mg",
                sodium = "72 mg",
                calsium = "17 mg",
                potasium = "310 mg",
                magnesium = "23 mg",
                iron = "1.9 mg"
            ),

            FoodItems(
                id = 7,
                title = getString(R.string.tavuk),
                calori = "239 calorie" ,
                imageFood = R.drawable.tavuk,
                protein = "27 gr",
                carb = "0 gr",
                yag = "14 gr",
                vitaminC = "0 mg",
                sodium = "82 mg",
                calsium = "15 mg",
                potasium = "223 mg",
                magnesium = "23 mg",
                iron = "1.3 mg"
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.ordek),
                calori = "337 calorie",
                imageFood = R.drawable.ordek,
                protein = "19 gr",
                carb = "0 gr",
                yag = "28 gr",
                vitaminC = "0 mg",
                sodium = "59 mg",
                calsium = "11 mg",
                potasium = "204 mg",
                magnesium = "16 mg",
                iron = "2.7 mg"
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.ahtapot),
                calori = "164 calorie",
                imageFood = R.drawable.ahtapot,
                protein = "30 gr",
                carb = "4.4 gr",
                yag = "2.1 gr",
                vitaminC = "8 mg",
                sodium = "460 mg",
                calsium = "106 mg",
                potasium = "630 mg",
                magnesium = "60 mg",
                iron = "9.5 mg"
            ),
            FoodItems(
                id = 10,
                title = getString(R.string.alabalık),
                calori = "140 calorie",
                imageFood = R.drawable.alabalik,
                protein = "20 gr",
                carb = "0 gr",
                yag = "6 gr",
                vitaminC = "2.9 mg",
                sodium = "51 mg",
                calsium = "25 mg",
                potasium = "337 mg",
                magnesium = "25 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 11,
                title = getString(R.string.levrek),
                calori = "124 calorie",
                imageFood = R.drawable.levrek,
                protein = "24 gr",
                carb = "0 gr",
                yag = "2.6 gr",
                vitaminC = "0 mg",
                sodium = "87 mg",
                calsium = "13 mg",
                potasium = "328 mg",
                magnesium = "53 mg",
                iron = "0.4 mg"
            ),
            FoodItems(
                id = 12,
                title = getString(R.string.kalamar),
                calori = "175 calorie",
                imageFood = R.drawable.kalamar,
                protein = "18 gr",
                carb = "8 gr",
                yag = "7 gr",
                vitaminC = "4.2 mg",
                sodium = "306 mg",
                calsium = "39 mg",
                potasium = "279 mg",
                magnesium = "38 mg",
                iron = "1 mg"
            ),
            FoodItems(
                id = 13,
                title = getString(R.string.somon),
                calori = "208 calorie",
                imageFood = R.drawable.somon,
                protein = "20 gr",
                carb = "0 gr",
                yag = "13 gr",
                vitaminC = "3.9 mg",
                sodium = "59 mg",
                calsium = "9 mg",
                potasium = "363 mg",
                magnesium = "27 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 14,
                title = getString(R.string.hamsi),
                calori = "210 calorie",
                imageFood = R.drawable.hamsi,
                protein = "29 gr",
                carb = "0 gr",
                yag = "10 gr",
                vitaminC = "0 mg",
                sodium = "3668 mg",
                calsium = "232 mg",
                potasium = "544 mg",
                magnesium = "69 mg",
                iron = "4.6 mg"
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }

    private fun setRecyclerview() = with(binding) {
        val layoutmanager = GridLayoutManager(requireContext(),2)
        rvMealList.layoutManager = layoutmanager
        foodAdapter = FoodTablayoutAdapter(foodList,this@MeetFragment)
        rvMealList.adapter = foodAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        val listProduct = foodList
        val id = foodList[position].id

        val fm = context.supportFragmentManager


        val bundle = Bundle()
        val gson = Gson()
        val json = gson.toJson(listProduct)
        bundle.putInt("intValue",id)
        bundle.putString("jsonList", json)


        val receiver = FoodDetailFragment()
        receiver.arguments = bundle

        val transaction : FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.containerfood,receiver).addToBackStack("transactionTag")
        transaction.commit()

    }


}