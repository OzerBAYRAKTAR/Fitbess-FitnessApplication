package com.bayraktar.healthybackandneck.ui.FirstActivity.SecondFragment

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentSecondBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.FirstFragment.FirstFragmentDirections


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding?= null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appCompatButton.setOnClickListener{
            val action = SecondFragmentDirections.actionSecondFragmentToThirdFragment()
            view.findNavController().navigate(action)
        }
        binding.appCompatButton2.setOnClickListener{
            val action = SecondFragmentDirections.actionSecondFragmentToFirstFragment()
            view.findNavController().navigate(action)
        }
    }


}