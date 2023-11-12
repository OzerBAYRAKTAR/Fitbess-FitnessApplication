package com.bayraktar.healthybackandneck.ui.FirstActivity.ThirdFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentSecondBinding
import com.bayraktar.healthybackandneck.databinding.FragmentThirdBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.SecondFragment.SecondFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding?= null
    val binding get() = _binding!!
    private lateinit var dataStoreManager:DataStoreManage
    private var selectedHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManage.getInstance(requireContext())

        // Observe the age changes
        observeCalculates()
        btnClicks()

        binding.numberPicker.setOnValueChangedListener { xx, yy, newVal ->
            selectedHeight = newVal
        }

    }

    private fun btnClicks() {


        binding.appCompatButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveHeight(selectedHeight)
            }
            val action = ThirdFragmentDirections.actionThirdFragmentToFourthFragment()
            view?.findNavController()?.navigate(action)
        }

        binding.appCompatButton2.setOnClickListener {
            val action = ThirdFragmentDirections.actionThirdFragmentToSecondFragment()
            view?.findNavController()?.navigate(action)
        }
    }

    private fun observeCalculates() {
        dataStoreManager.getWeight().asLiveData().observe(viewLifecycleOwner) { height ->
            if (height != 0 ) {
                binding.numberPicker.value = height
            }
        }
    }

}