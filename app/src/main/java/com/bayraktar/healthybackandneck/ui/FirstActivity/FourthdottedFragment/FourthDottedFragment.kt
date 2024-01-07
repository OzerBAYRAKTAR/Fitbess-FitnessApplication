package com.bayraktar.healthybackandneck.ui.FirstActivity.FourthdottedFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentFivethBinding
import com.bayraktar.healthybackandneck.databinding.FragmentFourthDottedBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.FivethFragment.FivethFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FourthDottedFragment : Fragment() {

    private var _binding: FragmentFourthDottedBinding? = null
    val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFourthDottedBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManage.getInstance(requireContext())
        changeBackground()
        observeCalculates()

        binding.appCompatButton.setOnClickListener {
            val action = FourthDottedFragmentDirections.actionFourthDottedFragmentToFivethFragment()
            view.findNavController().navigate(action)
        }
        binding.appCompatButton2.setOnClickListener {
            val action = FourthDottedFragmentDirections.actionFourthDottedFragmentToFourthFragment()
            view.findNavController().navigate(action)
        }
    }

    private fun observeCalculates() = with(binding) {
        dataStoreManager.getActivityLevel().asLiveData().observe(viewLifecycleOwner) { level ->
            if (level != 0) {
                when (level) {
                    1 -> {
                        cns1.setBackgroundResource(R.drawable.firstselected_background)
                        check1.isChecked = true
                    }
                    2 -> {
                        cns2.setBackgroundResource(R.drawable.firstselected_background)
                        check2.isChecked = true
                    }
                    3 -> {
                        cns3.setBackgroundResource(R.drawable.firstselected_background)
                        check3.isChecked = true
                    }
                    4 -> {
                        cns4.setBackgroundResource(R.drawable.firstselected_background)
                        check4.isChecked = true
                    }

                }
            }
        }
    }


    private fun changeBackground() = with(binding) {
        card1.setOnClickListener {
            cns1.setBackgroundResource(R.drawable.firstselected_background)
            check1.isChecked = true

            cns2.background = null
            cns3.background = null
            cns4.background = null
            check2.isChecked = false
            check3.isChecked = false
            check4.isChecked = false

            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveActivityLevel(1)
            }
        }

        card2.setOnClickListener {
            cns2.setBackgroundResource(R.drawable.firstselected_background)
            check2.isChecked = true
            cns1.background = null
            cns3.background = null
            cns4.background = null
            check1.isChecked = false
            check3.isChecked = false
            check4.isChecked = false

            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveActivityLevel(2)
            }
        }
        card3.setOnClickListener {
            cns3.setBackgroundResource(R.drawable.firstselected_background)
            check3.isChecked = true
            cns2.background = null
            cns1.background = null
            cns4.background = null
            check2.isChecked = false
            check1.isChecked = false
            check4.isChecked = false

            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveActivityLevel(3)
            }
        }
        card4.setOnClickListener {
            cns4.setBackgroundResource(R.drawable.firstselected_background)
            check4.isChecked = true
            cns2.background = null
            cns3.background = null
            cns1.background = null
            check2.isChecked = false
            check3.isChecked = false
            check1.isChecked = false

            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveActivityLevel(4)
            }
        }

    }

}