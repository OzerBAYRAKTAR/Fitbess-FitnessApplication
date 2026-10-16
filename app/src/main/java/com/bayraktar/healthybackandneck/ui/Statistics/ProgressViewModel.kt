package com.bayraktar.healthybackandneck.ui.Statistics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ProgressViewModel @Inject constructor(repository: WorkoutRepository) : ViewModel() {
    val sessions = repository.sessions.asLiveData()
}
