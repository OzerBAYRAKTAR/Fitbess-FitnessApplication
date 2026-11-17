package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentMoveListBinding
import com.bayraktar.healthybackandneck.utils.ExerciseMedia
import dagger.hilt.android.AndroidEntryPoint
import pl.droidsonroids.gif.GifImageView

@AndroidEntryPoint
class MoveListFragment : Fragment() {
    private var binding: FragmentMoveListBinding? = null
    private val viewModel: MoveListViewModel by viewModels()
    private var dialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        return FragmentMoveListBinding.inflate(inflater, container, false).also { binding = it }.root
    }
    override fun onViewCreated(view: View, state: Bundle?) {
        val adapter = MoveListAdapter(::inspect, ::confirmRemove, ::discover, viewModel::getFavExerciseList)
        binding!!.rvdaydetail.layoutManager = LinearLayoutManager(requireContext())
        binding!!.rvdaydetail.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner, adapter::submit)
        viewModel.getFavExerciseList()
    }
    private fun discover() {
        findNavController().navigate(MoveListFragmentDirections.actionMoveListFragmentToIdHomepageFragment())
    }
    private fun inspect(exerciseId: Int) {
        val model = viewModel.state.value?.uniqueRecords?.firstOrNull { it.exerciseId == exerciseId } ?: return
        // Saved-item preview is read-only, including old locked favorites. It never grants access or starts a workout.
        dialog?.dismiss()
        val content = layoutInflater.inflate(R.layout.info_favourite_layout, null)
        content.findViewById<TextView>(R.id.exerciseFavouriteTitle).text = model.exerciseName
        content.findViewById<TextView>(R.id.exerciseFavouriteDescription).text = model.exerciseDescription
        content.findViewById<GifImageView>(R.id.infoFavouriteGif).setImageResource(ExerciseMedia.drawable(model))
        dialog = AlertDialog.Builder(requireContext()).setView(content)
            .setPositiveButton(R.string.close, null).create().also { it.show() }
    }
    private fun confirmRemove(exerciseId: Int) {
        if (viewModel.state.value?.removing != null) return
        if (viewModel.state.value?.uniqueRecords?.none { it.exerciseId == exerciseId } != false) return
        dialog?.dismiss()
        val content = layoutInflater.inflate(R.layout.dialog_delete_favourite, null)
        val confirmation = AlertDialog.Builder(requireContext()).setView(content).setCancelable(false).create()
        dialog = confirmation
        content.findViewById<View>(R.id.dialogNoFV).setOnClickListener { confirmation.dismiss() }
        content.findViewById<View>(R.id.dialogYesFV).setOnClickListener {
            viewModel.updateIsFavouriteToFalse(exerciseId)
            confirmation.dismiss()
        }
        confirmation.show()
    }
    override fun onDestroyView() {
        dialog?.dismiss()
        dialog = null
        binding?.rvdaydetail?.adapter = null
        binding = null
        super.onDestroyView()
    }
}
