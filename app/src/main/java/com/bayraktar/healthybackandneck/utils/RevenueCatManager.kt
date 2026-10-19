package com.bayraktar.healthybackandneck.utils

import android.app.Activity
import android.content.Context
import com.bayraktar.healthybackandneck.BuildConfig
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** RevenueCat is the source of truth for purchase entitlements; local flags never grant access. */
object RevenueCatManager {
    const val AD_FREE_ENTITLEMENT = "ad_free"
    const val LIFETIME_PRODUCT_ID = "ad_free_lifetime"

    enum class Result { PURCHASED, RESTORED, NOTHING_TO_RESTORE, CANCELLED, ERROR, RESTORE_ERROR, UNAVAILABLE }

    data class State(
        val configured: Boolean = false,
        val entitlementResolved: Boolean = false,
        val adFree: Boolean = false,
        val offeringResolved: Boolean = false,
        val busy: Boolean = false,
        val price: String? = null,
        val result: Result? = null,
    )

    private val _adFree = MutableStateFlow<Boolean?>(null)
    val adFree = _adFree.asStateFlow()
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private var lifetimePackage: Package? = null
    private var initialized = false
    private var configured = false

    /** Ads stay paused while a configured SDK is resolving cached/verified customer info. */
    fun adsMayLoad(): Boolean = AdFreePolicy.mayLoadAds(_adFree.value)

    fun initialize(context: Context, apiKey: String = BuildConfig.REVENUECAT_ANDROID_API_KEY) {
        if (initialized) return
        initialized = true
        if (apiKey.isBlank() || apiKey.contains("YOUR_", ignoreCase = true) || apiKey.startsWith("test_")) {
            _adFree.value = false
            _state.value = State(configured = false, entitlementResolved = true, adFree = false, result = Result.UNAVAILABLE)
            return
        }

        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
        Purchases.configure(PurchasesConfiguration.Builder(context.applicationContext, apiKey).build())
        configured = true
        _state.value = State(configured = true)
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { info ->
            updateEntitlement(info)
        }
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = {
                // A failed lookup never grants an entitlement. Resume ordinary ads after the lookup fails.
                _adFree.value = false
                _state.value = _state.value.copy(entitlementResolved = true, adFree = false)
            },
            onSuccess = ::updateEntitlement,
        )
        refreshOffering()
    }

    private fun updateEntitlement(info: com.revenuecat.purchases.CustomerInfo) {
        val active = AdFreePolicy.hasActiveEntitlement(info.entitlements.active.keys, AD_FREE_ENTITLEMENT)
        _adFree.value = active
        _state.value = _state.value.copy(entitlementResolved = true, adFree = active)
    }

    private fun refreshOffering() {
        if (!configured) return
        Purchases.sharedInstance.getOfferingsWith(
            onError = {
                lifetimePackage = null
                _state.value = _state.value.copy(offeringResolved = true, price = null)
            },
            onSuccess = { offerings ->
                lifetimePackage = offerings.current?.availablePackages
                    ?.firstOrNull { it.product.id == LIFETIME_PRODUCT_ID }
                val product = lifetimePackage?.product
                _state.value = _state.value.copy(offeringResolved = true, price = product?.price?.formatted)
            },
        )
    }

    fun purchase(activity: Activity) {
        val packageToBuy = lifetimePackage
        if (!configured || packageToBuy == null) {
            _state.value = _state.value.copy(result = Result.UNAVAILABLE)
            return
        }
        _state.value = _state.value.copy(busy = true, result = null)
        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(activity, packageToBuy).build(),
            onError = { _, cancelled ->
                _state.value = _state.value.copy(
                    busy = false,
                    result = if (cancelled) Result.CANCELLED else Result.ERROR,
                )
            },
            onSuccess = { _, info ->
                updateEntitlement(info)
                _state.value = _state.value.copy(
                    busy = false,
                    result = if (_state.value.adFree) Result.PURCHASED else Result.ERROR,
                )
            },
        )
    }

    fun restore() {
        if (!configured) {
            _state.value = _state.value.copy(result = Result.UNAVAILABLE)
            return
        }
        _state.value = _state.value.copy(busy = true, result = null)
        Purchases.sharedInstance.restorePurchasesWith(
            onError = {
                _state.value = _state.value.copy(busy = false, result = Result.RESTORE_ERROR)
            },
            onSuccess = { info ->
                updateEntitlement(info)
                _state.value = _state.value.copy(
                    busy = false,
                    result = if (_state.value.adFree) Result.RESTORED else Result.NOTHING_TO_RESTORE,
                )
            },
        )
    }
}
