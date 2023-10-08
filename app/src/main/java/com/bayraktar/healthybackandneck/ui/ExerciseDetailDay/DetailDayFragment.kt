package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentDetailDayBinding
import com.bayraktar.healthybackandneck.utils.homeFragmentListener

class DetailDayFragment : Fragment() {

    private var _binding: FragmentDetailDayBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.startExercise.setOnClickListener {
            val action = DetailDayFragmentDirections.actionDetailDayFragmentToExerciseMovesFragment()
            view.findNavController().navigate(action)
        }
    }


}