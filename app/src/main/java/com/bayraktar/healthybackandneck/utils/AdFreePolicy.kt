package com.bayraktar.healthybackandneck.utils

/** Pure entitlement and ad policy, kept independent of RevenueCat and AdMob SDK types. */
object AdFreePolicy {
    fun hasActiveEntitlement(activeEntitlementIds: Set<String>, expectedId: String): Boolean =
        expectedId in activeEntitlementIds

    fun entitlementPendingForLockedMove(isLocked: Boolean, adFree: Boolean?): Boolean =
        isLocked && adFree == null

    fun mayLoadAds(adFree: Boolean?): Boolean = adFree == false
}
