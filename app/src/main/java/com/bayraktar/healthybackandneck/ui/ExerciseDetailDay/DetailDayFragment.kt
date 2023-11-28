package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentDetailDayBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstFragmentDirections
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDayFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentDetailDayBinding? = null
    val binding get() = _binding!!

    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private lateinit var detailDayAdapter: DetailDayAdapter
    private var exerciseArray: Array<ExerciseDayExercise>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSetData()
        setRecyclerview()


        binding.startExercise.setOnClickListener {
            if (exerciseDayModel != null) {
                val action =
                    DetailDayFragmentDirections.actionDetailDayFragmentToExerciseMovesFragment(
                        exerciseList.toTypedArray(),exerciseDayModel!!)
                view.findNavController().navigate(action)
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

        val time = exerciseDayModel?.exerciseTime.toString()
        val formattedTime = "0$time:00"

        detaildayFirst.text = detailDay
        txttime.text = formattedTime
        txtexercise.text = exerciseDayModel?.exerciseCount.toString()
        txtkcal.text = exerciseDayModel?.exerciseKcal.toString()
        txtdaysecond.text = exerciseDayModel?.day.toString()

        detailDayAdapter = DetailDayAdapter(exerciseList, this@DetailDayFragment)
        rvdaydetail.adapter = detailDayAdapter

    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        val selectedModel = exerciseList[position]
        TODO("tıklayınca checkbox aktif veya pasif olacak, toast ile favoriye eklendi yazdırılacak")
    }


}