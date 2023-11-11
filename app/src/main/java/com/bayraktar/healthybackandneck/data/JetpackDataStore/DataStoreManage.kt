package com.bayraktar.healthybackandneck.data.JetpackDataStore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class DataStoreManage(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "RATE_KEY")
    private val dataStore = context.dataStore


    companion object {

        @Volatile
        private var INSTANCE: DataStoreManage? = null

        fun getInstance(context: Context): DataStoreManage {
            return INSTANCE ?: synchronized(this) {
                val instance = DataStoreManage(context)
                INSTANCE = instance
                instance
            }
        }

        val TXT_GENDER = stringPreferencesKey("txt_gender")
        val TXT_ACTIVITY_LEVEL = stringPreferencesKey("txt_activity_level")
        val TXT_AGE = intPreferencesKey("txt_age")
        val TXT_HEIGHT = intPreferencesKey("txt_height")
        val TXT_WEIGHT = intPreferencesKey("txt_weight")
        val TXT_NECK = intPreferencesKey("txt_neck")
        val TXT_BELLY = intPreferencesKey("txt_belly")
        val TXT_BOOTY = intPreferencesKey("txt_booty")

    }

    suspend fun saveGender(data: String) {
        dataStore.edit { pref ->
            pref[TXT_GENDER] = data
        }
    }

    suspend fun saveActivityLevel(data: String) {
        dataStore.edit { pref ->
            pref[TXT_ACTIVITY_LEVEL] = data
        }
    }

    suspend fun saveAge(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_AGE] = data
        }
    }

    suspend fun saveHeight(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_HEIGHT] = data
        }
    }

    suspend fun saveWeight(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_WEIGHT] = data
        }
    }

    suspend fun saveNeck(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_NECK] = data
        }
    }

    suspend fun saveBelly(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_BELLY] = data
        }
    }

    suspend fun saveBooty(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_BOOTY] = data
        }
    }


    fun getGender(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_GENDER] ?: ""
            }
    }

    fun getActivityLevel(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_ACTIVITY_LEVEL] ?: ""
            }
    }

    fun getAge(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_AGE] ?: 0
            }
    }

    fun getHeight(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_HEIGHT] ?: 0
            }
    }

    fun getWeight(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_WEIGHT] ?: 0
            }
    }

    fun getNeck(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_NECK] ?: 0
            }
    }

    fun getBelly(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_BELLY] ?: 0
            }
    }

    fun getBooty(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_BOOTY] ?: 0
            }
    }

    suspend fun deleteGender() {
        dataStore.edit { pref ->
            pref.remove(TXT_GENDER)
        }
    }

    suspend fun deleteActivityLevel() {
        dataStore.edit { pref ->
            pref.remove(TXT_ACTIVITY_LEVEL)
        }
    }

    suspend fun deleteAge() {
        dataStore.edit { pref ->
            pref.remove(TXT_AGE)
        }
    }

    suspend fun deleteHeight() {
        dataStore.edit { pref ->
            pref.remove(TXT_HEIGHT)
        }
    }

    suspend fun deleteWeight() {
        dataStore.edit { pref ->
            pref.remove(TXT_WEIGHT)
        }
    }

    suspend fun deleteNeck() {
        dataStore.edit { pref ->
            pref.remove(TXT_NECK)
        }
    }

    suspend fun deleteBelly() {
        dataStore.edit { pref ->
            pref.remove(TXT_BELLY)
        }
    }

    suspend fun deleteBooty() {
        dataStore.edit { pref ->
            pref.remove(TXT_BOOTY)
        }
    }
}