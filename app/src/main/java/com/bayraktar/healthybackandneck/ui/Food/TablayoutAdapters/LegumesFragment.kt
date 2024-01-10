package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.bayraktar.healthybackandneck.data.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentLegumesBinding
import com.bayraktar.healthybackandneck.ui.FoodDetail.FoodDetailFragment
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.google.gson.Gson


class LegumesFragment : Fragment(), RecyclerViewClickListener {
    private var _binding: FragmentLegumesBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()
    lateinit var context: AppCompatActivity


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
                calori = "88 calorie",
                imageFood = R.drawable.bakla,
                protein = "8 gr",
                carb = "18 gr",
                yag = "0.7 gr",
                vitaminC = "3.7 mg",
                sodium = "25 mg",
                calsium = "37 mg",
                potasium = "332 mg",
                magnesium = "33 mg",
                iron = "1.6 mg"
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.bamya),
                calori = "20 calorie",
                imageFood = R.drawable.bamya,
                protein = "2 gr",
                carb = "2 gr",
                yag = "0.2 gr",
                vitaminC = "36 mg",
                sodium = "0 mg",
                calsium = "84 mg",
                potasium = "285 mg",
                magnesium = "0 mg",
                iron = "1 mg"
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.barbunya),
                calori = "335 calorie",
                imageFood = R.drawable.barbunya,
                protein = "23 gr",
                carb = "60 gr",
                yag = "1.2 gr",
                vitaminC = "0 mg",
                sodium = "6 mg",
                calsium = "127 mg",
                potasium = "1332 mg",
                magnesium = "156 mg",
                iron = "5 mg"
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.bezelye),
                calori = "81 calorie",
                imageFood = R.drawable.bezelye,
                protein = "5 gr",
                carb = "14 gr",
                yag = "0.4 gr",
                vitaminC = "40 mg",
                sodium = "5 mg",
                calsium = "25 mg",
                potasium = "244 mg",
                magnesium = "33 mg",
                iron = "1.5 mg"
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.borulce),
                calori = "115 calorie",
                imageFood = R.drawable.borulce,
                protein = "8 gr",
                carb = "21 gr",
                yag = "0.5 gr",
                vitaminC = "0.4 mg",
                sodium = "4 mg",
                calsium = "24 mg",
                potasium = "278 mg",
                magnesium = "53 mg",
                iron = "2.5 mg"
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.mercimek),
                calori = "116 calorie",
                imageFood = R.drawable.mercimek,
                protein = "9 gr",
                carb = "20 gr",
                yag = "0.4 gr",
                vitaminC = "1.5 mg",
                sodium = "2 mg",
                calsium = "19 mg",
                potasium = "369 mg",
                magnesium = "36 mg",
                iron = "3.3 mg"
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.kestane),
                calori = "131 calorie",
                imageFood = R.drawable.kestane,
                protein = "2 gr",
                carb = "28 gr",
                yag = "1.4 gr",
                vitaminC = "26.7 mg",
                sodium = "27 mg",
                calsium = "46 mg",
                potasium = "715 mg",
                magnesium = "54 mg",
                iron = "1.7 mg"
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.fasulye),
                calori = "347 calorie",
                imageFood = R.drawable.fasulye,
                protein = "21 gr",
                carb = "63 gr",
                yag = "1.2 gr",
                vitaminC = "6.3 mg",
                sodium = "12 mg",
                calsium = "113 mg",
                potasium = "1393 mg",
                magnesium = "176 mg",
                iron = "5.1 mg"
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.nohut),
                calori = "364 calorie",
                imageFood = R.drawable.nohut,
                protein = "19 gr",
                carb = "61 gr",
                yag = "6 gr",
                vitaminC = "4 mg",
                sodium = "24 mg",
                calsium = "105 mg",
                potasium = "875 mg",
                magnesium = "115 mg",
                iron = "6.2 mg"
            ),
            FoodItems(
                id = 10,
                title = getString(R.string.ceviz),
                calori = "654 calorie",
                imageFood = R.drawable.ceviz,
                protein = "15 gr",
                carb = "14 gr",
                yag = "65 gr",
                vitaminC = "1.3 mg",
                sodium = "2 mg",
                calsium = "98 mg",
                potasium = "441 mg",
                magnesium = "158 mg",
                iron = "2.9 mg"
            ),
            FoodItems(
                id = 14,
                title = getString(R.string.fistik),
                calori = "567 calorie",
                imageFood = R.drawable.fistik,
                protein = "26 gr",
                carb = "16 gr",
                yag = "49 gr",
                vitaminC = "0 mg",
                sodium = "18 mg",
                calsium = "92 mg",
                potasium = "705 mg",
                magnesium = "168 mg",
                iron = "4.6 mg"
            ),
            FoodItems(
                id = 11,
                title = getString(R.string.yesil_fasulye),
                calori = "31 calorie",
                imageFood = R.drawable.yesil_fasulye,
                protein = "1.8 gr",
                carb = "7 gr",
                yag = "0.2 gr",
                vitaminC = "12.2 mg",
                sodium = "6 mg",
                calsium = "37 mg",
                potasium = "211 mg",
                magnesium = "25 mg",
                iron = "1 mg"
            ),
            FoodItems(
                id = 12,
                title = getString(R.string.tofu),
                calori = "76 calorie",
                imageFood = R.drawable.tofu,
                protein = "8 gr",
                carb = "1.9 gr",
                yag = "4.8 gr",
                vitaminC = "0.1 mg",
                sodium = "7 mg",
                calsium = "350 mg",
                potasium = "121 mg",
                magnesium = "30 mg",
                iron = "5.4 mg"
            ),
            FoodItems(
                id = 13,
                title = getString(R.string.badem),
                calori = "575 calorie",
                imageFood = R.drawable.badem,
                protein = "21 gr",
                carb = "22 gr",
                yag = "49 gr",
                vitaminC = "0 mg",
                sodium = "1 mg",
                calsium = "264 mg",
                potasium = "705 mg",
                magnesium = "268 mg",
                iron = "3.7 mg"
            ),


        )
        foodAdapter = FoodTablayoutAdapter(foodList,this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }
    private fun setRecyclerview() = with(binding) {
        val layoutmanager = GridLayoutManager(requireContext(),2)
        rvMealList.layoutManager = layoutmanager
        foodAdapter = FoodTablayoutAdapter(foodList,this@LegumesFragment)
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