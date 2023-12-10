package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Abs

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentAbsBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayAdapter
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.FavouriteTablayoutadapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.AbsAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.HomePageFragmentDirections
import com.bayraktar.healthybackandneck.utils.OnFavouriteButtonClickListener
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.showToast
import com.bayraktar.healthybackandneck.utils.showToastFavourite
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AbsFragment : Fragment(),RecyclerViewClickListener {
    private var _binding: FragmentAbsBinding ? =null
    val binding get() = _binding!!

    private val viewModel : AbsViewModel by viewModels()

    private var absList = ArrayList<SubExerciseDayExercise>()
    private lateinit var absAdapter : FavouriteTablayoutadapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAbsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setRecyclerview()
        observeListWithTitle()
        viewModel.getExerciseListByTitle()
    }

    private fun observeListWithTitle() {
        viewModel.getExerciseListByTitle.observe(
            viewLifecycleOwner,
            Observer { exercise ->
                if (exercise != null) {
                    absList.clear()
                    absList.addAll(exercise)
                }
                binding.rvFavouriteLayout.layoutManager = LinearLayoutManager(requireContext())
                absAdapter = FavouriteTablayoutadapter(absList,this@AbsFragment,
                    object : OnFavouriteButtonClickListener {
                    override fun onButtonClicked(position: Int) {
                        showToast(requireContext(),"$position tıklandı",Gravity.CENTER,0,0)
                    }
                })
                binding.rvFavouriteLayout.adapter = absAdapter
                absAdapter.setData(absList)
            })
    }


    override fun recyclerviewListClicked(v: View, position: Int) {
        val positionList = absList[position]
        if (!positionList.isChecked) {
            showToastFavourite(requireContext(),"$position. egzersiz, Favorilerine eklendi.",Gravity.BOTTOM,0,0)
        }

    }
}