package edts.android.composedemo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edts.android.composedemo.domain.model.ArticlesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.demoApiDataStore: DataStore<Preferences> by preferencesDataStore(name = "demo-api")
val DEMO_API = stringPreferencesKey("DEMO_API")

class DemoLocalDataSource(
    context: Context
) : BaseDataStorePreference() {
    override val dataStore = context.demoApiDataStore
    suspend fun saveData(data: ArticlesData){
        val jsonString = Gson().toJson(data)
        savePreference(DEMO_API, jsonString)
    }

    fun getData(): Flow<ArticlesData?> =
        dataStore.data.map {
            val jsonString = it[DEMO_API] ?: return@map null
            val type = object : TypeToken<ArticlesData>() {}.type
            Gson().fromJson(jsonString, type)
        }
}