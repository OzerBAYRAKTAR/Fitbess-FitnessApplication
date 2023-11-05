package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.data.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentMeetBinding
import com.bayraktar.healthybackandneck.databinding.FragmentMilkProductsBinding
import com.bayraktar.healthybackandneck.ui.FoodDetail.FoodDetailFragment
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.showToast
import com.google.gson.Gson


class  MilkProductsFragment : Fragment(), RecyclerViewClickListener {
    private var _binding: FragmentMilkProductsBinding? = null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList = emptyList<FoodItems>()
    lateinit var context: AppCompatActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMilkProductsBinding.inflate(inflater, container, false)
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
                calori = "42 calorie",
                imageFood = R.drawable.sut,
                protein = "3.4 gr",
                carb = "5 gr",
                yag = "1 gr",
                vitaminC = "0 mg",
                sodium = "44 mg",
                calsium = "125 mg",
                potasium = "150 mg",
                magnesium = "11 mg",
                iron = "0 mg"
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.yogurt),
                calori = "58 calorie",
                imageFood = R.drawable.yogurt,
                protein = "10 gr",
                carb = "3.6 gr",
                yag = "0.4 gr",
                vitaminC = "0 mg",
                sodium = "36 mg",
                calsium = "110 mg",
                potasium = "141 mg",
                magnesium = "11 mg",
                iron = "0.1 mg"
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.taze_peynir),
                calori = "311 calorie" ,
                imageFood = R.drawable.peynir,
                protein = "20 gr",
                carb = "2.5 gr",
                yag = "24 gr",
                vitaminC = "0 mg",
                sodium = "704 mg",
                calsium = "690 mg",
                potasium = "126 mg",
                magnesium = "29 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.ayran),
                calori = "37 calorie",
                imageFood = R.drawable.ayran,
                protein = "2 gr",
                carb = "3.1 gr",
                yag = "1.6 gr",
                vitaminC = "0 mg",
                sodium = "312 mg",
                calsium = "100 mg",
                potasium = "0 mg",
                magnesium = "0 mg",
                iron = "0 mg"
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.kefir),
                calori = "38 calorie",
                imageFood = R.drawable.kefir,
                protein = "3.5 gr",
                carb = "4.1 gr",
                yag = "0.1 gr",
                vitaminC = "0 mg",
                sodium = "51 mg",
                calsium = "122 mg",
                potasium = "152 mg",
                magnesium = "0 mg",
                iron = "0 mg"
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.tereyagi),
                calori = "717 calorie",
                imageFood = R.drawable.tereyagi,
                protein = "0.9 gr",
                carb = "0 gr",
                yag = "81 gr",
                vitaminC = "0 mg",
                sodium = "11 mg",
                calsium = "20 mg",
                potasium = "24 mg",
                magnesium = "0 mg",
                iron = "0 mg"
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.lor_peynir),
                calori = "700 calorie",
                imageFood = R.drawable.lor,
                protein = "11 gr",
                carb = "3.4 gr",
                yag = "4.3 gr",
                vitaminC = "0 mg",
                sodium = "364 mg",
                calsium = "83 mg",
                potasium = "104 mg",
                magnesium = "8 mg",
                iron = "0.1 mg"
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.yumurta),
                calori = "115 calorie",
                imageFood = R.drawable.yumurta,
                protein = "13 gr",
                carb = "1.1 gr",
                yag = "11 gr",
                vitaminC = "0 mg",
                sodium = "124 mg",
                calsium = "50 mg",
                potasium = "126 mg",
                magnesium = "10 mg",
                iron = "1.2 mg"
            ),
        )
        foodAdapter = FoodTablayoutAdapter(foodList, this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }

    private fun setRecyclerview() = with(binding) {
        val layoutmanager = GridLayoutManager(requireContext(),2)
        rvMealList.layoutManager = layoutmanager
        foodAdapter = FoodTablayoutAdapter(foodList, this@MilkProductsFragment)
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
        bundle.putInt("intValue", id)
        bundle.putString("jsonList", json)


        val receiver = FoodDetailFragment()
        receiver.arguments = bundle

        val transaction: FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.containerfood, receiver).addToBackStack("transactionTag")
        transaction.commit()

    }


}