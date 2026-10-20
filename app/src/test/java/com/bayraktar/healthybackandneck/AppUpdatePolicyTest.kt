package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.utils.AppUpdatePolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class AppUpdatePolicyTest {
    @Test fun softUpdatePromptDoesNotRepeatForSameDismissedVersion() {
        assertEquals(false, AppUpdatePolicy.shouldPromptForFlexibleUpdate(9, 9))
    }

    @Test fun softUpdatePromptReturnsForANewerOfferedVersion() {
        assertEquals(true, AppUpdatePolicy.shouldPromptForFlexibleUpdate(10, 9))
    }

    @Test fun defaultConfigurationUsesDismissibleFlexibleUpdate() {
        assertEquals(
            AppUpdatePolicy.Mode.FLEXIBLE,
            AppUpdatePolicy.choose(7, 0, 8, updateAvailable = true, immediateAllowed = true, flexibleAllowed = true)
        )
    }

    @Test fun minimumVersionUsesImmediateWhenOfferedPlayVersionMeetsThreshold() {
        assertEquals(
            AppUpdatePolicy.Mode.IMMEDIATE,
            AppUpdatePolicy.choose(8, 9, 9, updateAvailable = true, immediateAllowed = true, flexibleAllowed = true)
        )
    }

    @Test fun unavailablePlayUpdateNeverBlocks() {
        assertEquals(
            AppUpdatePolicy.Mode.NONE,
            AppUpdatePolicy.choose(8, 9, 0, updateAvailable = false, immediateAllowed = true, flexibleAllowed = true)
        )
    }

    @Test fun unmetMinimumInStoreFallsBackToFlexible() {
        assertEquals(
            AppUpdatePolicy.Mode.FLEXIBLE,
            AppUpdatePolicy.choose(8, 10, 9, updateAvailable = true, immediateAllowed = true, flexibleAllowed = true)
        )
    }

    @Test fun disallowedImmediateFallsBackToFlexible() {
        assertEquals(
            AppUpdatePolicy.Mode.FLEXIBLE,
            AppUpdatePolicy.choose(8, 9, 9, updateAvailable = true, immediateAllowed = false, flexibleAllowed = true)
        )
    }

    @Test fun noAllowedFlowDoesNotBlock() {
        assertEquals(
            AppUpdatePolicy.Mode.NONE,
            AppUpdatePolicy.choose(8, 9, 9, updateAvailable = true, immediateAllowed = false, flexibleAllowed = false)
        )
    }

    @Test fun installedVersionAtMinimumUsesFlexibleForNewerRelease() {
        assertEquals(
            AppUpdatePolicy.Mode.FLEXIBLE,
            AppUpdatePolicy.choose(9, 9, 10, updateAvailable = true, immediateAllowed = true, flexibleAllowed = true)
        )
    }
}
