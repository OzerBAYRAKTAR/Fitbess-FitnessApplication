package com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentBMIBinding
import com.bayraktar.healthybackandneck.databinding.FragmentBodyFatRateBinding
import com.bayraktar.healthybackandneck.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.log10

class BodyFatRateFragment : Fragment() {

    private var _binding: FragmentBodyFatRateBinding? = null
    val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManage


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBodyFatRateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeWeightAndHeight()
        checkBoxClicked()
        dataStoreManager = DataStoreManage.getInstance(requireContext())

        btnClicked()


    }

    private fun checkBoxClicked() = with(binding) {
        checkMale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkFemale.isChecked = false
            }
        }
        checkFemale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkMale.isChecked = false
            }
        }
    }

    private fun calculateBodyFatRate(isMale: Boolean, bel: Double, kalca: Double, boyun: Double, boy: Double): Double {
        val formulaConstant = 495.0
        val logBelBoyunRatio = log10(bel - boyun)
        val logBelKalcaBoyunRatio = log10(bel + kalca - boyun)
        val logBoyRatio = log10(boy)

        return if (isMale) {
            formulaConstant / (1.0324 - 0.19077 * logBelBoyunRatio + 0.15456 * logBoyRatio) - 450
        } else {
            formulaConstant / (1.29579 - 0.35004 * logBelKalcaBoyunRatio + 0.22100 * logBoyRatio) - 450
        }
    }

    private suspend fun saveHeigtAndWeight() = with(binding) {
        val height = inputHeight.text.toString().toInt()
        val neck = neckDesc.text.toString().toInt()
        val belly = bellyDesc.text.toString().toInt()
        val butt = buttDesc.text.toString().toInt()
        val gender = if (checkMale.isChecked)  getString(R.string.label_male) else getString(R.string.label_female)



        dataStoreManager.saveHeight(height)
        dataStoreManager.saveNeck(neck)
        dataStoreManager.saveBelly(belly)
        dataStoreManager.saveBooty(butt)
        dataStoreManager.saveGender(gender)

        val isMale = checkMale.isChecked

        val bodyFatRate = calculateBodyFatRate(isMale, belly.toDouble(), butt.toDouble(), neck.toDouble(), height.toDouble())
        val labelBodyFatRate = getString(R.string.label_yourfatrate)

        val decimalFormat = DecimalFormat("#.###")
        val formattedRate = decimalFormat.format(bodyFatRate)
        val errorMessage = "Yanlış değer girdiniz, Lütfen tekrar deneyiniz!"

        val rate = formattedRate.replace(",", ".").toDouble()
        if (rate < 3) {
            showToast(requireContext(),errorMessage,Gravity.CENTER,0,0)
        }else {
            dataStoreManager.saveFatrate(formattedRate)
            val title = getString(R.string.label_bmi)
            val message = "\n$labelBodyFatRate: $formattedRate "
            AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.label_okay)){_,_ ->
                    val action = BodyFatRateFragmentDirections.actionBodyFatRateFragmentToIdStatisticsFragment()
                    view?.findNavController()?.navigate(action)
                }.show()
        }

    }

    private fun btnClicked() = with(binding) {
        btnSavee.setOnClickListener {

            val height = inputHeight.text.toString()
            val neck = neckDesc.text.toString()
            val belly = bellyDesc.text.toString()
            val butt = buttDesc.text.toString()

            if (height.isNotEmpty() && neck.isNotEmpty() && belly.isNotEmpty() && butt.isNotEmpty()) {
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
                dataStoreManager.getNeck(),
                dataStoreManager.getBelly(),
                dataStoreManager.getBooty(),
                dataStoreManager.getGender(),

            ) { height, neck, belly, booty, gender ->
                if (height != 0) {
                    inputHeight.setText(height.toString())
                }
                if (neck != 0) {
                    neckDesc.setText(neck.toString())
                }
                if (belly != 0) {
                    bellyDesc.setText(belly.toString())
                }
                if (booty != 0) {
                    buttDesc.setText(booty.toString())
                }
                if (gender != "") {
                    if (gender == "Erkek") {
                        checkMale.isChecked = true
                    }else {
                        checkFemale.isChecked = true
                    }
                }
            }.collectLatest {
                // You can add additional logic here if needed
            }
        }

    }
}
