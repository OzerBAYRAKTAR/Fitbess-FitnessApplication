package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.LegButt

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentLegButtBinding
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.FavouriteTablayoutadapter
import com.bayraktar.healthybackandneck.utils.Interfaces.OnFavouriteButtonClickListener
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.showToastFavourite
import dagger.hilt.android.AndroidEntryPoint
import pl.droidsonroids.gif.GifImageView


@AndroidEntryPoint
class LegButtFragment : Fragment(), RecyclerViewClickListener {
    private var _binding: FragmentLegButtBinding? =null
    val binding get() = _binding!!

    private val viewModel : LegButtViewModel by viewModels()

    private var legButtList = ArrayList<ExerciseDayExercise>()
    private lateinit var legButtAdapter : FavouriteTablayoutadapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLegButtBinding.inflate(inflater,container,false)
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
                    legButtList.clear()
                    legButtList.addAll(exercise)
                }
                binding.rvFavouriteLayout.layoutManager = LinearLayoutManager(requireContext())
                legButtAdapter = FavouriteTablayoutadapter(legButtList, this@LegButtFragment,
                    object : OnFavouriteButtonClickListener {
                        override fun onButtonClicked(position: Int) {
                            val inflater = layoutInflater
                            val dialogView = inflater.inflate(R.layout.info_favourite_layout, null)

                            val currentModel = legButtList[position]

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
                binding.rvFavouriteLayout.adapter = legButtAdapter
                legButtAdapter.setData(legButtList)

            })
    }


    override fun recyclerviewListClicked(v: View, position: Int) {
        val positionList = legButtList[position]

        if (!positionList.isFavourite) {
            showToastFavourite(
                requireContext(),
                "${positionList.exerciseName}, Favorilerine eklendi.",
                Gravity.BOTTOM,
                0,
                50
            )
            viewModel.updateExerciseById(legButtList[position].exerciseId)
            positionList.isFavourite = !positionList.isFavourite
            legButtAdapter.setData(legButtList)
        } else {
            viewModel.updateIsFavouriteToFalse(legButtList[position].exerciseId)
            legButtAdapter.setData(legButtList)
            positionList.isFavourite = !positionList.isFavourite
        }

    }
}