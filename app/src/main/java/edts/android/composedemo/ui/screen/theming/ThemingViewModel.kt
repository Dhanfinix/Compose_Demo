package edts.android.composedemo.ui.screen.theming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.data.local.SettingsLocalStorage
import edts.android.composedemo.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemingViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(ThemingScreenState())
    val uiState : StateFlow<ThemingScreenState>
        get() = _uiState

    fun setSelectedTheme(themeMode: ThemeMode) {
        _uiState.update {
            it.copy(
                themeOptions = it.themeOptions.copy(
                    selectedIndex = themeMode.ordinal
                )
            )
        }
    }
    fun changeTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsUseCase.saveTheme(themeMode)
        }
    }
}