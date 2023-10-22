package com.bayraktar.healthybackandneck.ui.Food.TablayoutAdapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.data.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFreshBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstAdapter
import com.bayraktar.healthybackandneck.ui.Food.FoodFragment
import com.bayraktar.healthybackandneck.ui.FoodDetail.FoodDetailFragment
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.google.gson.Gson

class FreshFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentFreshBinding? = null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FreshAdapter
    private var foodList = emptyList<FoodItems>()
    private var foodModel: FoodItems? = null

    lateinit var context: AppCompatActivity



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFreshBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()


        foodList = listOf(
            FoodItems(
                id = 1,
                title = getString(R.string.patates),
                calori = 70,
                imageFood = R.drawable.patates,
                protein = "2 gr",
                carb = "17 gr",
                yag = "0 gr",
                vitaminC = "7.5 mg",
                sodium = "5 mg",
                calsium = "8 mg",
                potasium = "328 mg",
                magnesium = "0 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.patlican),
                calori = 25,
                imageFood = R.drawable.patlican,
                protein = "1 gr",
                carb = "6 gr",
                yag = "0.2 gr",
                vitaminC = "2.2 mg",
                sodium = "2 mg",
                calsium = "9 mg",
                potasium = "229 mg",
                magnesium = "14 mg",
                iron = "0.2mg"
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.marul),
                calori = 14,
                imageFood = R.drawable.marul,
                protein = "1.5 gr",
                carb = "3 gr",
                vitaminC = "9.4 mg",
                yag = "0.2 gr",
                sodium = "28 mg",
                calsium = "36 mg",
                potasium = "194 mg",
                magnesium = "13 mg",
                iron = "0.9 mg"
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.havuc),
                calori = 41,
                imageFood = R.drawable.havuc,
                protein = "1 gr",
                carb = "10 gr",
                yag = "0.2 gr",
                vitaminC = "5.9 mg",
                sodium = "69 mg",
                calsium = "33 mg",
                potasium = "320 mg",
                magnesium = "12 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.roka),
                calori = 25,
                imageFood = R.drawable.roka,
                protein = "2.6 gr",
                carb = "3.7 gr",
                yag = "0.7 gr",
                vitaminC = "15 mg",
                sodium = "27 mg",
                calsium = "160 mg",
                potasium = "369 mg",
                magnesium = "47 mg",
                iron = "1.5 mg"
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.tere),
                calori = 32,
                imageFood = R.drawable.tere,
                protein = "2.6 gr",
                carb = "6 gr",
                yag = "0.7 gr",
                vitaminC = "69 mg",
                sodium = "14 mg",
                calsium = "81 mg",
                potasium = "606 mg",
                magnesium = "38 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.sogan),
                calori = 39,
                imageFood = R.drawable.sogan,
                protein = "1.1 gr",
                carb = "9 gr",
                yag = "0.1 gr",
                vitaminC = "7.4 mg",
                sodium = "4 mg",
                calsium = "23 mg",
                potasium = "146 mg",
                magnesium = "10 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.biber),
                calori = 700,
                imageFood = R.drawable.biber,
                protein = "2 gr",
                carb = "9 gr",
                yag = "0.2 gr",
                vitaminC = "242.5 mg",
                sodium = "7 mg",
                calsium = "18 mg",
                potasium = "242.5 mg",
                magnesium = "25 mg",
                iron = "1.2 mg"
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.domates),
                calori = 20,
                imageFood = R.drawable.domates,
                protein = "0.9 gr",
                carb = "3.8 gr",
                yag = "0.2 gr",
                vitaminC = "13.7 mg",
                sodium = "5 mg",
                calsium = "10 mg",
                potasium = "237 mg",
                magnesium = "0 mg",
                iron = "0.3 mg"
            ),
        )
        foodAdapter = FreshAdapter(foodList, this)
        binding.rvMealList.adapter = foodAdapter
        foodAdapter.setData(foodList)

    }

    private fun setRecyclerview() = with(binding) {
        rvMealList.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = FreshAdapter(foodList, this@FreshFragment)
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