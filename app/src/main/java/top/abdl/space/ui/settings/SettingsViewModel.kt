package top.abdl.space.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true,
    val isAmoledDark: Boolean = false,
    val themeColorIndex: Int = 0,
    val blurEnabled: Boolean = true,
    val animationLevel: Int = 1,
    val cacheSize: String = "计算中..."
)

class SettingsViewModel(private val appContext: Context) : ViewModel() {

    private val settingsManager = AppSettingsManager(appContext)
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsManager.useSystemTheme.collect { v ->
                _uiState.value = _uiState.value.copy(useSystemTheme = v, cacheSize = settingsManager.getCacheSize())
            }
        }
        viewModelScope.launch {
            settingsManager.isDarkMode.collect { v ->
                _uiState.value = _uiState.value.copy(isDarkMode = v)
            }
        }
        viewModelScope.launch {
            settingsManager.isAmoledDark.collect { v ->
                _uiState.value = _uiState.value.copy(isAmoledDark = v)
            }
        }
        viewModelScope.launch {
            settingsManager.themeColorIndex.collect { v ->
                _uiState.value = _uiState.value.copy(themeColorIndex = v)
            }
        }
        viewModelScope.launch {
            settingsManager.blurEnabled.collect { v ->
                _uiState.value = _uiState.value.copy(blurEnabled = v)
            }
        }
        viewModelScope.launch {
            settingsManager.animationLevel.collect { v ->
                _uiState.value = _uiState.value.copy(animationLevel = v)
            }
        }
    }

    fun toggleSystemTheme() = viewModelScope.launch { settingsManager.toggleSystemTheme() }
    fun toggleDarkMode() = viewModelScope.launch { settingsManager.toggleDarkMode() }
    fun toggleAmoledDark() = viewModelScope.launch { settingsManager.toggleAmoledDark() }
    fun setThemeColorIndex(index: Int) = viewModelScope.launch { settingsManager.setThemeColorIndex(index) }
    fun toggleBlur() = viewModelScope.launch { settingsManager.toggleBlur() }
    fun setAnimationLevel(level: Int) = viewModelScope.launch { settingsManager.setAnimationLevel(level) }

    fun clearCache() = viewModelScope.launch {
        settingsManager.clearCache()
        _uiState.value = _uiState.value.copy(cacheSize = "0 B")
    }
}
