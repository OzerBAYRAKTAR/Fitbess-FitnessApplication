package com.bayraktar.healthybackandneck.ui.FoodDetail

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.material3.AlertDialog
import com.bayraktar.healthybackandneck.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFoodDetailBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class FoodDetailFragment : Fragment() {

    private var _binding: FragmentFoodDetailBinding? = null
    val binding get() = _binding!!

    private var foodModel: FoodItems? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSetDate()
    }

    private fun getSetDate() = with(binding) {
        //val args = FoodDetailFragmentArgs.fromBundle(requireArguments())
        //foodModel = args.newList
        val args = arguments
        if (args != null) {
            val json = arguments?.getString("jsonList")
            val gson = Gson()

            foodModel = gson.fromJson(json, FoodItems::class.java)

            foodModel?.imageFood?.let {
                imageOtherFood.setImageResource(it)
            }
            txtTitle.text = foodModel?.title
            txtCalsium.text = foodModel?.calsium
            txtIron.text = foodModel?.iron
            txtKarb.text = foodModel?.carb
            txtVitaminc.text = foodModel?.vitaminC.toString()
            txtYag.text = foodModel?.yag
            txtMagnesium.text = foodModel?.magnesium
            txtPotasium.text = foodModel?.potasium
            txtProtein.text = foodModel?.protein
            txtSodium.text = foodModel?.sodium
        }
    }
    private fun onBackStack() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //things what we do when backpressed clicked

            }

        }
    }

}