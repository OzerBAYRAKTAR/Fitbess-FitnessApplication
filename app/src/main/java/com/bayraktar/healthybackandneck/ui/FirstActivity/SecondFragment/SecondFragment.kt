package com.bayraktar.healthybackandneck.ui.FirstActivity.SecondFragment

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
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManage
    private var selectedAge = 25


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
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
            selectedAge = newVal
        }

    }

    private fun btnClicks() {

        binding.appCompatButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveAge(selectedAge)
            }
            val action = SecondFragmentDirections.actionSecondFragmentToThirdFragment()
            view?.findNavController()?.navigate(action)
        }

        binding.appCompatButton2.setOnClickListener {
            val action = SecondFragmentDirections.actionSecondFragmentToFirstFragment()
            view?.findNavController()?.navigate(action)
        }
    }

    private fun observeCalculates(){
        dataStoreManager.getAge().asLiveData().observe(viewLifecycleOwner) { age ->
            if (age != 0 ) {
                binding.numberPicker.value = age
            }
        }
    }

}