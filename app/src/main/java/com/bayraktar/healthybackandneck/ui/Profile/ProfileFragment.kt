package com.bayraktar.healthybackandneck.ui.Profile

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentProfileBinding
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.bayraktar.healthybackandneck.utils.ProfileIntents
import com.bayraktar.healthybackandneck.utils.ReminderKind
import com.bayraktar.healthybackandneck.utils.loadForLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var pendingPermission: ReminderKind? = null
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val kind = pendingPermission
        pendingPermission = null
        if (granted && kind != null && viewModel.allowed(kind)) viewModel.setEnabled(kind, true)
        else { render(viewModel.state.value); Toast.makeText(context, R.string.notification_blocked, Toast.LENGTH_LONG).show() }
    }
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        pendingPermission = state?.getString("pendingReminder")?.let { ReminderKind.valueOf(it) }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        pendingPermission?.let { outState.putString("pendingReminder", it.name) }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, state: Bundle?) {
        binding.adView.loadForLifecycle(viewLifecycleOwner)
        binding.consLanguage.setOnClickListener { chooseLanguage() }
        binding.consRateus.setOnClickListener {
            try { startActivity(ProfileIntents.store(requireContext())) }
            catch (_: ActivityNotFoundException) { open(ProfileIntents.store(requireContext(), web = true)) }
        }
        binding.consShare.setOnClickListener { open(Intent.createChooser(ProfileIntents.share(requireContext()), getString(R.string.label_sharevia))) }
        binding.consPrivacy.setOnClickListener { open(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Glovani/Glovani.github.io/blob/main/privacy-policy.txt"))) }
        binding.notificationSettings.setOnClickListener { openSettings(null) }
        binding.waterStatus.setOnClickListener { if (!viewModel.allowed(ReminderKind.WATER)) openSettings(ReminderKind.WATER) }
        binding.motivationStatus.setOnClickListener { if (!viewModel.allowed(ReminderKind.MOTIVATION)) openSettings(ReminderKind.MOTIVATION) }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.state.collect(::render) }
        }
    }
    private fun render(state: ProfileViewModel.State) {
        if (_binding == null) return
        listOf(ReminderKind.WATER to binding.waterReminderSwitch, ReminderKind.MOTIVATION to binding.notificationSwitch).forEach { (kind, toggle) ->
            toggle.setOnCheckedChangeListener(null)
            toggle.isChecked = state.enabled[kind] == true
            toggle.isEnabled = !state.busy && pendingPermission == null
            toggle.setOnCheckedChangeListener { _, enabled -> changeReminder(kind, enabled) }
            val status = if (kind == ReminderKind.WATER) binding.waterStatus else binding.motivationStatus
            status.setText(when {
                !viewModel.allowed(kind) -> R.string.notification_blocked
                state.enabled[kind] != true -> R.string.reminder_off
                kind == ReminderKind.WATER -> R.string.water_cadence
                else -> R.string.motivation_cadence
            })
        }
        binding.profileError.visibility = if (state.failed) View.VISIBLE else View.GONE
    }
    private fun changeReminder(kind: ReminderKind, enabled: Boolean) {
        if (!enabled) { viewModel.setEnabled(kind, false); return }
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            pendingPermission = kind
            render(viewModel.state.value)
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (!viewModel.allowed(kind)) {
            render(viewModel.state.value)
            AlertDialog.Builder(requireContext()).setMessage(R.string.notification_blocked)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.notification_settings) { _, _ -> openSettings(kind) }.show()
        } else viewModel.setEnabled(kind, true)
    }
    private fun openSettings(kind: ReminderKind?) {
        val intent = if (Build.VERSION.SDK_INT >= 26) Intent(if (kind == null) Settings.ACTION_APP_NOTIFICATION_SETTINGS else Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            kind?.let { putExtra(Settings.EXTRA_CHANNEL_ID, it.channel) }
        } else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireContext().packageName}"))
        open(intent)
    }
    private fun open(intent: Intent) {
        try { startActivity(intent) }
        catch (_: ActivityNotFoundException) { Toast.makeText(context, R.string.label_error, Toast.LENGTH_LONG).show() }
    }
    private fun chooseLanguage() {
        val codes = arrayOf("tr", "en", "de", "ru")
        AlertDialog.Builder(requireContext()).setTitle(R.string.label_selectlanguage)
            .setSingleChoiceItems(arrayOf("Türkçe", "English", "Deutsch", "Русский"), codes.indexOf(Locale.getDefault().language)) { dialog, index ->
                LanguagePreference.setLanguageCode(requireContext(), codes[index])
                LocaleHelper.setLocale(requireContext(), codes[index])
                dialog.dismiss()
                requireActivity().recreate()
            }.setNegativeButton(android.R.string.cancel, null).show()
    }
    override fun onResume() { super.onResume(); if (_binding != null) render(viewModel.state.value) }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
