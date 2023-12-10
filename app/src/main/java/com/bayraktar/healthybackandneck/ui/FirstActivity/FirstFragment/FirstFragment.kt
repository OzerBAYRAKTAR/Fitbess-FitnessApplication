package com.bayraktar.healthybackandneck.ui.FirstActivity.FirstFragment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentFirstBinding
import com.bayraktar.healthybackandneck.ui.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding?= null
    val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManage

    private var selectedFemaleViewId: Int = -1
    private var selectedMaleViewId: Int = -1

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
        dataStoreManager = DataStoreManage.getInstance(requireContext())

        binding.goNext.setOnClickListener{
            lifecycleScope.launchWhenStarted {
                saveGen()
            }
           //val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment()
           //view.findNavController().navigate(action)
            startActivity(Intent(requireActivity(),HomeActivity::class.java))
        }
    }
    private suspend fun saveGen() {
        val gender = if(isFemaleSelected()) "Kadın" else "Erkek"
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.saveGender(gender)
        }
    }
    private fun isFemaleSelected(): Boolean {
        return selectedFemaleViewId != -1 && selectedFemaleViewId == binding.viewfemale.id
    }

    private fun viewBackgrounds() = with(binding) {
        viewfemale.setOnClickListener {
            handleViewClick(viewfemale, R.drawable.oval_yellow, selectedFemaleViewId)
            viewmale.setBackgroundResource(R.drawable.oval_gray)
        }
        viewmale.setOnClickListener {
            handleViewClick(viewmale, R.drawable.oval_yellow, selectedMaleViewId)
            viewfemale.setBackgroundResource(R.drawable.oval_gray)
        }
    }

    private fun handleViewClick(view: View, backgroundResId: Int, selectedViewId: Int) {
        // Reset the background of the previously selected View
        selectedViewId.takeIf { it != -1 }?.let { previousSelectedId ->
            val previousSelectedView = view?.findViewById<View>(previousSelectedId)
            setViewBackground(previousSelectedView, R.drawable.oval_gray)
        }

        // Update the background of the clicked View
        setViewBackground(view, backgroundResId)

        // Update the currently selected View ID
        when (view.id) {
            binding.viewfemale.id -> selectedFemaleViewId = view.id
            binding.viewmale.id -> selectedMaleViewId = view.id
        }
    }

    private fun setViewBackground(view: View?, backgroundResId: Int) {
        view?.setBackgroundResource(backgroundResId)
    }
}