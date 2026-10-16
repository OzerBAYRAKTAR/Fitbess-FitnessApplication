package com.bayraktar.healthybackandneck.ui.Statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var historyLimit = 50
    private var latestSessions = emptyList<com.bayraktar.healthybackandneck.data.Models.WorkoutSession>()
    private val viewModel: ProgressViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, state: Bundle?) {
        binding.editEndeks.setOnClickListener { findNavController().navigate(StatisticsFragmentDirections.actionIdStatisticsFragmentToBMIFragment()) }
        binding.editDayofCalorie.setOnClickListener { findNavController().navigate(StatisticsFragmentDirections.actionIdStatisticsFragmentToDailyCalorieFragment()) }
        binding.editfatrate.setOnClickListener { findNavController().navigate(StatisticsFragmentDirections.actionIdStatisticsFragmentToBodyFatRateFragment()) }
        binding.showMore.setOnClickListener { historyLimit += 50; render(latestSessions) }
        viewModel.sessions.observe(viewLifecycleOwner) { sessions -> latestSessions = sessions; render(sessions) }
    }
    private fun render(sessions: List<com.bayraktar.healthybackandneck.data.Models.WorkoutSession>) {
            val weekStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val week = sessions.filter { it.endedAt >= weekStart }
            binding.weekWorkouts.text = week.count { it.isComplete }.toString()
            binding.weekMinutes.text = (week.sumOf { it.activeMillis } / 60000).toString()
            binding.weekMoves.text = week.sumOf { it.completedMoves }.toString()
            binding.emptyHistory.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
            binding.achievement.setText(if (sessions.any { it.isComplete }) R.string.first_workout_done else R.string.first_workout_goal)
            binding.programProgress.removeAllViews()
            (1..3).forEach { level ->
                val days = sessions.filter { it.isComplete && it.level == level && it.day in 1..21 }.map { it.day }.distinct().size
                val label = TextView(requireContext()).apply {
                    text = getString(R.string.program_progress_format, level, days)
                    textSize = 15f
                    setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.fit_text))
                }
                val progress = android.widget.ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
                    max = 21; this.progress = days
                    progressTintList = android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(context, R.color.fit_primary))
                    layoutParams = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, (24 * resources.displayMetrics.density).toInt())
                    contentDescription = label.text
                }
                binding.programProgress.addView(label)
                binding.programProgress.addView(progress)
            }
            binding.showMore.visibility = if (sessions.size > historyLimit) View.VISIBLE else View.GONE
            binding.weekDays.removeAllViews()
            repeat(7) { offset ->
                val date = Calendar.getInstance().apply { timeInMillis = weekStart; add(Calendar.DAY_OF_YEAR, offset) }
                val next = (date.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
                val active = sessions.any { it.endedAt in date.timeInMillis until next && it.activeMillis > 0 }
                val day = TextView(requireContext()).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    gravity = android.view.Gravity.CENTER
                    text = java.text.SimpleDateFormat("EE", java.util.Locale.getDefault()).format(date.time) + "\n" + if (active) "●" else "○"
                    textSize = 14f
                    setTextColor(androidx.core.content.ContextCompat.getColor(context, if (active) R.color.fit_primary else R.color.fit_muted))
                    contentDescription = DateFormat.getDateInstance().format(date.time) + ": " + getString(if (active) R.string.active_day else R.string.no_workout_day)
                }
                binding.weekDays.addView(day)
            }
            binding.history.removeAllViews()
            sessions.take(historyLimit).forEach { session ->
                val row = layoutInflater.inflate(R.layout.item_workout_history, binding.history, false)
                row.findViewById<TextView>(R.id.historyTitle).text = if (session.level in 1..3 && session.day in 1..21) getString(R.string.workout_day_title, session.level, session.day) else session.title
                row.findViewById<TextView>(R.id.historyDate).text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(session.endedAt))
                row.findViewById<TextView>(R.id.historyDetails).text = getString(R.string.session_details, session.activeMillis / 60000, (session.activeMillis / 1000) % 60, session.completedMoves, session.plannedMoves, session.skippedMoves)
                row.findViewById<TextView>(R.id.historyStatus).setText(if (session.isComplete) R.string.session_complete else R.string.session_partial)
                binding.history.addView(row)
            }
    }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
