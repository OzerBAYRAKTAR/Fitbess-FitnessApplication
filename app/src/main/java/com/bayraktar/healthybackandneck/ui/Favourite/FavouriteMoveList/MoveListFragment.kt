package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentMoveListBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDaySubAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import com.bayraktar.healthybackandneck.utils.OnDeleteClicked
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.showToast
import com.bayraktar.healthybackandneck.utils.showToastFavourite
import dagger.hilt.android.AndroidEntryPoint
import pl.droidsonroids.gif.GifImageView


@AndroidEntryPoint
class MoveListFragment : Fragment(),RecyclerViewClickListener,OnDeleteClicked {

    private var _binding: FragmentMoveListBinding? = null
    val binding get() = _binding!!

    private val viewModel: MoveListViewModel by viewModels()

    private var favList = ArrayList<ExerciseDayExercise>()
    private var moveListAdapter: MoveListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeFavlist()
        viewModel.getFavExerciseList()
    }

    private fun observeFavlist() = with(binding) {
        viewModel.getFavExercises.observe(viewLifecycleOwner, Observer { list ->
            if (list != null) {
                if (list.any()) {
                    txtEmptyFav.visibility = View.GONE
                    btnCheck.visibility = View.GONE
                    favList = list.distinctBy { it.exerciseName } as ArrayList<ExerciseDayExercise>
                }else {
                    txtEmptyFav.visibility = View.VISIBLE
                    btnCheck.visibility = View.VISIBLE

                    btnCheck.setOnClickListener {
                        val action = MoveListFragmentDirections.actionMoveListFragmentToIdHomepageFragment()
                        view?.findNavController()?.navigate(action)
                    }
                }

            }else {
                showToast(requireContext(),getString(R.string.label_error),Gravity.CENTER,0,0)
            }

            txttime.text = "${favList.size}:00"
            txtexercise.text = favList.size.toString()
            txtkcal.text = "${favList.size}00"
            rvdaydetail.layoutManager = LinearLayoutManager(requireContext())
            moveListAdapter = MoveListAdapter(favList,this@MoveListFragment,this@MoveListFragment)
            rvdaydetail.adapter = moveListAdapter
        })
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.info_favourite_layout, null)

        val currentModel = favList[position]

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
    @SuppressLint("MissingInflatedId")
    private fun btnCloseClicked(position: Int) {
        val currentModel = favList[position]
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_delete_favourite, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        val positiveBtn: Button = dialogView.findViewById(R.id.dialogYesFV)
        val negativeBtn: Button = dialogView.findViewById(R.id.dialogNoFV)

        negativeBtn.setOnClickListener {
            dialog.dismiss()
        }
        positiveBtn.setOnClickListener {
            viewModel.updateIsFavouriteToFalse(currentModel.exerciseId)
            currentModel.isFavourite = !currentModel.isFavourite

            val updatedList = moveListAdapter?.lsMenu?.toMutableList() ?: mutableListOf()
            updatedList.removeAt(position)

            moveListAdapter?.setData(updatedList)
            dialog.dismiss()
        }

    }

    override fun onDeleteClicked(position: Int) {
        btnCloseClicked(position)
    }

}