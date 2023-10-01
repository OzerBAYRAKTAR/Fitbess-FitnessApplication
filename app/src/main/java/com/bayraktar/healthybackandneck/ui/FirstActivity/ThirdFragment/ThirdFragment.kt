package com.bayraktar.healthybackandneck.ui.FirstActivity.ThirdFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentThirdBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.SecondFragment.SecondFragmentDirections


class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appCompatButton.setOnClickListener{
            val action = ThirdFragmentDirections.actionThirdFragmentToFourthFragment()
            view.findNavController().navigate(action)
        }
        binding.appCompatButton2.setOnClickListener{
            val action = ThirdFragmentDirections.actionThirdFragmentToSecondFragment()
            view.findNavController().navigate(action)
        }
    }


}