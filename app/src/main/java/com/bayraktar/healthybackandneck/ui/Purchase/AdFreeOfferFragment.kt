package com.bayraktar.healthybackandneck.ui.Purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentAdFreeOfferBinding
import com.bayraktar.healthybackandneck.utils.RevenueCatManager
import kotlinx.coroutines.launch

class AdFreeOfferFragment : Fragment() {
    private var _binding: FragmentAdFreeOfferBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentAdFreeOfferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.purchaseButton.setOnClickListener {
            RevenueCatManager.purchase(requireActivity())
            render(RevenueCatManager.state.value)
        }
        binding.restoreButton.setOnClickListener { RevenueCatManager.restore() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                RevenueCatManager.state.collect(::render)
            }
        }
        render(RevenueCatManager.state.value)
    }

    private fun render(state: RevenueCatManager.State) {
        if (_binding == null) return
        binding.purchaseStatus.text = when {
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
            else -> getString(R.string.ad_free_offer_price, state.price)
        }
        binding.purchaseStatus.visibility = View.VISIBLE
        binding.purchaseButton.text = when {
            state.busy -> getString(R.string.purchase_processing)
            else -> getString(R.string.ad_free_buy_once)
        }
        binding.purchaseButton.isEnabled = state.configured && state.price != null && !state.adFree && !state.busy
        binding.restoreButton.isEnabled = state.configured && !state.busy
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
