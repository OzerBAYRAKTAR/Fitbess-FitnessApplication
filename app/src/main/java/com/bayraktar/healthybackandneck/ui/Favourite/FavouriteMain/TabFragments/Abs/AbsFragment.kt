package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Abs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import pl.droidsonroids.gif.GifImageView


@AndroidEntryPoint
class AbsFragment : Fragment(), RecyclerViewClickListener {
    private var _binding: FragmentAbsBinding? = null
    val binding get() = _binding!!

    private val viewModel: AbsViewModel by viewModels()

    private var absList = ArrayList<SubExerciseDayExercise>()
    private lateinit var absAdapter: FavouriteTablayoutadapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAbsBinding.inflate(inflater, container, false)
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
                absAdapter = FavouriteTablayoutadapter(absList, this@AbsFragment,
                    object : OnFavouriteButtonClickListener {
                        @SuppressLint("MissingInflatedId")
                        override fun onButtonClicked(position: Int) {
                            val inflater = layoutInflater
                            val dialogView = inflater.inflate(R.layout.info_favourite_layout, null)

                            val currentModel = absList[position]

                            val exerciseTitle: TextView = dialogView.findViewById(R.id.exerciseFavouriteTitle)
                            val exerciseDescription: TextView = dialogView.findViewById(R.id.exerciseFavouriteDescription)
                            val exerciseGif: GifImageView = dialogView.findViewById(R.id.infoFavouriteGif)

                            exerciseTitle.text = currentModel.exerciseName
                            exerciseGif.setImageResource(currentModel.image)
                            exerciseDescription.text = currentModel.exerciseDescription


                            val builder = AlertDialog.Builder(requireContext())
                            builder.setView(dialogView)
                                .setPositiveButton(R.string.close) { dialog, _ ->
                                    dialog.dismiss()
                                }
                            val dialog = builder.create()
                            dialog.show()
                        }
                    })
                binding.rvFavouriteLayout.adapter = absAdapter
                absAdapter.setData(absList)

            })
    }


    override fun recyclerviewListClicked(v: View, position: Int) {
        val positionList = absList[position]

        if (!positionList.isFavourite) {
            showToastFavourite(
                requireContext(),
                "${positionList.exerciseName}, Favorilerine eklendi.",
                Gravity.BOTTOM,
                0,
                50
            )
            viewModel.updateExerciseById(absList[position].exerciseId)
            positionList.isFavourite = !positionList.isFavourite
            absAdapter.setData(absList)
        } else {
            viewModel.updateIsFavouriteToFalse(absList[position].exerciseId)
            absAdapter.setData(absList)
            positionList.isFavourite = !positionList.isFavourite
        }

    }
}