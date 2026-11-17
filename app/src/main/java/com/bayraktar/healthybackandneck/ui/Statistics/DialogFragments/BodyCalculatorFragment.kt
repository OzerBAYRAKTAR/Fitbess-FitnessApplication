package com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentBMIBinding
import com.bayraktar.healthybackandneck.databinding.FragmentBodyFatRateBinding
import com.bayraktar.healthybackandneck.databinding.FragmentDailyCalorieBinding
import com.bayraktar.healthybackandneck.ui.Statistics.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import kotlin.math.abs

/** Common measurement/result interaction for the three existing XML calculator destinations. */
abstract class BodyCalculatorFragment(private val kind: Kind) : Fragment() {
    enum class Kind { BMI, CALORIES, FAT }
    private var binding: ViewBinding? = null
    private val root get() = binding!!.root
    private lateinit var store: DataStoreManage
    private var activeResult: Double? = null
    private var initializing = false
    private var busy = false
    private var selectedActivity = 0
    private var measurementsLoaded = false
    private var draftEdited = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        binding = when (kind) {
            Kind.BMI -> FragmentBMIBinding.inflate(inflater, container, false)
            Kind.CALORIES -> FragmentDailyCalorieBinding.inflate(inflater, container, false)
            Kind.FAT -> FragmentBodyFatRateBinding.inflate(inflater, container, false)
        }
        return root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        busy = false
        initializing = state != null
        measurementsLoaded = state?.getBoolean("measurementsLoaded") ?: false
        draftEdited = state?.getBoolean("draftEdited") ?: false
        if (kind == Kind.BMI && (resources.configuration.screenWidthDp < 360 || resources.configuration.fontScale >= 1.2f)) {
            val first = root.findViewById<View>(R.id.inputHeight).parent.parent as View
            val second = root.findViewById<View>(R.id.inputWeightDesc).parent.parent as View
            val row = first.parent as android.widget.LinearLayout
            row.orientation = android.widget.LinearLayout.VERTICAL
            listOf(first, second).forEach { field ->
                field.layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = (14 * resources.displayMetrics.density).toInt() }
            }
        }
        androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            root.findViewById(R.id.resultValue), 24, 52, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        if (resources.configuration.screenWidthDp < 360 || resources.configuration.fontScale >= 1.2f) {
            root.findViewById<android.widget.LinearLayout>(R.id.resultMetrics).orientation = android.widget.LinearLayout.VERTICAL
            root.findViewById<TextView>(R.id.resultValue).layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            root.findViewById<View>(R.id.resultIcon).visibility = View.GONE
        }
        store = DataStoreManage.getInstance(requireContext().applicationContext)
        root.findViewById<View>(R.id.goBack).setOnClickListener { findNavController().popBackStack() }
        root.findViewById<View>(R.id.returnHub).setOnClickListener {
            val nav = findNavController()
            if (!nav.popBackStack(R.id.bodyCalculatorsHubFragment, false)) {
                nav.navigate(R.id.bodyCalculatorsHubFragment, null, androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.id_statistics_fragment, false).build())
            }
        }
        root.findViewById<View>(R.id.btnSavee).setOnClickListener { calculate() }
        if (kind == Kind.CALORIES) {
            val spinner = root.findViewById<Spinner>(R.id.spnrActivity)
            spinner.adapter = ArrayAdapter(requireContext(), R.layout.pc_spinner_item,
                listOf(R.string.label_low, R.string.label_middle, R.string.label_high, R.string.label_toohigh).map(::getString)).apply {
                setDropDownViewResource(R.layout.pc_spinner_item)
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, selected: View?, position: Int, id: Long) {
                    if (position != selectedActivity) { selectedActivity = position; invalidateResult() }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) = invalidateResult()
            }
        }
        inputs().forEach { it.doAfterTextChanged { invalidateResult() } }
        root.findViewById<RadioGroup>(R.id.sexGroup)?.setOnCheckedChangeListener { _, _ ->
            updateHipVisibility()
            invalidateResult()
        }
        updateHipVisibility()
        if (state == null) loadMeasurements()
    }

    protected open suspend fun readMeasurements(): BodyCalculatorSnapshot = store.calculatorSnapshots().first()

    private fun loadMeasurements(preserveDraft: Boolean = false) {
        setBusy(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val saved = readMeasurements()
                initializing = true
                if (!preserveDraft) {
                setNumber(R.id.inputHeight, saved.height)
                setNumber(if (kind == Kind.BMI) R.id.inputWeightDesc else R.id.inputWeight, saved.weight)
                setNumber(R.id.inputAge, saved.age)
                setNumber(R.id.neckDesc, saved.neck)
                setNumber(R.id.bellyDesc, saved.waist)
                setNumber(R.id.buttDesc, saved.hip)
                root.findViewById<RadioButton>(if (saved.sex == BodySex.MALE) R.id.checkMale else R.id.checkFemale)
                    ?.isChecked = saved.sex != null
                selectedActivity = (saved.activity - 1).coerceIn(0, 3)
                root.findViewById<Spinner>(R.id.spnrActivity)?.setSelection(selectedActivity)
                updateHipVisibility()
                val previous = when (kind) { Kind.BMI -> saved.validSavedBmi; Kind.CALORIES -> saved.validSavedCalories; Kind.FAT -> saved.validSavedFat }
                if (previous != null) showResult(previous, true)
                }
                measurementsLoaded = true
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) {
                android.util.Log.e("BodyCalculator", "Could not load measurements", error)
                showError(R.string.pc_load_error)
            } finally {
                initializing = false
                if (binding != null) setBusy(false)
            }
        }
    }

    override fun onViewStateRestored(state: Bundle?) {
        super.onViewStateRestored(state)
        selectedActivity = root.findViewById<Spinner>(R.id.spnrActivity)?.selectedItemPosition ?: 0
        if (state != null) initializing = false
        if (state != null && !measurementsLoaded) {
            loadMeasurements(preserveDraft = draftEdited)
            return
        }
        if (state?.containsKey("calculationResult") == true) {
            val value = state.getDouble("calculationResult")
            val calculated = result(readDraft())
            if (calculated != null && abs(calculated - value) < .00001) showResult(value, false)
        }
    }

    private fun inputs(): List<EditText> = listOf(R.id.inputHeight, R.id.inputWeightDesc, R.id.inputWeight,
        R.id.inputAge, R.id.neckDesc, R.id.bellyDesc, R.id.buttDesc).mapNotNull { root.findViewById<EditText>(it) }
    private fun setNumber(id: Int, value: Int) { root.findViewById<EditText>(id)?.setText(if (value > 0) value.toString() else "") }
    private fun sex(): BodySex? = when {
        root.findViewById<RadioButton>(R.id.checkMale)?.isChecked == true -> BodySex.MALE
        root.findViewById<RadioButton>(R.id.checkFemale)?.isChecked == true -> BodySex.FEMALE
        else -> null
    }
    private fun updateHipVisibility() {
        root.findViewById<View>(R.id.hipLayout)?.visibility = if (sex() == BodySex.MALE) View.GONE else View.VISIBLE
    }
    private fun invalidateResult() {
        if (initializing || busy || binding == null) return
        draftEdited = true
        activeResult = null
        root.findViewById<View>(R.id.resultCard).visibility = View.GONE
        root.findViewById<View>(R.id.formError).visibility = View.GONE
    }
    private fun readDraft(): BodyCalculatorSnapshot {
        fun number(id: Int) = root.findViewById<EditText>(id)?.text?.toString()?.toIntOrNull() ?: 0
        return BodyCalculatorSnapshot(number(R.id.inputHeight), number(if (kind == Kind.BMI) R.id.inputWeightDesc else R.id.inputWeight),
            number(R.id.inputAge), (root.findViewById<Spinner>(R.id.spnrActivity)?.selectedItemPosition ?: -1) + 1,
            sex()?.name?.lowercase().orEmpty(), number(R.id.neckDesc), number(R.id.bellyDesc), number(R.id.buttDesc))
    }
    private fun result(draft: BodyCalculatorSnapshot): Double? = when (kind) {
        Kind.BMI -> BodyCalculations.bmi(draft.height, draft.weight)
        Kind.CALORIES -> BodyCalculations.dailyCalories(draft.sex, draft.height, draft.weight, draft.age, draft.activity)
        Kind.FAT -> BodyCalculations.bodyFat(draft.sex, draft.height, draft.neck, draft.waist, draft.hip)
    }

    private fun calculate() {
        if (busy) return
        invalidateResult()
        inputs().forEach { it.error = null }
        val required = when (kind) {
            Kind.BMI -> listOf(R.id.inputHeight, R.id.inputWeightDesc)
            Kind.CALORIES -> listOf(R.id.inputHeight, R.id.inputWeight, R.id.inputAge)
            Kind.FAT -> listOf(R.id.inputHeight, R.id.neckDesc, R.id.bellyDesc) + if (sex() == BodySex.FEMALE) listOf(R.id.buttDesc) else emptyList()
        }
        val invalid = required.map { root.findViewById<EditText>(it) }
            .firstOrNull { it.text.toString().toIntOrNull()?.let { value -> value > 0 } != true }
        if (invalid != null) {
            invalid.error = getString(R.string.pc_positive_error)
            invalid.requestFocus()
            return
        }
        val draft = readDraft()
        if (kind != Kind.BMI && draft.sex == null) { showError(R.string.pc_choose_sex); return }
        val value = result(draft) ?: run { showError(R.string.pc_measure_error); return }
        setBusy(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val gender = getString(if (draft.sex == BodySex.MALE) R.string.label_male else R.string.label_female)
                when (kind) {
                    Kind.BMI -> store.saveBmiCalculation(draft.height, draft.weight, value.toString(), getString(categoryResource(value)))
                    Kind.CALORIES -> store.saveCalorieCalculation(draft.height, draft.weight, draft.age, draft.activity, gender, value.toString())
                    Kind.FAT -> store.saveFatCalculation(draft.height, draft.neck, draft.waist, draft.hip.takeIf { draft.sex == BodySex.FEMALE }, gender, value.toString())
                }
                measurementsLoaded = true
                showResult(value, false)
                (requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager)
                    .hideSoftInputFromWindow(root.windowToken, 0)
                root.findViewById<View>(R.id.resultCard).post {
                    binding?.root?.findViewById<View>(R.id.resultCard)?.let { card ->
                        card.requestRectangleOnScreen(android.graphics.Rect(0, 0, card.width, card.height), false)
                    }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) {
                android.util.Log.e("BodyCalculator", "Could not save calculation", error)
                showError(R.string.pc_save_error)
            } finally { if (binding != null) setBusy(false) }
        }
    }

    private fun setBusy(value: Boolean) {
        busy = value
        root.findViewById<View>(R.id.btnSavee).isEnabled = !value
        inputs().forEach { it.isEnabled = !value }
        root.findViewById<View>(R.id.checkMale)?.isEnabled = !value
        root.findViewById<View>(R.id.checkFemale)?.isEnabled = !value
        root.findViewById<View>(R.id.spnrActivity)?.isEnabled = !value
    }
    private fun showError(message: Int) {
        root.findViewById<TextView>(R.id.formError).apply { setText(message); visibility = View.VISIBLE }
    }
    private fun showResult(value: Double, previous: Boolean) {
        activeResult = value
        root.findViewById<TextView>(R.id.btnSavee).setText(R.string.ac_recalculate)
        val digits = when (kind) { Kind.BMI -> 2; Kind.CALORIES -> 0; Kind.FAT -> 1 }
        root.findViewById<TextView>(R.id.resultValue).text = NumberFormat.getNumberInstance().apply { maximumFractionDigits = digits }.format(value)
        root.findViewById<TextView>(R.id.resultUnit).setText(when (kind) {
            Kind.BMI -> R.string.pc_bmi_unit; Kind.CALORIES -> R.string.pc_kcal_unit; Kind.FAT -> R.string.pc_percent_unit
        })
        root.findViewById<TextView>(R.id.resultDetail).apply {
            visibility = if (kind == Kind.BMI) View.VISIBLE else View.GONE
            if (kind == Kind.BMI) setText(categoryResource(value))
        }
        root.findViewById<TextView>(R.id.resultStatus).setText(if (previous) R.string.pc_result_previous else R.string.pc_result_saved)
        root.findViewById<View>(R.id.resultCard).visibility = View.VISIBLE
    }
    private fun categoryResource(value: Double) = when (BodyCalculations.bmiCategory(value)) {
        BmiCategory.UNDERWEIGHT -> R.string.pc_category_under
        BmiCategory.HEALTHY -> R.string.pc_category_healthy
        BmiCategory.OVERWEIGHT -> R.string.pc_category_over
        else -> R.string.pc_category_obesity
    }
    override fun onSaveInstanceState(state: Bundle) {
        state.putBoolean("measurementsLoaded", measurementsLoaded)
        state.putBoolean("draftEdited", draftEdited)
        activeResult?.let { state.putDouble("calculationResult", it) }
        super.onSaveInstanceState(state)
    }
    override fun onDestroyView() { binding = null; super.onDestroyView() }
}
