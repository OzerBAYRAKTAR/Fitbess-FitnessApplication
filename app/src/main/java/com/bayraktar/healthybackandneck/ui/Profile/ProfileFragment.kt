package com.bayraktar.healthybackandneck.ui.Profile

import android.Manifest
import android.app.AlertDialog
import android.content.Context
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
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentProfileBinding
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.bayraktar.healthybackandneck.utils.ProfileIntents
import com.bayraktar.healthybackandneck.utils.ReminderKind
import com.bayraktar.healthybackandneck.utils.ReminderInterval
import com.bayraktar.healthybackandneck.utils.RevenueCatManager
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
    private val permissionPreferences by lazy {
        requireContext().getSharedPreferences("profile_notification_permissions", Context.MODE_PRIVATE)
    }
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val kind = pendingPermission
        pendingPermission = null
        if (granted && kind != null && viewModel.allowed(kind)) {
            viewModel.setEnabled(kind, true)
        } else {
            render(viewModel.state.value)
            if (!granted && !shouldShowNotificationPermissionRationale()) openSettings(null)
            else Toast.makeText(context, R.string.notification_blocked, Toast.LENGTH_LONG).show()
        }
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
        binding.notificationSettings.setOnClickListener { openNotificationSettings() }
        binding.buyAdFree.setOnClickListener {
            findNavController().navigate(R.id.action_global_adFreeOfferFragment)
        }
        binding.waterStatus.setOnClickListener { selectInterval(ReminderKind.WATER) }
        binding.motivationStatus.setOnClickListener { selectInterval(ReminderKind.MOTIVATION) }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.state.collect(::render) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                RevenueCatManager.state.collect(::renderPurchases)
            }
        }
        renderPurchases(RevenueCatManager.state.value)
    }
    private fun renderPurchases(state: RevenueCatManager.State) {
        if (_binding == null) return
        binding.adFreeStatus.text = when {
            state.busy -> getString(R.string.purchase_processing)
            state.result == RevenueCatManager.Result.PURCHASED -> getString(R.string.purchase_success)
            state.result == RevenueCatManager.Result.RESTORED -> getString(R.string.restore_success)
            state.adFree -> getString(R.string.remove_ads_active)
            state.result == RevenueCatManager.Result.NOTHING_TO_RESTORE -> getString(R.string.restore_empty)
            state.result == RevenueCatManager.Result.RESTORE_ERROR -> getString(R.string.restore_error)
            state.result == RevenueCatManager.Result.CANCELLED -> getString(R.string.purchase_cancelled)
            state.result == RevenueCatManager.Result.ERROR -> getString(R.string.purchase_error)
            !state.configured -> getString(R.string.remove_ads_unavailable)
            !state.offeringResolved -> getString(R.string.remove_ads_loading)
            state.price == null -> getString(R.string.remove_ads_not_found)
            else -> getString(R.string.remove_ads_price, state.price)
        }
        binding.buyAdFree.text = getString(R.string.view_ad_free_offer)
    }
    private fun render(state: ProfileViewModel.State) {
        if (_binding == null) return
        listOf(ReminderKind.WATER to binding.waterReminderSwitch, ReminderKind.MOTIVATION to binding.notificationSwitch).forEach { (kind, toggle) ->
            toggle.setOnCheckedChangeListener(null)
            toggle.isChecked = state.enabled[kind] == true && viewModel.allowed(kind)
            toggle.isEnabled = !state.busy && pendingPermission == null
            toggle.setOnCheckedChangeListener { _, enabled -> changeReminder(kind, enabled) }
            val status = if (kind == ReminderKind.WATER) binding.waterStatus else binding.motivationStatus
            val intervalRes = when (state.intervals[kind] ?: kind.defaultInterval) {
                ReminderInterval.DAILY -> R.string.reminder_daily
                ReminderInterval.EVERY_12_HOURS -> R.string.reminder_12_hours
                ReminderInterval.EVERY_6_HOURS -> R.string.reminder_6_hours
            }
            status.text = getString(intervalRes)
            status.isEnabled = !state.busy && pendingPermission == null
        }
        val notificationsUnavailable = ReminderKind.entries.any { !viewModel.allowed(it) }
        binding.notificationGuidance.visibility = if (notificationsUnavailable) View.VISIBLE else View.GONE
        binding.notificationSettings.visibility = if (notificationsUnavailable) View.VISIBLE else View.GONE
        binding.profileError.visibility = if (state.failed) View.VISIBLE else View.GONE
    }
    private fun changeReminder(kind: ReminderKind, enabled: Boolean) {
        if (!enabled) { viewModel.setEnabled(kind, false); return }
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            val wasRequested = permissionPreferences.getBoolean(POST_NOTIFICATIONS_REQUESTED, false)
            if (wasRequested && !shouldShowNotificationPermissionRationale()) {
                render(viewModel.state.value)
                openSettings(null)
                return
            }
            pendingPermission = kind
            permissionPreferences.edit().putBoolean(POST_NOTIFICATIONS_REQUESTED, true).apply()
            render(viewModel.state.value)
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (!viewModel.allowed(kind)) {
            render(viewModel.state.value)
            AlertDialog.Builder(requireContext()).setMessage(R.string.notification_blocked)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.notification_settings) { _, _ -> openNotificationSettings() }.show()
        } else viewModel.setEnabled(kind, true)
    }
    private fun selectInterval(kind: ReminderKind) {
        if (!viewModel.allowed(kind)) {
            openNotificationSettings()
            return
        }
        val intervals = ReminderInterval.entries.toTypedArray()
        val selected = intervals.indexOf(viewModel.state.value.intervals[kind] ?: kind.defaultInterval)
        val labels = intervals.map { interval ->
            getString(when (interval) {
                ReminderInterval.DAILY -> R.string.reminder_daily
                ReminderInterval.EVERY_12_HOURS -> R.string.reminder_12_hours
                ReminderInterval.EVERY_6_HOURS -> R.string.reminder_6_hours
            })
        }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.reminder_interval_title)
            .setSingleChoiceItems(labels, selected) { dialog, index ->
                viewModel.setInterval(kind, intervals[index])
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    private fun openSettings(kind: ReminderKind?) {
        val intent = if (Build.VERSION.SDK_INT >= 26) Intent(if (kind == null) Settings.ACTION_APP_NOTIFICATION_SETTINGS else Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            kind?.let { putExtra(Settings.EXTRA_CHANNEL_ID, it.channel) }
        } else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireContext().packageName}"))
        open(intent)
    }
    private fun openNotificationSettings() {
        val runtimePermissionMissing = Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        val appNotificationsDisabled = !NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        val blockedChannel = ReminderKind.entries.firstOrNull { !viewModel.allowed(it) }
        openSettings(if (runtimePermissionMissing || appNotificationsDisabled) null else blockedChannel)
    }
    private fun shouldShowNotificationPermissionRationale() =
        Build.VERSION.SDK_INT >= 33 && requireActivity().shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
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

    private companion object {
        const val POST_NOTIFICATIONS_REQUESTED = "post_notifications_requested"
    }
}
