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
    private var _binding: FragmentFivethBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFivethBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeBackground()

        binding.appCompatButton.setOnClickListener {
            val action = FivethFragmentDirections.actionFivethFragmentToSixthFragment()
            view.findNavController().navigate(action)
        }
        binding.appCompatButton2.setOnClickListener {
            val action = FivethFragmentDirections.actionFivethFragmentToFourthDottedFragment()
            view.findNavController().navigate(action)
        }
    }

    private fun changeBackground() = with(binding) {

        card1.setOnClickListener {
            cns1.setBackgroundResource(R.drawable.firstselected_background)
            check1.isChecked = true


            cns2.background = null
            cns3.background = null
            cns4.background = null
            cns5.background = null
            check2.isChecked = false
            check3.isChecked = false
            check4.isChecked = false
            check5.isChecked = false

        }
        card2.setOnClickListener {

            cns2.setBackgroundResource(R.drawable.firstselected_background)
            check2.isChecked = true

            cns1.background = null
            cns3.background = null
            cns4.background = null
            cns5.background = null
            check1.isChecked = false
            check3.isChecked = false
            check4.isChecked = false
            check5.isChecked = false

        }
        card3.setOnClickListener {

            cns3.setBackgroundResource(R.drawable.firstselected_background)
            check3.isChecked = true

            cns2.background = null
            cns1.background = null
            cns4.background = null
            cns5.background = null
            check2.isChecked = false
            check1.isChecked = false
            check4.isChecked = false
            check5.isChecked = false
        }

        card4.setOnClickListener {

            cns4.setBackgroundResource(R.drawable.firstselected_background)
            check4.isChecked = true

            cns2.background = null
            cns3.background = null
            cns1.background = null
            cns5.background = null
            check2.isChecked = false
            check3.isChecked = false
            check1.isChecked = false
            check5.isChecked = false
        }

        card5.setOnClickListener {

            cns5.setBackgroundResource(R.drawable.firstselected_background)
            check5.isChecked = true

            cns2.background = null
            cns3.background = null
            cns4.background = null
            cns1.background = null
            check2.isChecked = false
            check3.isChecked = false
            check4.isChecked = false
            check1.isChecked = false
        }


    }

}