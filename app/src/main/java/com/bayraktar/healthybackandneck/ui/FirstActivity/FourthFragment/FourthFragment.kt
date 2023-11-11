package com.bayraktar.healthybackandneck.ui.FirstActivity.FourthFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFourthBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.ThirdFragment.ThirdFragmentDirections


class FourthFragment : Fragment() {

    private var _binding: FragmentFourthBinding?= null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFourthBinding.inflate(inflater,container,false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appCompatButton.setOnClickListener{
            val action = FourthFragmentDirections.actionFourthFragmentToFourthDottedFragment()
            view.findNavController().navigate(action)
        }
        binding.appCompatButton2.setOnClickListener{
            val action = FourthFragmentDirections.actionFourthFragmentToThirdFragment()
            view.findNavController().navigate(action)
        }

    }


}