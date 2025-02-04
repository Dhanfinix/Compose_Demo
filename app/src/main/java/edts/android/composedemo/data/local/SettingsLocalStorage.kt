package edts.android.composedemo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import edts.android.composedemo.constants.ThemeMode
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val THEME_MODE = stringPreferencesKey("THEME_MODE")

class SettingsLocalStorage(
    context: Context
) : BaseDataStorePreference() {
    override val dataStore = context.settingsDataStore
    suspend fun saveThemeMode(themeMode: ThemeMode) = savePreference(THEME_MODE, themeMode.name)
    fun getThemeMode() =
        getDatastoreData().map { pref ->
            ThemeMode.valueOf(pref[THEME_MODE] ?: ThemeMode.SYSTEM.name)
        }
}