package com.bayraktar.healthybackandneck.ui.FoodDetail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.data.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.databinding.FragmentFoodDetailBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class FoodDetailFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentFoodDetailBinding? = null
    val binding get() = _binding!!

    private var foodList = ArrayList<FoodItems>()
    private var foodlistExceptId = ArrayList<FoodItems>()
    private var selectedFoodModel: FoodItems? = null
    private var id = 0
    lateinit var context: AppCompatActivity
    private lateinit var detailAdapter: FoodDetailAdapter

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


        backstack()
        setRecycler()
        getSetDate()
        backBtnclicked()

    }

    private fun setRecycler() = with(binding) {
        rvOtherFood.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        detailAdapter = FoodDetailAdapter(foodList, this@FoodDetailFragment)
        rvOtherFood.adapter = detailAdapter
    }

    private fun getSetDate() = with(binding) {

        val args = arguments
        if (args != null) {
            val json = arguments?.getString("jsonList")
            val gson = Gson()
            val type = object : TypeToken<List<FoodItems>>() {}.type

            id = args.getInt("intValue")


            foodList = gson.fromJson<List<FoodItems>>(
                json,
                type
            ) as ArrayList<FoodItems>
            selectedFoodModel = foodList.filter { it.id == id }.firstOrNull()
            foodlistExceptId = foodList.filter { it.id != id }.toList() as ArrayList<FoodItems>




            selectedFoodModel?.imageFood?.let {
                imageOtherFood.setImageResource(it)
            }
            txtTitle.text = selectedFoodModel?.title
            txtCalsium.text = selectedFoodModel?.calsium
            txtIron.text = selectedFoodModel?.iron
            txtKarb.text = selectedFoodModel?.carb
            txtVitaminc.text = selectedFoodModel?.vitaminC.toString()
            txtYag.text = selectedFoodModel?.yag
            txtMagnesium.text = selectedFoodModel?.magnesium
            txtPotasium.text = selectedFoodModel?.potasium
            txtProtein.text = selectedFoodModel?.protein
            txtSodium.text = selectedFoodModel?.sodium

            detailAdapter = FoodDetailAdapter(foodlistExceptId, this@FoodDetailFragment)
            rvOtherFood.adapter = detailAdapter
            detailAdapter.setData(foodlistExceptId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack(
                    "transactionTag",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun backBtnclicked() = with(binding) {
        goFoodBack.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack("transactionTag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        binding.apply {
            val model = foodlistExceptId[position]

            model.imageFood.let {
                imageOtherFood.setImageResource(it)
            }
            txtTitle.text = model.title
            txtCalsium.text = model.calsium
            txtIron.text = model.iron
            txtKarb.text = model.carb
            txtVitaminc.text = model.vitaminC.toString()
            txtYag.text = model.yag
            txtMagnesium.text = model.magnesium
            txtPotasium.text = model.potasium
            txtProtein.text = model.protein
            txtSodium.text = model.sodium

        }

    }

}