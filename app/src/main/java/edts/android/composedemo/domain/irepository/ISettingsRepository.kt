package edts.android.composedemo.domain.irepository

import edts.android.composedemo.constants.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository{
    suspend fun saveTheme(themeMode: ThemeMode)
    fun getTheme(): Flow<ThemeMode>
}