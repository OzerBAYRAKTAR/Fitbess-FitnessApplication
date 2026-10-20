package com.bayraktar.healthybackandneck.utils

/** Chooses an update flow only from Play Store state and the remotely configured minimum version. */
internal object AppUpdatePolicy {
    enum class Mode { NONE, FLEXIBLE, IMMEDIATE }

    fun shouldPromptForFlexibleUpdate(offeredVersionCode: Long, dismissedVersionCode: Long): Boolean =
        offeredVersionCode != dismissedVersionCode

    fun choose(
        installedVersionCode: Long,
        minimumSupportedVersionCode: Long,
        availableVersionCode: Long,
        updateAvailable: Boolean,
        immediateAllowed: Boolean,
        flexibleAllowed: Boolean
    ): Mode {
        if (!updateAvailable) return Mode.NONE

        val forceImmediate = minimumSupportedVersionCode > installedVersionCode &&
            availableVersionCode >= minimumSupportedVersionCode && immediateAllowed
        if (forceImmediate) return Mode.IMMEDIATE

        return if (flexibleAllowed) Mode.FLEXIBLE else Mode.NONE
    }
}
