package com.bayraktar.healthybackandneck.ui.Statistics

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.widget.ArrayAdapter
import android.widget.AdapterView
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
    private var programLevel = 1

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
        binding.openCalculators.setOnClickListener { findNavController().navigate(R.id.bodyCalculatorsHubFragment) }
        programLevel = state?.getInt("programLevel", 1)?.coerceIn(1, 3) ?: 1
        binding.programLevel.adapter = ArrayAdapter(requireContext(), R.layout.pc_spinner_item,
            listOf(R.string.easy_level, R.string.middle_level, R.string.hard_level).map { getString(it).replace("\n", " ") }).apply {
            setDropDownViewResource(R.layout.pc_spinner_item)
        }
        binding.programLevel.setSelection(programLevel - 1)
        binding.programLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, selected: View?, position: Int, id: Long) {
                programLevel = position + 1
                renderProgram(ProgressSummary.trustworthy(latestSessions, System.currentTimeMillis()))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.showMore.setOnClickListener { historyLimit += 5; renderHistory(latestSessions) }
        viewModel.sessions.observe(viewLifecycleOwner) { latestSessions = it; render(it) }
    }

    private fun render(sessions: List<WorkoutSession>) {
        val now = System.currentTimeMillis()
        val trustworthy = ProgressSummary.trustworthy(sessions, now)
        val summary = ProgressSummary.from(trustworthy, now, period)
        binding.weekTitle.setText(R.string.ac_effort)
        binding.periodDate.text = dateRange(summary.start, summary.end)
        binding.weekWorkouts.text = NumberFormat.getIntegerInstance().format(summary.completedWorkouts)
        binding.weekMinutes.text = minuteLabel(summary.activeMillis)
        binding.weekMoves.text = NumberFormat.getIntegerInstance().format(summary.completedMoves)
        binding.chartTitle.setText(if (period == ProgressPeriod.LAST_SEVEN) R.string.pc_daily_activity else R.string.pc_weekly_activity)
        binding.chartCaption.setText(if (period == ProgressPeriod.LAST_SEVEN) R.string.pc_daily_unit else R.string.pc_weekly_unit)
        binding.chartEmpty.visibility = if (summary.activeMillis == 0L) View.VISIBLE else View.GONE
        binding.chartEmpty.setText(if (summary.recordedSessions == 0) R.string.pc_no_activity else R.string.pc_zero_activity)
        renderChart(summary.buckets)
        renderProgram(trustworthy)
        renderHistory(sessions)
    }

    private fun renderProgram(records: List<WorkoutSession>) {
        val count = completedProgramDays(records, programLevel)
        binding.programRing.progress = count
        binding.programRing.contentDescription = getString(R.string.program_progress_format, programLevel, count)
        binding.programCount.text = getString(R.string.ac_ring_count, count)
        binding.programDescription.text = getString(R.string.ac_program_days, programLevel, count)
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
        val max = (kotlin.math.ceil((buckets.maxOfOrNull { it.activeMillis } ?: 0L) / 300000.0).toLong().coerceAtLeast(1) * 300000L)
        binding.chartGrid.maximumMinutes = max / 60000.0
        if (selectedBucket !in buckets.indices) selectedBucket = buckets.lastIndex
        val columns = mutableListOf<LinearLayout>()
        fun select(index: Int) {
            selectedBucket = index
            columns.forEachIndexed { i, column ->
                (column.getChildAt(1) as FrameLayout).getChildAt(0).backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), if (i == index && buckets[i].activeMillis > 0) R.color.ac_lime else if (buckets[i].activeMillis == 0L) R.color.pc_border else R.color.pc_forest))
                (column.getChildAt(0) as TextView).setBackgroundResource(if (i == index) R.drawable.ac_pill else android.R.color.transparent)
                column.isSelected = i == index
            }
            val bucket = buckets[index]
            binding.chartSelection.text = getString(R.string.pc_chart_detail, bucketDate(bucket), bucket.activeMillis / 60000, bucket.activeMillis / 1000 % 60)
        }
        buckets.forEachIndexed { index, bucket ->
            val column = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                isClickable = true; isFocusable = true
                contentDescription = getString(R.string.pc_chart_access, bucketDate(bucket), bucket.activeMillis / 60000, bucket.activeMillis / 1000 % 60)
                setPadding(dp(2), dp(8), dp(2), dp(8))
                setOnClickListener { select(index) }
            }
            val value = TextView(requireContext()).apply {
                text = minuteLabel(bucket.activeMillis); textSize = 11f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(24))
                setTextColor(ContextCompat.getColor(context, R.color.pc_forest))
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            val bar = View(requireContext()).apply {
                val height = if (bucket.activeMillis == 0L) 2 else (128.0 * bucket.activeMillis / max).toInt().coerceAtLeast(4)
                layoutParams = FrameLayout.LayoutParams(dp(28), dp(height), Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
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
            val plot = FrameLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(128))
                addView(bar)
            }
            label.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40))
            column.addView(value); column.addView(plot); column.addView(label)
            columns += column
            binding.weekBars.addView(column)
        }
        select(selectedBucket)
    }

    private fun bucketDate(bucket: ActivityBucket): String = if (period == ProgressPeriod.LAST_SEVEN)
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(bucket.start)) else dateRange(bucket.start, bucket.endExclusive - 1)
    private fun dateRange(start: Long, end: Long) = DateFormat.getDateInstance(DateFormat.MEDIUM).let { it.format(Date(start)) + " \u2013 " + it.format(Date(end)) }
    private fun minuteLabel(millis: Long) = if (millis in 1..59999) "<1" else NumberFormat.getIntegerInstance().format(millis / 60000)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    override fun onSaveInstanceState(outState: Bundle) { outState.putInt("programLevel", programLevel); outState.putString("period", period.name); outState.putInt("historyLimit", historyLimit); outState.putInt("selectedBucket", selectedBucket); super.onSaveInstanceState(outState) }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
