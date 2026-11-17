package com.bayraktar.healthybackandneck.ui.Statistics

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Models.completedProgramDays
import com.bayraktar.healthybackandneck.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProgressViewModel by viewModels()
    private var period = ProgressPeriod.LAST_SEVEN
    private var historyLimit = 5
    private var latestSessions = emptyList<WorkoutSession>()
    private var selectedBucket = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        period = ProgressPeriod.entries.firstOrNull { it.name == state?.getString("period") } ?: ProgressPeriod.LAST_SEVEN
        historyLimit = state?.getInt("historyLimit", 5) ?: 5
        selectedBucket = state?.getInt("selectedBucket", -1) ?: -1
        binding.periodGroup.check(if (period == ProgressPeriod.LAST_SEVEN) R.id.periodSeven else R.id.periodFourWeeks)
        binding.periodGroup.addOnButtonCheckedListener { _, id, checked ->
            if (checked) {
                period = if (id == R.id.periodSeven) ProgressPeriod.LAST_SEVEN else ProgressPeriod.LAST_FOUR_WEEKS
                selectedBucket = -1
                render(latestSessions)
            }
        }
        binding.editEndeks.setOnClickListener { findNavController().navigate(StatisticsFragmentDirections.actionIdStatisticsFragmentToBMIFragment()) }
        binding.editDayofCalorie.setOnClickListener { findNavController().navigate(StatisticsFragmentDirections.actionIdStatisticsFragmentToDailyCalorieFragment()) }
        binding.editfatrate.setOnClickListener { findNavController().navigate(StatisticsFragmentDirections.actionIdStatisticsFragmentToBodyFatRateFragment()) }
        binding.showMore.setOnClickListener { historyLimit += 5; renderHistory(latestSessions) }
        viewModel.sessions.observe(viewLifecycleOwner) { latestSessions = it; render(it) }
        viewModel.calculators.observe(viewLifecycleOwner) { snapshot ->
            renderSaved(binding.bmiSaved, snapshot.validSavedBmi, R.string.pc_bmi_unit, 2)
            renderSaved(binding.calorieSaved, snapshot.validSavedCalories, R.string.pc_kcal_unit, 0)
            renderSaved(binding.fatSaved, snapshot.validSavedFat, R.string.pc_percent_unit, 1)
        }
    }

    private fun renderSaved(label: TextView, value: Double?, unit: Int, digits: Int) {
        label.text = if (value == null) getString(R.string.pc_open_calculator) else
            getString(R.string.pc_saved_estimate, NumberFormat.getNumberInstance().apply { maximumFractionDigits = digits }.format(value) + " " + getString(unit))
    }

    private fun render(sessions: List<WorkoutSession>) {
        val now = System.currentTimeMillis()
        val trustworthy = ProgressSummary.trustworthy(sessions, now)
        val summary = ProgressSummary.from(trustworthy, now, period)
        binding.weekTitle.setText(if (period == ProgressPeriod.LAST_SEVEN) R.string.pc_period_seven else R.string.pc_period_four)
        binding.periodDate.text = dateRange(summary.start, summary.end)
        binding.weekWorkouts.text = NumberFormat.getIntegerInstance().format(summary.completedWorkouts)
        binding.weekMinutes.text = minuteLabel(summary.activeMillis)
        binding.weekMoves.text = NumberFormat.getIntegerInstance().format(summary.completedMoves)
        binding.chartTitle.setText(if (period == ProgressPeriod.LAST_SEVEN) R.string.pc_daily_activity else R.string.pc_weekly_activity)
        binding.chartCaption.setText(if (period == ProgressPeriod.LAST_SEVEN) R.string.pc_daily_unit else R.string.pc_weekly_unit)
        binding.chartEmpty.visibility = if (summary.activeMillis == 0L) View.VISIBLE else View.GONE
        binding.chartEmpty.setText(if (summary.recordedSessions == 0) R.string.pc_no_activity else R.string.pc_zero_activity)
        renderChart(summary.buckets)
        binding.programProgress.removeAllViews()
        for (level in 1..3) {
            val count = completedProgramDays(trustworthy, level)
            val label = TextView(requireContext()).apply {
                text = getString(R.string.program_progress_format, level, count)
                textSize = 15f
                setTextColor(ContextCompat.getColor(context, R.color.pc_forest))
                setPadding(0, if (level == 1) 0 else dp(16), 0, dp(6))
            }
            val progress = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 21; this.progress = count
                progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.fit_primary))
                progressBackgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.fit_soft))
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(14))
                contentDescription = label.text
            }
            binding.programProgress.addView(label)
            binding.programProgress.addView(progress)
        }
        renderHistory(sessions)
    }

    private fun renderHistory(records: List<WorkoutSession>) {
        val sessions = ProgressSummary.trustworthy(records, System.currentTimeMillis())
        binding.emptyHistory.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
        binding.showMore.visibility = if (sessions.size > historyLimit) View.VISIBLE else View.GONE
        binding.history.removeAllViews()
        sessions.take(historyLimit).forEach { session ->
            val row = layoutInflater.inflate(R.layout.item_workout_history, binding.history, false)
            row.findViewById<TextView>(R.id.historyTitle).text = if (session.level in 1..3 && session.day in 1..21)
                getString(R.string.workout_day_title, session.level, session.day) else session.title
            row.findViewById<TextView>(R.id.historyDate).text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(session.endedAt))
            row.findViewById<TextView>(R.id.historyDetails).text = getString(R.string.pc_history_summary,
                getString(R.string.pc_time_short, session.activeMillis.coerceAtLeast(0) / 60000, session.activeMillis.coerceAtLeast(0) / 1000 % 60),
                session.completedMoves, session.plannedMoves)
            row.findViewById<TextView>(R.id.historyStatus).apply {
                setText(if (session.isComplete) R.string.session_complete else R.string.session_partial)
                setTextColor(ContextCompat.getColor(context, if (session.isComplete) R.color.fit_primary else R.color.fit_muted))
            }
            binding.history.addView(row)
        }
    }

    private fun renderChart(buckets: List<ActivityBucket>) {
        binding.weekBars.removeAllViews()
        val max = buckets.maxOfOrNull { it.activeMillis }?.coerceAtLeast(60000L) ?: 60000L
        if (selectedBucket !in buckets.indices) selectedBucket = buckets.lastIndex
        val columns = mutableListOf<LinearLayout>()
        fun select(index: Int) {
            selectedBucket = index
            columns.forEachIndexed { i, column ->
                column.setBackgroundResource(if (i == index) R.drawable.pc_soft else android.R.color.transparent)
                column.isSelected = i == index
            }
            val bucket = buckets[index]
            binding.chartSelection.text = getString(R.string.pc_chart_detail, bucketDate(bucket), bucket.activeMillis / 60000, bucket.activeMillis / 1000 % 60)
        }
        buckets.forEachIndexed { index, bucket ->
            val column = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(dp(48), ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                isClickable = true; isFocusable = true
                contentDescription = getString(R.string.pc_chart_access, bucketDate(bucket), bucket.activeMillis / 60000, bucket.activeMillis / 1000 % 60)
                setPadding(dp(2), dp(8), dp(2), dp(8))
                setOnClickListener { select(index) }
            }
            val value = TextView(requireContext()).apply {
                text = minuteLabel(bucket.activeMillis); textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.pc_forest))
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            val bar = View(requireContext()).apply {
                val height = if (bucket.activeMillis == 0L) 2 else (104.0 * bucket.activeMillis / max).toInt().coerceAtLeast(4)
                layoutParams = LinearLayout.LayoutParams(dp(16), dp(height)).apply { topMargin = dp(8); bottomMargin = dp(8) }
                setBackgroundResource(if (bucket.activeMillis == 0L) R.drawable.fit_soft_card else R.drawable.pc_bar)
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            val label = TextView(requireContext()).apply {
                text = if (period == ProgressPeriod.LAST_SEVEN) SimpleDateFormat("EE\nd", Locale.getDefault()).format(Date(bucket.start))
                    else SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(bucket.start))
                textSize = 11f; gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.fit_muted))
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            column.addView(value); column.addView(bar); column.addView(label)
            columns += column
            binding.weekBars.addView(column)
        }
        select(selectedBucket)
    }

    private fun bucketDate(bucket: ActivityBucket): String = if (period == ProgressPeriod.LAST_SEVEN)
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(bucket.start)) else dateRange(bucket.start, bucket.endExclusive - 1)
    private fun dateRange(start: Long, end: Long) = DateFormat.getDateInstance(DateFormat.MEDIUM).let { it.format(Date(start)) + " – " + it.format(Date(end)) }
    private fun minuteLabel(millis: Long) = if (millis in 1..59999) "<1" else NumberFormat.getIntegerInstance().format(millis / 60000)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    override fun onSaveInstanceState(outState: Bundle) { outState.putString("period", period.name); outState.putInt("historyLimit", historyLimit); outState.putInt("selectedBucket", selectedBucket); super.onSaveInstanceState(outState) }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
