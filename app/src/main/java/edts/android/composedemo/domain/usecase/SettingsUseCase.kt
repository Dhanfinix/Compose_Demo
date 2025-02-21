package edts.android.composedemo.domain.usecase

import edts.android.composedemo.constants.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsUseCase {
    suspend fun saveTheme(themeMode: ThemeMode)
    fun getTheme(): Flow<ThemeMode>
}