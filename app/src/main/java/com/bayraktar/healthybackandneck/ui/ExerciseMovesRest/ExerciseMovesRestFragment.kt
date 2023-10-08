package com.bayraktar.healthybackandneck.ui.ExerciseMovesRest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesRestBinding

class ExerciseMovesRestFragment : Fragment() {

    private var _binding: FragmentExerciseMovesRestBinding?= null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesRestBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.skipRest.setOnClickListener {
            val action= ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesEndFragment()
            view.findNavController().navigate(action)
        }
    }




}