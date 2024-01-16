package com.bayraktar.healthybackandneck.data.JetpackDataStore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
        val TXT_ACTIVITY_LEVEL = intPreferencesKey("txt_activity_level")
        val TXT_AGE = intPreferencesKey("txt_age")
        val TXT_HEIGHT = intPreferencesKey("txt_height")
        val TXT_WEIGHT = intPreferencesKey("txt_weight")
        val TXT_NECK = intPreferencesKey("txt_neck")
        val TXT_BELLY = intPreferencesKey("txt_belly")
        val TXT_BOOTY = intPreferencesKey("txt_booty")
        val TXT_INDEKS = stringPreferencesKey("txt_indeks")
        val TXT_CATEGORY = stringPreferencesKey("txt_category")
        val TXT_CALORIE = stringPreferencesKey("txt_calorie")
        val TXT_FATRATE = stringPreferencesKey("txt_fatrate")
        val TXT_LOGIN = booleanPreferencesKey("isLogged")

    }

    suspend fun saveGender(data: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[TXT_GENDER] = data
            }
        }
    }
    suspend fun saveLog(data: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[TXT_LOGIN] = data
            }
        }
    }

    suspend fun saveActivityLevel(data: Int) {
        dataStore.edit { pref ->
            pref[TXT_ACTIVITY_LEVEL] = data
        }
    }

    suspend fun saveAge(data: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[TXT_AGE] = data
            }
        }
    }

    suspend fun saveHeight(data: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[TXT_HEIGHT] = data
            }
        }
    }

    suspend fun saveWeight(data: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[TXT_WEIGHT] = data
            }
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
    suspend fun saveIndeks(data: String) {
        dataStore.edit { pref ->
            pref[TXT_INDEKS] = data
        }
    }
    suspend fun saveCategory(data: String) {
        dataStore.edit { pref ->
            pref[TXT_CATEGORY] = data
        }
    }
    suspend fun saveCalorie(data: String) {
        dataStore.edit { pref ->
            pref[TXT_CALORIE] = data
        }
    }
    suspend fun saveFatrate(data: String) {
        dataStore.edit { pref ->
            pref[TXT_FATRATE] = data
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
    fun getLog(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_LOGIN] ?: false
            }
    }

    fun getActivityLevel(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_ACTIVITY_LEVEL] ?: 0
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
    fun getIndeks(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_INDEKS] ?: ""
            }
    }
    fun getCategory(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_CATEGORY] ?: ""
            }
    }
    fun getCalorie(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_CALORIE] ?: ""
            }
    }
    fun getFatRate(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[TXT_FATRATE] ?: ""
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
    suspend fun deleteIndeks() {
        dataStore.edit { pref ->
            pref.remove(TXT_INDEKS)
        }
    }
    suspend fun deleteCategory() {
        dataStore.edit { pref ->
            pref.remove(TXT_CATEGORY)
        }
    }
    suspend fun deleteCalorie() {
        dataStore.edit { pref ->
            pref.remove(TXT_CALORIE)
        }
    }
    suspend fun deleteFatRate() {
        dataStore.edit { pref ->
            pref.remove(TXT_FATRATE)
        }
    }
}