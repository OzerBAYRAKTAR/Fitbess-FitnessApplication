package com.bayraktar.healthybackandneck.ui.ExerciseMovesEnd

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesEndBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayFragmentDirections
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentArgs
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.ui.HomeActivity.Companion.mInterstitialAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesEndFragment : Fragment() {

    private var _binding: FragmentExerciseMovesEndBinding? = null
    val binding get() = _binding!!
    private val viewModel: ExerciseMovesEndViewModel by viewModels()

    private var mInterstitialAd: InterstitialAd? = null

    private var isAdLoaded = false

    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private var exerciseArray: Array<ExerciseDayExercise>? = null
    private var adClicked = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesEndBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //interstealler canlı id => ca-app-pub-4754194669476617/6013325105
        //interstealler test id => ca-app-pub-3940256099942544/1033173712

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), "ca-app-pub-4754194669476617/6013325105", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })




        backstack()
        getSetData()
        binding.btnEnd.setOnClickListener {
            btnEndClicked()
        }
    }

    private fun btnCloseClicked() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_cancel, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        val positiveBtn: Button = dialogView.findViewById(R.id.dialogYes)
        val negativeBtn: Button = dialogView.findViewById(R.id.dialogNo)

        negativeBtn.setOnClickListener {
            dialog.dismiss()
        }
        positiveBtn.setOnClickListener {
            val action =
                ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToIdHomepageFragment()
            view?.findNavController()?.navigate(action)
            dialog.dismiss()
        }

    }

    private fun btnEndClicked() {
        if (mInterstitialAd != null ) {
            mInterstitialAd?.show(requireActivity())
            Log.d(TAG, "Null değil.")
        }
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                println("kapandı5")
                // Called when a click is recorded for an ad.
                Log.d(ContentValues.TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                println("kapandı")
                mInterstitialAd = null
                // Called when ad is dismissed.
                val action = ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToIdHomepageFragment()
                view?.findNavController()?.navigate(action)
            }
            override fun onAdImpression() {
                println("kapandı1")
                // Called when an impression is recorded for an ad.
                Log.d(ContentValues.TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                println("kapandı3")
                // Called when ad is shown.
                Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
            }
        }

    }

    private fun getSetData() = with(binding) {
        val args = ExerciseMovesEndFragmentArgs.fromBundle(requireArguments())

        viewModel.updatecount()

        exerciseArray = args.exerciseNewList
        exerciseDayModel = args.exerciseDayModel

        if (exerciseDayModel!!.exerciseCount > 1) {
            exerciseList = ArrayList(exerciseArray!!.asList())
            txttime.text = exerciseDayModel?.exerciseTime.toString()
            txtexercise.text = exerciseDayModel?.exerciseCount.toString()
            txtkcal.text = exerciseDayModel?.exerciseKcal.toString()


            viewModel.updateIsCompletedToTrue(exerciseDayModel!!.level,exerciseDayModel!!.day + 1)

        }
    }


    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                btnCloseClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}