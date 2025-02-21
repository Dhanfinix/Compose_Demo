package edts.android.composedemo.ui.screen.api_demo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edts.android.composedemo.BuildConfig
import edts.android.composedemo.data.remote.Resource
import edts.android.composedemo.domain.usecase.DemoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiDemoViewModel @Inject constructor(
    private val demoUseCase: DemoUseCase
): ViewModel() {
    private val _uiState= MutableStateFlow(ApiDemoScreenState())
    val uiState: StateFlow<ApiDemoScreenState>
        get() = _uiState

    init {
        getArticles()
    }

    private fun updateLoading(isLoading: Boolean){
        _uiState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private fun setApiKeyExistance(exist: Boolean){
        _uiState.update {
            it.copy(
                isApiKeyExist = exist
            )
        }
    }

    fun getArticles(reload: Boolean = false) {
        try {
            val apiKeyField = BuildConfig::class.java.getField("API_KEY")
            setApiKeyExistance(apiKeyField.get(null) as? String != null)
        } catch (e: NoSuchFieldException){
            setApiKeyExistance(false)
            return
        }
        viewModelScope.launch {
            demoUseCase.getArticles(reload).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        updateLoading(false)
                        _uiState.update {
                            it.copy(
                                articles = result.data?.articles.orEmpty()
                            )
                        }
                    }

                    is Resource.Error -> {
                        updateLoading(false)
                    }
                    is Resource.Loading -> updateLoading(true)
                }
            }
        }
    }
}