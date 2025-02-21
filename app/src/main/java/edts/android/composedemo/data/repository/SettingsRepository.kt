package edts.android.composedemo.data.repository

import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.data.local.SettingsLocalStorage
import edts.android.composedemo.domain.irepository.ISettingsRepository

class SettingsRepository(
    private val settingsLocalStorage: SettingsLocalStorage
): ISettingsRepository {
    override suspend fun saveTheme(themeMode: ThemeMode) =
        settingsLocalStorage.saveThemeMode(themeMode)

    override fun getTheme() = settingsLocalStorage.getThemeMode()
}