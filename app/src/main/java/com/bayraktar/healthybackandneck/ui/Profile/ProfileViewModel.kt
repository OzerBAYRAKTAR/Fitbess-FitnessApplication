package com.bayraktar.healthybackandneck.ui.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.utils.ReminderCoordinator
import com.bayraktar.healthybackandneck.utils.ReminderKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val reminders: ReminderCoordinator) : ViewModel() {
    data class State(val enabled: Map<ReminderKind, Boolean> = emptyMap(), val busy: Boolean = true, val failed: Boolean = false)
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    init { refresh() }
    fun allowed(kind: ReminderKind? = null) = reminders.allowed(kind)
    fun refresh() = viewModelScope.launch {
        try { _state.value = State(reminders.enabled(), busy = false) }
        catch (error: CancellationException) { throw error }
        catch (_: Exception) { _state.value = _state.value.copy(busy = false, failed = true) }
    }
    fun setEnabled(kind: ReminderKind, enabled: Boolean) = viewModelScope.launch {
        _state.value = _state.value.copy(busy = true, failed = false)
        try {
            reminders.setEnabled(kind, enabled)
            _state.value = State(reminders.enabled(), busy = false)
        } catch (error: CancellationException) { throw error }
        catch (_: Exception) { _state.value = _state.value.copy(busy = false, failed = true) }
    }
}
