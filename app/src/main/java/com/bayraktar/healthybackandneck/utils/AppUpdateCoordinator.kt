package com.bayraktar.healthybackandneck.utils

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.bayraktar.healthybackandneck.R
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.gms.tasks.Tasks
import timber.log.Timber

/** Coordinates Play's flexible and immediate update UX without making app access depend on network/config. */
internal class AppUpdateCoordinator(private val activity: AppCompatActivity) {
    private val manager: AppUpdateManager? = try {
        AppUpdateManagerFactory.create(activity)
    } catch (error: Exception) {
        Timber.i(error, "Play updates are unavailable; continuing without blocking")
        null
    }
    private val preferences = activity.getSharedPreferences(PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    private var flowInProgress = false
    private var immediateWasRequired = false
    private var checkingForUpdate = false
    private var downloadedDialog: AlertDialog? = null
    private var softUpdateDialog: AlertDialog? = null

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            flowInProgress = false
            if (result.resultCode == Activity.RESULT_CANCELED && immediateWasRequired) {
                showRequiredUpdateDialog()
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                pendingSoftVersionCode?.let { preferences.edit { putLong(KEY_DISMISSED_VERSION, it) } }
            }
            immediateWasRequired = false
        }

    private var pendingSoftVersionCode: Long? = null

    private val installListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) showDownloadedUpdateDialog()
    }

    private val remoteConfig: FirebaseRemoteConfig? = try {
        FirebaseRemoteConfig.getInstance()
    } catch (error: Exception) {
        Timber.w(error, "Remote update policy is unavailable; using flexible updates")
        null
    }

    private val remoteConfigSetup = remoteConfig?.let { config ->
        try {
            Tasks.whenAll(
                config.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(REMOTE_CONFIG_FETCH_INTERVAL_SECONDS)
                    .build()
                ),
                config.setDefaultsAsync(mapOf(MINIMUM_SUPPORTED_VERSION_CODE to 0L))
            )
        } catch (error: Exception) {
            Timber.w(error, "Remote update policy setup failed; using flexible updates")
            null
        }
    }

    init {
        manager?.registerListener(installListener)
    }

    fun onResume() {
        if (flowInProgress || checkingForUpdate || softUpdateDialog?.isShowing == true) return
        val updateManager = manager ?: return
        checkingForUpdate = true
        try {
            updateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                when (info.installStatus()) {
                    InstallStatus.DOWNLOADED -> {
                        checkingForUpdate = false
                        showDownloadedUpdateDialog()
                    }
                    InstallStatus.PENDING, InstallStatus.DOWNLOADING, InstallStatus.INSTALLING -> {
                        // A flexible flow can outlive this Activity; don't show the app prompt over Play's flow.
                        checkingForUpdate = false
                    }
                    else -> when (info.updateAvailability()) {
                        com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                            checkingForUpdate = false
                            // Play requires an immediate update already in progress to resume after returning to the app.
                            startFlow(info, AppUpdatePolicy.Mode.IMMEDIATE)
                        }
                        com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE -> evaluateAvailableUpdate(info)
                        else -> checkingForUpdate = false
                    }
                }
            }
            .addOnFailureListener { error ->
                checkingForUpdate = false
                Timber.i(error, "Play update check unavailable; continuing without blocking")
            }
        } catch (error: Exception) {
            checkingForUpdate = false
            Timber.i(error, "Play update check failed; continuing without blocking")
        }
    }

    fun close() {
        manager?.unregisterListener(installListener)
        downloadedDialog?.dismiss()
        softUpdateDialog?.dismiss()
    }

    private fun evaluateAvailableUpdate(info: AppUpdateInfo) {
        val currentVersionCode = try {
            @Suppress("DEPRECATION")
            activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode.toLong()
        } catch (error: Exception) {
            Timber.w(error, "Cannot read installed version; using flexible update")
            0L
        }
        val offeredVersionCode = info.availableVersionCode().toLong()
        val config = remoteConfig
        if (config == null) {
            checkingForUpdate = false
            maybeStartFlexible(info, currentVersionCode, offeredVersionCode, 0L)
            return
        }

        // Remote Config retains its last activated values and uses the local default (0) when no value exists.
        val setup = remoteConfigSetup
        if (setup == null) {
            checkingForUpdate = false
            maybeStartFlexible(info, currentVersionCode, offeredVersionCode, 0L)
            return
        }
        setup.addOnCompleteListener(activity) { setupResult ->
            if (!setupResult.isSuccessful) {
                checkingForUpdate = false
                Timber.w(setupResult.exception, "Remote update policy setup failed; using flexible update")
                maybeStartFlexible(info, currentVersionCode, offeredVersionCode, 0L)
                return@addOnCompleteListener
            }
            try {
                config.fetchAndActivate()
                    .addOnCompleteListener(activity) {
                        checkingForUpdate = false
                        val minimumVersionCode = try {
                            config.getLong(MINIMUM_SUPPORTED_VERSION_CODE)
                        } catch (error: Exception) {
                            Timber.w(error, "Cannot read remote update policy; using flexible update")
                            0L
                        }
                        maybeStartFlexible(info, currentVersionCode, offeredVersionCode, minimumVersionCode)
                    }
            } catch (error: Exception) {
                checkingForUpdate = false
                Timber.w(error, "Remote update policy fetch failed; using flexible update")
                maybeStartFlexible(info, currentVersionCode, offeredVersionCode, 0L)
            }
        }
    }

    private fun maybeStartFlexible(
        info: AppUpdateInfo,
        installedVersionCode: Long,
        offeredVersionCode: Long,
        minimumVersionCode: Long
    ) {
        if (activity.isFinishing || activity.isDestroyed) return
        val immediateAllowed = info.isUpdateTypeAllowed(AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
        val flexibleAllowed = info.isUpdateTypeAllowed(AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
        when (AppUpdatePolicy.choose(
            installedVersionCode,
            minimumVersionCode,
            offeredVersionCode,
            updateAvailable = true,
            immediateAllowed = immediateAllowed,
            flexibleAllowed = flexibleAllowed
        )) {
            AppUpdatePolicy.Mode.IMMEDIATE -> startFlow(info, AppUpdatePolicy.Mode.IMMEDIATE)
            AppUpdatePolicy.Mode.FLEXIBLE -> {
                val dismissedVersionCode = preferences.getLong(KEY_DISMISSED_VERSION, -1L)
                if (AppUpdatePolicy.shouldPromptForFlexibleUpdate(offeredVersionCode, dismissedVersionCode)) {
                    showSoftUpdateDialog(info, offeredVersionCode)
                }
            }
            AppUpdatePolicy.Mode.NONE -> Unit
        }
    }

    private fun showSoftUpdateDialog(info: AppUpdateInfo, offeredVersionCode: Long) {
        if (activity.isFinishing || activity.isDestroyed || softUpdateDialog?.isShowing == true) return
        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.update_available_title)
            .setMessage(R.string.update_available_message)
            .setPositiveButton(R.string.update_now, null)
            .setNegativeButton(R.string.update_later, null)
            .create()
        dialog.setCancelable(false)
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialog.dismiss()
                softUpdateDialog = null
                pendingSoftVersionCode = offeredVersionCode
                startFlow(info, AppUpdatePolicy.Mode.FLEXIBLE)
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                preferences.edit { putLong(KEY_DISMISSED_VERSION, offeredVersionCode) }
                dialog.dismiss()
                softUpdateDialog = null
            }
        }
        dialog.setOnDismissListener {
            if (softUpdateDialog === dialog) softUpdateDialog = null
        }
        softUpdateDialog = dialog
        dialog.show()
    }

    private fun startFlow(info: AppUpdateInfo, mode: AppUpdatePolicy.Mode) {
        if (activity.isFinishing || activity.isDestroyed || flowInProgress) return
        val updateType = if (mode == AppUpdatePolicy.Mode.IMMEDIATE) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
        val options = AppUpdateOptions.newBuilder(updateType).build()
        if (!info.isUpdateTypeAllowed(options)) return
        try {
            val updateManager = manager ?: return
            immediateWasRequired = mode == AppUpdatePolicy.Mode.IMMEDIATE
            flowInProgress = updateManager.startUpdateFlowForResult(info, updateLauncher, options)
            if (!flowInProgress) immediateWasRequired = false
        } catch (error: IntentSender.SendIntentException) {
            flowInProgress = false
            immediateWasRequired = false
            Timber.w(error, "Play update flow could not be started; continuing without blocking")
        } catch (error: Exception) {
            flowInProgress = false
            immediateWasRequired = false
            Timber.w(error, "Play update flow failed to start; continuing without blocking")
        }
    }

    private fun showDownloadedUpdateDialog() {
        if (activity.isFinishing || activity.isDestroyed || downloadedDialog?.isShowing == true) return
        downloadedDialog = AlertDialog.Builder(activity)
            .setTitle(R.string.update_ready_title)
            .setMessage(R.string.update_ready_message)
            .setPositiveButton(R.string.update_install_now) { _, _ ->
                manager?.completeUpdate()?.addOnFailureListener { error ->
                    Timber.w(error, "Could not complete flexible update")
                }
            }
            .setNegativeButton(R.string.update_later, null)
            .setOnDismissListener { downloadedDialog = null }
            .show()
    }

    private fun showRequiredUpdateDialog() {
        if (activity.isFinishing || activity.isDestroyed) return
        AlertDialog.Builder(activity)
            .setTitle(R.string.update_required_title)
            .setMessage(R.string.update_required_message)
            .setCancelable(false)
            .setPositiveButton(R.string.update_retry) { _, _ -> retryImmediateUpdate() }
            .setNegativeButton(R.string.update_exit) { _, _ -> activity.finishAffinity() }
            .show()
    }

    private fun retryImmediateUpdate() {
        if (activity.isFinishing || activity.isDestroyed) return
        val updateManager = manager ?: return
        updateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                if (info.updateAvailability() == com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
                ) {
                    startFlow(info, AppUpdatePolicy.Mode.IMMEDIATE)
                }
            }
            .addOnFailureListener { error ->
                Timber.i(error, "Cannot retry Play update now; app will remain usable")
            }
    }

    companion object {
        const val MINIMUM_SUPPORTED_VERSION_CODE = "minimum_supported_version_code"
        private const val PREFERENCES = "app_update_preferences"
        private const val KEY_DISMISSED_VERSION = "dismissed_flexible_version_code"
        private const val REMOTE_CONFIG_FETCH_INTERVAL_SECONDS = 60L * 60L
    }
}
