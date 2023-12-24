package com.bayraktar.healthybackandneck.ui.FirstActivity.FivethFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFivethBinding


class FivethFragment : Fragment() {
    private var _binding: FragmentFivethBinding?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFivethBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeBackground()

        binding.appCompatButton.setOnClickListener{
            val action = FivethFragmentDirections.actionFivethFragmentToSixthFragment()
            view.findNavController().navigate(action)
        }
        binding.appCompatButton2.setOnClickListener{
            val action = FivethFragmentDirections.actionFivethFragmentToFourthDottedFragment()
            view.findNavController().navigate(action)
        }
    }
    private fun changeBackground() = with(binding) {
        card1.setOnClickListener {
            if (cns1.background != null) {
                cns1.background = null
                check1.isChecked = false
            }else {
                cns1.setBackgroundResource(R.drawable.firstselected_background)
                check1.isChecked = true
            }
        }
        card2.setOnClickListener {
            if (cns2.background != null) {
                cns2.background = null
                check2.isChecked = false
            } else {
                cns2.setBackgroundResource(R.drawable.firstselected_background)
                check2.isChecked = true
            }
        }
        card3.setOnClickListener {
            if (cns3.background != null) {
                cns3.background = null
                check3.isChecked = false
            } else {
                cns3.setBackgroundResource(R.drawable.firstselected_background)
                check3.isChecked = true
            }
        }
        card4.setOnClickListener {
            if (cns4.background != null) {
                cns4.background = null
                check4.isChecked = false
            } else {
                cns4.setBackgroundResource(R.drawable.firstselected_background)
                check4.isChecked = true
            }
        }
        card5.setOnClickListener {
            if (cns5.background != null) {
                cns5.background = null
                check5.isChecked = false
            } else {
                cns5.setBackgroundResource(R.drawable.firstselected_background)
                check5.isChecked = true
            }
        }

    }

}