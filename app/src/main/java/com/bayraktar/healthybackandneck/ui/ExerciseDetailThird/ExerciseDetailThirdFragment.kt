package com.bayraktar.healthybackandneck.ui.ExerciseDetailThird

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDetailSecondBinding
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDetailThirdBinding

class ExerciseDetailThirdFragment : Fragment() {

    private var _binding: FragmentExerciseDetailThirdBinding?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseDetailThirdBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

}