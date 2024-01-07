package com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentBMIBinding
import com.bayraktar.healthybackandneck.databinding.FragmentDailyCalorieBinding
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import com.bayraktar.healthybackandneck.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.log10


class DailyCalorieFragment : Fragment() {

    private var _binding: FragmentDailyCalorieBinding? = null
    val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManage
    private var activityLevelList = ArrayList<String>()
    private var shouldObserveData: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDailyCalorieBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dataStoreManager = DataStoreManage.getInstance(requireContext())

        observeWeightAndHeight()
        checkBoxClicked()

        btnClicked()
        setSpinner()

    }

    private fun setSpinner() = with(binding) {
        val l1: String = getString(R.string.label_low)
        val l2: String = getString(R.string.label_middle)
        val l3: String = getString(R.string.label_high)
        val l4: String = getString(R.string.label_toohigh)

        activityLevelList.add(l1)
        activityLevelList.add(l2)
        activityLevelList.add(l3)
        activityLevelList.add(l4)
        val levelAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            activityLevelList
        )
        spnrActivity.adapter = levelAdapter

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

    private fun calculateDailyCalorieRequirement(
        isMale: Boolean,
        height: Double,
        weight: Double,
        age: Double,
        position: Int
    ): Double {
        val maleMetabolism = 66.5
        val femaleMetabolism = 655.1

        return if (isMale) {
            val maleStatic = maleMetabolism + (13.75 * weight) + (5 * height) - (6.77 * age)
            when (position) {
                1 -> {
                    maleStatic * 1.2
                }

                2 -> {
                    maleStatic * 1.3
                }

                3 -> {
                    maleStatic * 1.4
                }

                4 -> {
                    maleStatic * 1.5
                }

                else -> {
                    0.00
                }
            }
        } else {
            val femaleStatic = femaleMetabolism + (9.56 * weight) + (1.85 * height) - (4.67 * age)
            when (position) {
                1 -> {
                    femaleStatic * 1.2
                }

                2 -> {
                    femaleStatic * 1.3
                }

                3 -> {
                    femaleStatic * 1.4
                }

                4 -> {
                    femaleStatic * 1.5
                }

                else -> {
                    0.00
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private suspend fun saveHeigtAndWeight() = with(binding) {
        val height = inputHeight.text.toString().toInt()
        val weight = inputWeight.text.toString().toInt()
        val age = inputAge.text.toString().toInt()
        val activityPosition = spnrActivity.selectedItemPosition
        val gender = if (checkMale.isChecked)  getString(R.string.label_male) else getString(R.string.label_female)

        shouldObserveData = false

        dataStoreManager.saveHeight(height)
        dataStoreManager.saveWeight(weight)
        dataStoreManager.saveAge(age)
        dataStoreManager.saveActivityLevel(activityPosition + 1)
        dataStoreManager.saveGender(gender)

        shouldObserveData = true
        val isMale = checkMale.isChecked

        val dailyCalorieReq = calculateDailyCalorieRequirement(
            isMale,
            height.toDouble(),
            weight.toDouble(),
            age.toDouble(),
            (activityPosition + 1),
        )
        val labelDailyCalorieRequirement = getString(R.string.label_yourdailycaloriereq)

        val decimalFormat = DecimalFormat("#.###")
        val formattedRate = decimalFormat.format(dailyCalorieReq)
        val errorMessage = getString(R.string.label_tryagain)

        val rate = formattedRate.replace(",", ".").toDouble()
        if (rate < 3) {
            showToast(requireContext(), errorMessage, Gravity.CENTER, 0, 0)
        } else {
            val title = getString(R.string.label_dailyCalorieRequirement)
            dataStoreManager.saveCalorie(formattedRate)
            val message = "\n$labelDailyCalorieRequirement: $formattedRate "

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

            val height = inputHeight.text.toString()
            val weight = inputWeight.text.toString()
            val age = inputAge.text.toString()

            if (height.isNotEmpty() && weight.isNotEmpty() && age.isNotEmpty()) {
                try {
                    lifecycleScope.launch {
                        saveHeigtAndWeight()
                    }
                } catch (e: NumberFormatException) {
                    val message = getString(R.string.label_inputattention)
                    showToast(requireContext(), message, Gravity.CENTER, 0, 0)
                }
            } else {
                val message = getString(R.string.label_fillall)
                showToast(requireContext(), message, Gravity.CENTER, 0, 0)
            }
        }
        goBack.setOnClickListener {
            val action =
                DailyCalorieFragmentDirections.actionDailyCalorieFragmentToIdStatisticsFragment()
            view?.findNavController()?.navigate(action)
        }
    }


    private fun observeWeightAndHeight() = with(binding) {
        lifecycleScope.launch(Dispatchers.Main) {
            combine(
                dataStoreManager.getHeight(),
                dataStoreManager.getWeight(),
                dataStoreManager.getAge(),
                dataStoreManager.getActivityLevel(),
                dataStoreManager.getGender(),

                ) { height, weight, age, level,gender ->
                if (shouldObserveData) {
                    if (height != 0) {
                        inputHeight.setText(height.toString())
                    }
                    if (weight != 0) {
                        inputWeight.setText(weight.toString())
                    }
                    if (age != 0) {
                        inputAge.setText(age.toString())
                    }
                    spnrActivity.setSelection(level-1)
                    if (gender != "") {
                        if (gender == "Erkek") {
                            checkMale.isChecked = true
                        }else {
                            checkFemale.isChecked = true
                        }
                    }
                }

            }.collect() {
            }
        }

    }
}
