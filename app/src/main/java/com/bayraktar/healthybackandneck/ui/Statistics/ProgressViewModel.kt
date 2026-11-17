package com.bayraktar.healthybackandneck.ui.Statistics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
@HiltViewModel
class ProgressViewModel @Inject constructor(repository: WorkoutRepository, @ApplicationContext context: Context) : ViewModel() {
    val sessions = repository.sessions.asLiveData()
    val calculators = DataStoreManage.getInstance(context.applicationContext).calculatorSnapshots().asLiveData()
}
