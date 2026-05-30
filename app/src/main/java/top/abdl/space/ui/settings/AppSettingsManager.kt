package top.abdl.space.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 单例 DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val USE_SYSTEM_THEME = booleanPreferencesKey("system_theme")
    val THEME_COLOR_INDEX = intPreferencesKey("theme_color_index")
    val BLUR_ENABLED = booleanPreferencesKey("blur_enabled")
    val ANIMATION_LEVEL = intPreferencesKey("animation_level")
    val AMOLED_DARK = booleanPreferencesKey("amoled_dark")
}

enum class AnimationLevel(val value: Int, val label: String) {
    REDUCED(0, "减少"),
    STANDARD(1, "标准"),
    ENHANCED(2, "增强");
    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: STANDARD
    }
}

class AppSettingsManager(private val context: Context) {

    val useSystemTheme: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.USE_SYSTEM_THEME] ?: true }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.DARK_MODE] ?: false }
    val isAmoledDark: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.AMOLED_DARK] ?: false }
    val themeColorIndex: Flow<Int> = context.dataStore.data.map { it[SettingsKeys.THEME_COLOR_INDEX] ?: 0 }
    val blurEnabled: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.BLUR_ENABLED] ?: true }
    val animationLevel: Flow<Int> = context.dataStore.data.map { it[SettingsKeys.ANIMATION_LEVEL] ?: 1 }

    suspend fun toggleSystemTheme() {
        context.dataStore.edit { it[SettingsKeys.USE_SYSTEM_THEME] = !(it[SettingsKeys.USE_SYSTEM_THEME] ?: true) }
    }
    suspend fun toggleDarkMode() {
        context.dataStore.edit { it[SettingsKeys.DARK_MODE] = !(it[SettingsKeys.DARK_MODE] ?: false) }
    }
    suspend fun toggleAmoledDark() {
        context.dataStore.edit { it[SettingsKeys.AMOLED_DARK] = !(it[SettingsKeys.AMOLED_DARK] ?: false) }
    }
    suspend fun setThemeColorIndex(index: Int) {
        context.dataStore.edit { it[SettingsKeys.THEME_COLOR_INDEX] = index }
    }
    suspend fun toggleBlur() {
        context.dataStore.edit { it[SettingsKeys.BLUR_ENABLED] = !(it[SettingsKeys.BLUR_ENABLED] ?: true) }
    }
    suspend fun setAnimationLevel(level: Int) {
        context.dataStore.edit { it[SettingsKeys.ANIMATION_LEVEL] = level }
    }

    fun getCacheSize(): String {
        return try {
            val bytes = context.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
            when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
                else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
            }
        } catch (_: Exception) { "未知" }
    }

    suspend fun clearCache() {
        try { context.cacheDir.deleteRecursively() } catch (_: Exception) {}
    }
}
