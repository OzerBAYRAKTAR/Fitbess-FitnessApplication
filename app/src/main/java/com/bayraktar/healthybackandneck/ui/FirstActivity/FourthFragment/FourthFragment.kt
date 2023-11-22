package com.bayraktar.healthybackandneck.ui.FirstActivity.FourthFragment

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
import com.bayraktar.healthybackandneck.databinding.FragmentFourthBinding
import com.bayraktar.healthybackandneck.databinding.FragmentThirdBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.SecondFragment.SecondFragmentDirections
import com.bayraktar.healthybackandneck.ui.FirstActivity.ThirdFragment.ThirdFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class FourthFragment : Fragment() {

    private var _binding: FragmentFourthBinding? = null
    val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManage
    private var selectedWeight = 65

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFourthBinding.inflate(inflater, container, false)
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
            selectedWeight = newVal
        }

    }

    private fun btnClicks() {


        binding.appCompatButton.setOnClickListener {
            println(selectedWeight)
            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveWeight(selectedWeight)
            }
            val action = FourthFragmentDirections.actionFourthFragmentToFourthDottedFragment()
            view?.findNavController()?.navigate(action)
        }

        binding.appCompatButton2.setOnClickListener {
            val action = FourthFragmentDirections.actionFourthFragmentToThirdFragment()
            view?.findNavController()?.navigate(action)
        }
    }

    private fun observeCalculates(){
        dataStoreManager.getWeight().asLiveData().observe(viewLifecycleOwner) { weight ->
            if (weight != 0) {
                binding.numberPicker.value = weight
            }
        }
    }
}
