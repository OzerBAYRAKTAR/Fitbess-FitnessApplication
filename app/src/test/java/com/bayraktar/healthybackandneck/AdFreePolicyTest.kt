package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.utils.AdFreePolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdFreePolicyTest {
    @Test
    fun onlyActiveAdFreeEntitlementGrantsAccess() {
        assertTrue(AdFreePolicy.hasActiveEntitlement(setOf("ad_free"), "ad_free"))
        assertFalse(AdFreePolicy.hasActiveEntitlement(setOf("premium"), "ad_free"))
        assertFalse(AdFreePolicy.hasActiveEntitlement(emptySet(), "ad_free"))
    }

    @Test
    fun adsAreLoadedOnlyAfterAnInactiveEntitlementIsResolved() {
        assertFalse(AdFreePolicy.mayLoadAds(null))
        assertFalse(AdFreePolicy.mayLoadAds(true))
        assertTrue(AdFreePolicy.mayLoadAds(false))
    }

    @Test
    fun lockedMovesWaitForEntitlementResolutionBeforeOpeningRewardedFlow() {
        assertTrue(AdFreePolicy.entitlementPendingForLockedMove(isLocked = true, adFree = null))
        assertFalse(AdFreePolicy.entitlementPendingForLockedMove(isLocked = true, adFree = false))
        assertFalse(AdFreePolicy.entitlementPendingForLockedMove(isLocked = true, adFree = true))
        assertFalse(AdFreePolicy.entitlementPendingForLockedMove(isLocked = false, adFree = null))
    }
}
