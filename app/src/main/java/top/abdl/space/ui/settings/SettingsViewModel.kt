package top.abdl.space.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true
)

class SettingsViewModel(
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val systemThemeKey = booleanPreferencesKey("system_theme")

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            context.dataStore.data.collect { preferences ->
                val isDarkMode = preferences[darkModeKey] ?: false
                val useSystemTheme = preferences[systemThemeKey] ?: true
                _uiState.value = SettingsUiState(
                    isDarkMode = isDarkMode,
                    useSystemTheme = useSystemTheme
                )
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[darkModeKey] = !(_uiState.value.isDarkMode)
            }
        }
    }

    fun toggleSystemTheme() {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[systemThemeKey] = !(_uiState.value.useSystemTheme)
            }
        }
    }
}
