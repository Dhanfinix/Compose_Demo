package dhandev.android.composedemo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dhandev.android.composedemo.data.local.SettingsLocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localStorage: SettingsLocalStorage
): ViewModel() {
    private val _uiState = MutableStateFlow(MainState())
    val uiState : StateFlow<MainState>
        get() = _uiState

    init {
        getTheme()
    }

    private fun getTheme(){
        viewModelScope.launch {
            localStorage.getThemeMode().collectLatest { savedTheme ->
                _uiState.update {
                    it.copy(
                        theme = savedTheme,
                    )
                }
            }
        }
    }

}