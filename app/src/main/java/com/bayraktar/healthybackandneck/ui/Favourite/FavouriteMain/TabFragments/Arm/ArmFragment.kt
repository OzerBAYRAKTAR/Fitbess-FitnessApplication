package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Arm

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
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentAbsBinding
import com.bayraktar.healthybackandneck.databinding.FragmentArmBinding
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Abs.AbsViewModel
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.FavouriteTablayoutadapter
import com.bayraktar.healthybackandneck.utils.OnFavouriteButtonClickListener
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.showToast
import com.bayraktar.healthybackandneck.utils.showToastFavourite
import dagger.hilt.android.AndroidEntryPoint
import pl.droidsonroids.gif.GifImageView


@AndroidEntryPoint
class ArmFragment : Fragment(), RecyclerViewClickListener {
    private var _binding: FragmentArmBinding? =null
    val binding get() = _binding!!

    private val viewModel : ArmViewModel by viewModels()

    private var armList = ArrayList<SubExerciseDayExercise>()
    private lateinit var armAdapter : FavouriteTablayoutadapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArmBinding.inflate(inflater, container, false)
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
                    armList.clear()
                    armList.addAll(exercise)
                }
                binding.rvFavouriteLayout.layoutManager = LinearLayoutManager(requireContext())
                armAdapter = FavouriteTablayoutadapter(armList, this@ArmFragment,
                    object : OnFavouriteButtonClickListener {
                        override fun onButtonClicked(position: Int) {
                            val inflater = layoutInflater
                            val dialogView = inflater.inflate(R.layout.info_favourite_layout, null)

                            val currentModel = armList[position]

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
                binding.rvFavouriteLayout.adapter = armAdapter
                armAdapter.setData(armList)

            })
    }


    override fun recyclerviewListClicked(v: View, position: Int) {
        val positionList = armList[position]

        if (!positionList.isFavourite) {
            showToastFavourite(
                requireContext(),
                "${positionList.exerciseName}, Favorilerine eklendi.",
                Gravity.BOTTOM,
                0,
                50
            )
            viewModel.updateExerciseById(armList[position].exerciseId)
            positionList.isFavourite = !positionList.isFavourite
            armAdapter.setData(armList)
        } else {
            viewModel.updateIsFavouriteToFalse(armList[position].exerciseId)
            armAdapter.setData(armList)
            positionList.isFavourite = !positionList.isFavourite
        }

    }
}