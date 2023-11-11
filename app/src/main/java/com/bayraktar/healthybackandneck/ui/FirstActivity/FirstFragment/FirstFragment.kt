package com.bayraktar.healthybackandneck.ui.FirstActivity.FirstFragment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentFirstBinding
import com.bayraktar.healthybackandneck.ui.HomeActivity

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBackgrounds()

        binding.goNext.setOnClickListener{
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment()
            view.findNavController().navigate(action)
            //startActivity(Intent(requireActivity(),HomeActivity::class.java))
        }
    }
    private fun viewBackgrounds() = with(binding) {
        viewfemale.setOnClickListener {
            viewfemale.setBackgroundResource(R.drawable.oval_yellow)
            viewmale.setBackgroundResource(R.drawable.oval_gray)
        }
        viewmale.setOnClickListener {
            viewmale.setBackgroundResource(R.drawable.oval_yellow)
            viewfemale.setBackgroundResource(R.drawable.oval_gray)
        }
    }



}