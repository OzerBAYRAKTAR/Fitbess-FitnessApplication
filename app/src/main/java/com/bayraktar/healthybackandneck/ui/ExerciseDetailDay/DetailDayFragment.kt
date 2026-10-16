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
import com.bayraktar.healthybackandneck.utils.PreloadedInterstitialAd
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentDetailDayBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerViewClickListener
import com.bayraktar.healthybackandneck.utils.RewardedAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDayFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentDetailDayBinding? = null
    val binding get() = _binding!!

    private var transitionAd: PreloadedInterstitialAd? = null

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
        transitionAd = PreloadedInterstitialAd(
            requireActivity(), viewLifecycleOwner, "ca-app-pub-1896269552628731/3768357562"
        )
        binding.prgrsDetail.visibility = View.GONE
        binding.startExercise.setOnClickListener {
            transitionAd?.showOrContinue {
                if (!isAdded || parentFragmentManager.isStateSaved) return@showOrContinue
                val controller = view?.findNavController() ?: return@showOrContinue
                if (controller.currentDestination?.id == R.id.detailDayFragment) {
                    val action = DetailDayFragmentDirections.actionDetailDayFragmentToExerciseMovesFragment(
                        exerciseList.toTypedArray(), exerciseDayModel
                    )
                    controller.navigate(action)
                }
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

        val activeSeconds = exerciseList.size * 30
        txttime.text = String.format(java.util.Locale.getDefault(), "%d:%02d", activeSeconds / 60, activeSeconds % 60)
        txtexercise.text = exerciseList.size.toString()
        binding.root.findViewById<View>(R.id.cardView5)?.visibility = View.GONE
        if (exerciseDayModel != null) txtdaysecond.text = exerciseDayModel?.day.toString()
        else { detaildaySecond.visibility = View.GONE; txtdaysecond.visibility = View.GONE }

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

    override fun onDestroyView() {
        transitionAd = null
        _binding = null
        super.onDestroyView()
    }
}