package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentDetailDayBinding
import com.bayraktar.healthybackandneck.ui.HomeActivity.Companion.mInterstitialAd
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.RewardedAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDayFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentDetailDayBinding? = null
    val binding get() = _binding!!

    //private var mInterstitialAd: InterstitialAd? = null

    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private var subExerciseList = ArrayList<SubExerciseDayExercise>()
    private var detailDayAdapter: DetailDayAdapter? = null
    private var subDetailDayAdapter: DetailDaySubAdapter? = null
    private var exerciseArray: Array<ExerciseDayExercise>? = null
    private var subExerciseArray: Array<SubExerciseDayExercise>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDayBinding.inflate(inflater, container, false)





        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //intersteallar test id => ca-app-pub-3940256099942544/1033173712
        //intersteallar canlı id => ca-app-pub-4754194669476617/8160142148



        adMOb()
        getSetData()
        //setRecyclerview()
        backstack()


    }
    private fun adMOb() {
        binding.startExercise.setOnClickListener {
            requireActivity().runOnUiThread {
                val adRequest = AdRequest.Builder().build()

                InterstitialAd.load(
                    requireContext(),
                    "ca-app-pub-4754194669476617/8160142148",
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            println("yy")
                            mInterstitialAd = null
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            mInterstitialAd = interstitialAd
                            mInterstitialAd?.show(requireActivity())
                            println("aaa")

                            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                                override fun onAdClicked() {
                                    // Called when a click is recorded for an ad.
                                    println("ccc")
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    println("vvv")
                                    mInterstitialAd = null
                                    // Called when ad is dismissed.
                                    val action = DetailDayFragmentDirections.actionDetailDayFragmentToExerciseMovesFragment(
                                        exerciseList.toTypedArray(), exerciseDayModel
                                    )
                                    view?.findNavController()?.navigate(action)
                                }

                                override fun onAdImpression() {
                                    println("qqq")
                                    // Called when an impression is recorded for an ad.
                                    Log.d(TAG, "Ad recorded an impression.")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    println("fff")
                                    // Called when ad is shown.
                                    Log.d(TAG, "Ad showed fullscreen content.")
                                }
                            }

                        }
                    })
            }
        }


    }

    private fun setRecyclerview() = with(binding) {
        rvdaydetail.layoutManager = LinearLayoutManager(requireContext())
        detailDayAdapter = DetailDayAdapter(exerciseList, this@DetailDayFragment)
        rvdaydetail.adapter = detailDayAdapter
    }

    private fun getSetData() = with(binding) {
        val args = DetailDayFragmentArgs.fromBundle(requireArguments())
        exerciseDayModel = args.exerciseDayModel

        exerciseArray = args.exerciseNewList


        exerciseList = ArrayList(exerciseArray!!.asList())

        val detailDay = args.exerciseLevel
        detaildayFirst.text = detailDay

        if (exerciseDayModel != null) {
            val time = exerciseDayModel?.exerciseTime.toString()
            val formattedTime = "0$time:00"

            txttime.text = formattedTime
            txtexercise.text = exerciseDayModel?.exerciseCount.toString()
            txtkcal.text = exerciseDayModel?.exerciseKcal.toString()
            txtdaysecond.text = exerciseDayModel?.day.toString()
        }else {
            detaildaySecond.visibility = View.GONE
            txtdaysecond.visibility = View.GONE

            txttime.text = "07:30"
            txtexercise.text = "7"
            txtkcal.text = "300"
        }


        rvdaydetail.layoutManager = LinearLayoutManager(requireContext())
        detailDayAdapter = DetailDayAdapter(exerciseList, this@DetailDayFragment)
        rvdaydetail.adapter = detailDayAdapter


    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        val selectedModel = exerciseList[position]
        println("xx")
        //TODO("tıklayınca checkbox aktif veya pasif olacak, toast ile favoriye eklendi yazdırılacak")
    }

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = DetailDayFragmentDirections.actionDetailDayFragmentToIdHomepageFragment()
                view?.findNavController()?.navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}