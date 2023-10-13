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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFreshBinding
import com.bayraktar.healthybackandneck.databinding.FragmentFruitBinding
import com.bayraktar.healthybackandneck.ui.FoodDetail.FoodDetailFragment
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.google.gson.Gson


class FruitFragment : Fragment(),RecyclerViewClickListener {

    private var _binding: FragmentFruitBinding?= null
    val binding get() = _binding!!
    private lateinit var foodAdapter: FoodTablayoutAdapter
    private var foodList= emptyList<FoodItems>()
    lateinit var context: AppCompatActivity


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
                calori = 88,
                imageFood = R.drawable.muz,
                protein = "1.1 gr",
                carb = "23 gr",
                yag = "0.3 gr",
                vitaminC = "8.7 mg",
                sodium = "1 mg",
                calsium = "5 mg",
                potasium = "358 mg",
                magnesium = "27 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 2,
                title = getString(R.string.elma),
                calori = 52,
                imageFood = R.drawable.elma,
                protein = "0.3 gr",
                carb = "14 gr",
                yag = "0.2 gr",
                vitaminC = "4.6 mg",
                sodium = "1 mg",
                calsium = "6 mg",
                potasium = "107 mg",
                magnesium = "5 mg",
                iron = "0.1 mg"
            ),
            FoodItems(
                id = 3,
                title = getString(R.string.armut),
                calori = 57,
                imageFood = R.drawable.armut,
                protein = "0.4 gr",
                carb = "15 gr",
                yag = "0.1 gr",
                vitaminC = "4.3 mg",
                sodium = "1 mg",
                calsium = "9 mg",
                potasium = "116 mg",
                magnesium = "7 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 4,
                title = getString(R.string.cilek),
                calori = 32,
                imageFood = R.drawable.cilek,
                protein = "0.7 gr",
                carb = "8 gr",
                yag = "0.3 gr",
                vitaminC = "58.8 mg",
                sodium = "1 mg",
                calsium = "16 mg",
                potasium = "153 mg",
                magnesium = "13 mg",
                iron = "0.4 mg"
            ),
            FoodItems(
                id = 5,
                title = getString(R.string.karpuz),
                calori = 30,
                imageFood = R.drawable.karpuz,
                protein = "0.6 gr",
                carb = "8 gr",
                yag = "0.2 gr",
                vitaminC = "8.1 mg",
                sodium = "1 mg",
                calsium = "7 mg",
                potasium = "112 mg",
                magnesium = "10 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 6,
                title = getString(R.string.kavun),
                calori = 33,
                imageFood = R.drawable.kavun,
                protein = "0.8 gr",
                carb = "8 gr",
                yag = "0.2 gr",
                vitaminC = "36.7 mg",
                sodium = "16 mg",
                calsium = "9 mg",
                potasium = "267 mg",
                magnesium = "120 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 7,
                title = getString(R.string.seftali),
                calori = 39,
                imageFood = R.drawable.seftali,
                protein = "0.9 gr",
                carb = "10 gr",
                yag = "0.3 gr",
                vitaminC = "6.6 mg",
                sodium = "0 mg",
                calsium = "6 mg",
                potasium = "190 mg",
                magnesium = "9 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 8,
                title = getString(R.string.kayisi),
                calori = 48,
                imageFood = R.drawable.kayisi,
                protein = "1.4 gr",
                carb = "11 gr",
                yag = "0.4 gr",
                vitaminC = "10 mg",
                sodium = "1 mg",
                calsium = "13 mg",
                potasium = "259 mg",
                magnesium = "10 mg",
                iron = "0.4 mg"
            ),
            FoodItems(
                id = 9,
                title = getString(R.string.erik),
                calori = 45,
                imageFood = R.drawable.erik,
                protein = "0.7 gr",
                carb = "11 gr",
                yag = "0.3 gr",
                vitaminC = "9.5 mg",
                sodium = "0 mg",
                calsium = "6 mg",
                potasium = "157 mg",
                magnesium = "7 mg",
                iron = "0.2 mg"
            ),
            FoodItems(
                id = 10,
                title = getString(R.string.incir),
                calori = 74,
                imageFood = R.drawable.incir,
                protein = "0.8 gr",
                carb = "19 gr",
                yag = "0.3 gr",
                vitaminC = "2 mg",
                sodium = "1 mg",
                calsium = "35 mg",
                potasium = "232 mg",
                magnesium = "17 mg",
                iron = "0.4 mg"
            ),
            FoodItems(
                id = 11,
                title = getString(R.string.uzum),
                calori = 67,
                imageFood = R.drawable.uzum,
                protein = "0.6 gr",
                carb = "17 gr",
                yag = "0.4 gr",
                vitaminC = "4 mg",
                sodium = "2 mg",
                calsium = "14 mg",
                potasium = "191 mg",
                magnesium = "5 mg",
                iron = "0.3 mg"
            ),
            FoodItems(
                id = 12,
                title = getString(R.string.kiraz),
                calori = 50,
                imageFood = R.drawable.kiraz,
                protein = "1 gr",
                carb = "12 gr",
                yag = "0.3 gr",
                vitaminC = "10 mg",
                sodium = "3 mg",
                calsium = "16 mg",
                potasium = "173 mg",
                magnesium = "9 mg",
                iron = "0.3 mg"
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
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        val listProduct = foodList[position]

        val fm = context.supportFragmentManager


        val bundle = Bundle()
        val gson = Gson()
        val json = gson.toJson(listProduct)
        bundle.putString("jsonList", json)


        val receiver = FoodDetailFragment()
        receiver.arguments = bundle

        val transaction : FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.containerfood,receiver).addToBackStack(null)
        transaction.commit()

    }


}