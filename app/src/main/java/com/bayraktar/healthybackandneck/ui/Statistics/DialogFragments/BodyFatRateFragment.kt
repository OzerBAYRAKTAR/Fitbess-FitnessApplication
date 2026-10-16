package com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

        dataStoreManager = DataStoreManage.getInstance(requireContext())
        observeWeightAndHeight()
        checkBoxClicked()

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
        val height = inputHeight.text.toString().toIntOrNull()?.takeIf { it > 0 } ?: run {
            inputHeight.error = getString(R.string.label_inputattention)
            return@with
        }
        val neck = neckDesc.text.toString().toIntOrNull()?.takeIf { it > 0 } ?: run {
            neckDesc.error = getString(R.string.label_inputattention)
            return@with
        }
        val belly = bellyDesc.text.toString().toIntOrNull()?.takeIf { it > 0 } ?: run {
            bellyDesc.error = getString(R.string.label_inputattention)
            return@with
        }
        val butt = buttDesc.text.toString().toIntOrNull()?.takeIf { it > 0 } ?: run {
            buttDesc.error = getString(R.string.label_inputattention)
            return@with
        }
        if (!checkMale.isChecked && !checkFemale.isChecked) {
            showToast(requireContext(), getString(R.string.label_fillall), Gravity.CENTER, 0, 0)
            return@with
        }
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
        val errorMessage = getString(R.string.label_inputattention)

        val rate = bodyFatRate
        if (!rate.isFinite() || rate !in 3.0..100.0) {
            showToast(requireContext(),errorMessage,Gravity.CENTER,0,0)
        }else {

            val title = getString(R.string.label_bmi)
            dataStoreManager.saveFatrate(formattedRate)
            val message = "\n$labelBodyFatRate: $formattedRate "

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.custom_statistic_layout, null)

            val positiveBtn: Button = dialogView.findViewById(R.id.dialogYesss)
            val txtLabel: TextView = dialogView.findViewById(R.id.txtLabel)
            val txtFirst: TextView = dialogView.findViewById(R.id.txtFirst)


            val builder = AlertDialog.Builder(requireContext())

            builder.setView(dialogView)
            builder.setCancelable(false)

            txtLabel.text = title
            txtFirst.text = message

            val dialog = builder.create()
            dialog.show()

            positiveBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

    }

    private fun btnClicked() = with(binding) {
        btnSavee.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch { saveHeigtAndWeight() }
        }
        goBack.setOnClickListener {
            val action = BodyFatRateFragmentDirections.actionBodyFatRateFragmentToIdStatisticsFragment()
            view?.findNavController()?.navigate(action)
        }
    }


    private fun observeWeightAndHeight() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
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
                    if (gender == getString(R.string.label_male)) {
                        checkMale.isChecked = true
                    } else if (gender == getString(R.string.label_female)) {
                        checkFemale.isChecked = true
                    }
                }
            }.collectLatest {
                // You can add additional logic here if needed
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
