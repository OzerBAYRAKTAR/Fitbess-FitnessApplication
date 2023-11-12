package com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentBMIBinding
import com.bayraktar.healthybackandneck.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class BMIFragment : DialogFragment() {


    private var _binding: FragmentBMIBinding? = null
    val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManage
    private var bmiResult = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBMIBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeWeightAndHeight()
        dataStoreManager = DataStoreManage.getInstance(requireContext())

        btnClicked()

    }
    private suspend fun saveHeigtAndWeight() = with(binding) {
        val height = inputHeight.text.toString().toInt()
        val weight = inputWeightDesc.text.toString().toInt()


        dataStoreManager.saveHeight(height)
        dataStoreManager.saveWeight(weight)


        val percentHeight = (height/100.0)
        val bmi = weight/(percentHeight*percentHeight)
        val decimalFormat = DecimalFormat("#.##")
        val formattedBmi = decimalFormat.format(bmi)


        //formattedBmi ve bmiResult datastore oluştur, kaydet , getle

        val bmiResult = try {   //bmiResult -> kategori, formattedBmi -> endeks
            val bmiValue = formattedBmi.replace(',', '.').toDouble()

            when (bmiValue) {
                in Double.MIN_VALUE..18.5 -> getString(R.string.label_zayif)
                in 18.5..25.0 -> getString(R.string.label_normal)
                in 25.0..29.9 -> getString(R.string.label_weighted)
                in 30.0..1000.0 -> getString(R.string.label_fat)
                else -> getString(R.string.label_tryagain)
            }
        } catch (e: NumberFormatException) {
            getString(R.string.label_tryagain)
        }

        dataStoreManager.saveCategory(bmiResult)
        dataStoreManager.saveIndeks(formattedBmi)
        val title = getString(R.string.label_bmi)
        val message = "\nBMI: $formattedBmi kg/m2 \n \nCategory: $bmiResult"
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_okay)){_,_ ->
                val action = BMIFragmentDirections.actionBMIFragmentToIdStatisticsFragment()
                view?.findNavController()?.navigate(action)
            }.show()

    }

    private fun btnClicked() = with(binding) {
        btnSavee.setOnClickListener {

            val height = inputHeight.text.toString()
            val weight = inputWeightDesc.text.toString()

            if (height.isNotEmpty() && weight.isNotEmpty()) {
                try {
                    lifecycleScope.launch {
                        saveHeigtAndWeight()
                    }
                } catch (e: NumberFormatException) {
                    val message = getString(R.string.label_inputattention)
                    showToast(requireContext(), message, Gravity.CENTER, 0, 0)
                }
            } else {
                val message = getString(R.string.label_inputattention)
                showToast(requireContext(), message, Gravity.CENTER, 0, 0)
            }
        }
    }


    private fun observeWeightAndHeight() = with(binding) {
        lifecycleScope.launchWhenStarted {
            combine(
                dataStoreManager.getHeight(),
                dataStoreManager.getWeight()
            ) { height, weight ->
                if (height != 0) {
                    inputHeight.setText(height.toString())
                }
                if (weight != 0) {
                    inputWeightDesc.setText(weight.toString())
                }
            }.collectLatest {
            }
        }

    }
}
