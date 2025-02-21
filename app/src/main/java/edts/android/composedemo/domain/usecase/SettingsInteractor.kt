package edts.android.composedemo.domain.usecase

import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.domain.irepository.ISettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsInteractor(
    private val settingsRepository: ISettingsRepository
): SettingsUseCase {
    override suspend fun saveTheme(themeMode: ThemeMode)=
        settingsRepository.saveTheme(themeMode)

    override fun getTheme()=
        settingsRepository.getTheme()
}

class DummySettingsInteractor(): SettingsUseCase{
    override suspend fun saveTheme(themeMode: ThemeMode) {
        TODO("Not yet implemented")
    }

    override fun getTheme(): Flow<ThemeMode> {
        TODO("Not yet implemented")
    }
}