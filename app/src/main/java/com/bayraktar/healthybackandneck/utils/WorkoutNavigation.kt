package com.bayraktar.healthybackandneck.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/** A transition belongs to one view instance, shared by its timer and click callbacks. */
internal class WorkoutNavigation(private val fragment: Fragment, private val sourceId: Int) : DefaultLifecycleObserver {
    private var consumed = false

    init { fragment.viewLifecycleOwner.lifecycle.addObserver(this) }

    fun navigate(action: NavDirections, beforeNavigate: () -> Unit) {
        if (consumed || !fragment.isAdded || fragment.parentFragmentManager.isStateSaved ||
            !fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
        val controller = fragment.findNavController()
        if (controller.currentDestination?.id != sourceId) return
        consumed = true
        beforeNavigate()
        controller.navigate(action)
    }

    override fun onDestroy(owner: LifecycleOwner) { consumed = true }
}
