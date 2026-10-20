package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentDetailDayBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.MoveAccess
import com.bayraktar.healthybackandneck.utils.MoveAccessViewModel
import com.bayraktar.healthybackandneck.utils.PreloadedInterstitialAd
import com.bayraktar.healthybackandneck.utils.RewardedAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView

@AndroidEntryPoint
class DetailDayFragment : Fragment(), RecyclerViewClickListener {
    private var _binding: FragmentDetailDayBinding? = null
    val binding get() = _binding!!
    private val access: MoveAccessViewModel by viewModels()
    private var transitionAd: PreloadedInterstitialAd? = null
    private var rewarded: RewardedAds? = null
    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = emptyList<ExerciseDayExercise>()
    private var adapter: DetailDayAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentDetailDayBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, state: Bundle?) {
        val args = DetailDayFragmentArgs.fromBundle(requireArguments())
        exerciseDayModel = args.exerciseDayModel
        exerciseList = args.exerciseNewList.orEmpty().toList()
        binding.detaildayFirst.text = args.exerciseLevel
        binding.detaildaySecond.visibility = if (exerciseDayModel == null) View.GONE else View.VISIBLE
        binding.txtdaysecond.visibility = binding.detaildaySecond.visibility
        binding.txtdaysecond.text = exerciseDayModel?.day?.toString().orEmpty()
        binding.backButton.setOnClickListener { view.findNavController().popBackStack() }
        adapter = DetailDayAdapter(exerciseList, this)
        binding.rvdaydetail.layoutManager = LinearLayoutManager(requireContext())
        binding.rvdaydetail.adapter = adapter
        transitionAd = PreloadedInterstitialAd(requireActivity(), viewLifecycleOwner, getString(R.string.ad_interstitial_detail_day))
        rewarded = RewardedAds(requireActivity(), viewLifecycleOwner).also { it.loadRewardedAds(R.string.rewarded_ad1) }
        binding.startExercise.setOnClickListener {
            val list = available()
            if (list.isEmpty()) {
                if (canUnlockCurrentLevel()) confirmLevelUnlock()
                return@setOnClickListener
            }
            transitionAd?.showOrContinue {
                if (!isAdded || parentFragmentManager.isStateSaved) return@showOrContinue
                val controller = view.findNavController()
                if (controller.currentDestination?.id == R.id.detailDayFragment) controller.navigate(
                    DetailDayFragmentDirections.actionDetailDayFragmentToExerciseMovesFragment(list.toTypedArray(), exerciseDayModel)
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { access.earned.collect { render() } }
                launch { access.adFree.collect { render() } }
                launch { access.failed.collect { render() } }
            }
        }
        render()
    }
    private fun available(): List<ExerciseDayExercise> {
        val earned = access.earned.value ?: return emptyList()
        val adFree = access.adFree.value
        if (adFree == null && exerciseList.any(MoveAccess::requiresReward)) return emptyList()
        return exerciseList.filter { MoveAccess.allowed(it, earned, adFree == true) }
    }
    private fun render() {
        if (_binding == null) return
        val list = available()
        val loading = access.earned.value == null ||
                (access.adFree.value == null && exerciseList.any(MoveAccess::requiresReward))
        val earned = access.earned.value.orEmpty()
        val levelUnlockIds = if (exerciseDayModel == null) MoveAccess.levelUnlockIds(exerciseList) else emptySet()
        val lockedLevelMoves = levelUnlockIds.count { it !in earned }
        val canUnlockLevel = !loading && access.adFree.value != true && lockedLevelMoves > 0
        val unlockAction = list.isEmpty() && canUnlockLevel
        adapter?.earned = access.earned.value
        adapter?.adFree = access.adFree.value == true
        adapter?.notifyDataSetChanged()
        binding.startExercise.isEnabled = !loading && (list.isNotEmpty() || canUnlockLevel)
        binding.startExercise.text = when {
            unlockAction -> getString(R.string.unlock_level_to_start)
            list.isEmpty() -> getString(R.string.unlock_to_start)
            else -> getString(R.string.start_available, list.size)
        }
        binding.startExercise.icon = if (unlockAction) ContextCompat.getDrawable(requireContext(), R.drawable.fit_lock) else null
        if (unlockAction) {
            val lockedTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.fit_border))
            val lockedContentTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.fit_text))
            binding.startExercise.backgroundTintList = lockedTint
            binding.startExercise.setTextColor(lockedContentTint)
            binding.startExercise.iconTint = lockedContentTint
        } else {
            binding.startExercise.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.fit_button_background)
            binding.startExercise.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.fit_button_text))
            binding.startExercise.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.fit_button_text)
        }
        binding.txtexercise.text = getString(R.string.available_moves, list.size, exerciseList.size)
        val seconds = list.size * 30
        binding.txttime.text = String.format(java.util.Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60)
        binding.accessStatus.visibility = if (loading || access.failed.value) View.VISIBLE else View.GONE
        binding.accessStatus.setText(if (access.failed.value) R.string.access_failed else R.string.access_loading)
    }
    override fun recyclerviewListClicked(v: View, position: Int) {
        val model = exerciseList.getOrNull(position) ?: return
        val earned = access.earned.value ?: return
        if (!MoveAccess.allowed(model, earned, access.adFree.value == true)) {
            AlertDialog.Builder(requireContext()).setTitle(R.string.unlock_move_title)
                .setMessage(getString(R.string.unlock_move_choice_message))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.watch_unlock) { _, _ ->
                    rewarded?.showRewardAds(
                        retainEarnedReward = true,
                        onStateChanged = { state ->
                            if (state == RewardedAds.ShowState.PREPARING && isAdded) {
                                android.widget.Toast.makeText(requireContext(), R.string.ad_preparing, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                    ) {
                        access.grant(model)
                    }
                }
                .setNeutralButton(R.string.remove_ads) { _, _ ->
                    view?.findNavController()?.navigate(R.id.action_global_adFreeOfferFragment)
                }
                .show()
            return
        }
        val preview = layoutInflater.inflate(R.layout.info_favourite_layout, null)
        preview.findViewById<TextView>(R.id.exerciseFavouriteTitle).text = model.exerciseName
        preview.findViewById<TextView>(R.id.exerciseFavouriteDescription).text = model.exerciseDescription
        preview.findViewById<GifImageView>(R.id.infoFavouriteGif).setImageResource(model.image)
        AlertDialog.Builder(requireContext()).setView(preview).setPositiveButton(R.string.close, null).show()
    }

    private fun confirmLevelUnlock() {
        val ids = MoveAccess.levelUnlockIds(exerciseList)
        val earned = access.earned.value.orEmpty()
        val lockedCount = ids.count { it !in earned }
        if (lockedCount == 0 || access.adFree.value == true) return
        AlertDialog.Builder(requireContext()).setTitle(R.string.unlock_level_title)
            .setMessage(getString(R.string.unlock_level_choice_message, lockedCount))
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.watch_unlock_level) { _, _ ->
                rewarded?.showRewardAds(
                    retainEarnedReward = true,
                    onStateChanged = { state ->
                        if (state == RewardedAds.ShowState.PREPARING && isAdded) {
                            android.widget.Toast.makeText(requireContext(), R.string.ad_preparing, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                ) { access.grantLevel(exerciseList) }
            }
            .setNeutralButton(R.string.remove_ads) { _, _ ->
                view?.findNavController()?.navigate(R.id.action_global_adFreeOfferFragment)
            }
            .show()
    }

    private fun canUnlockCurrentLevel(): Boolean =
        exerciseDayModel == null && access.adFree.value != true &&
                MoveAccess.levelUnlockIds(exerciseList).any { it !in access.earned.value.orEmpty() }
    override fun onDestroyView() {
        binding.rvdaydetail.adapter = null
        adapter = null
        rewarded = null
        transitionAd = null
        _binding = null
        super.onDestroyView()
    }
}
