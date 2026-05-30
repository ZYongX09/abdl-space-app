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

/**
 * 设置管理器 — 集中管理所有偏好设置
 */
object SettingsKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val USE_SYSTEM_THEME = booleanPreferencesKey("system_theme")
    val THEME_COLOR_INDEX = intPreferencesKey("theme_color_index")
    val BLUR_ENABLED = booleanPreferencesKey("blur_enabled")
    val ANIMATION_LEVEL = intPreferencesKey("animation_level")  // 0=减少, 1=标准, 2=增强
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

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // ─── 主题模式 ───
    val useSystemTheme: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.USE_SYSTEM_THEME] ?: true }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.DARK_MODE] ?: false }
    val isAmoledDark: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.AMOLED_DARK] ?: false }

    suspend fun toggleSystemTheme() {
        context.dataStore.edit { it[SettingsKeys.USE_SYSTEM_THEME] = !(it[SettingsKeys.USE_SYSTEM_THEME] ?: true) }
    }

    suspend fun toggleDarkMode() {
        context.dataStore.edit { it[SettingsKeys.DARK_MODE] = !(it[SettingsKeys.DARK_MODE] ?: false) }
    }

    suspend fun toggleAmoledDark() {
        context.dataStore.edit { it[SettingsKeys.AMOLED_DARK] = !(it[SettingsKeys.AMOLED_DARK] ?: false) }
    }

    // ─── 主题色 ───
    val themeColorIndex: Flow<Int> = context.dataStore.data.map { it[SettingsKeys.THEME_COLOR_INDEX] ?: 0 }

    suspend fun setThemeColorIndex(index: Int) {
        context.dataStore.edit { it[SettingsKeys.THEME_COLOR_INDEX] = index }
    }

    // ─── 毛玻璃 ───
    val blurEnabled: Flow<Boolean> = context.dataStore.data.map { it[SettingsKeys.BLUR_ENABLED] ?: true }

    suspend fun toggleBlur() {
        context.dataStore.edit { it[SettingsKeys.BLUR_ENABLED] = !(it[SettingsKeys.BLUR_ENABLED] ?: true) }
    }

    // ─── 动画强度 ───
    val animationLevel: Flow<Int> = context.dataStore.data.map { it[SettingsKeys.ANIMATION_LEVEL] ?: 1 }

    suspend fun setAnimationLevel(level: Int) {
        context.dataStore.edit { it[SettingsKeys.ANIMATION_LEVEL] = level }
    }

    // ─── 缓存 ───
    fun getCacheSize(context: Context): String {
        return try {
            val cacheDir = context.cacheDir
            val size = cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
            formatFileSize(size)
        } catch (e: Exception) {
            "未知"
        }
    }

    suspend fun clearCache(context: Context) {
        try {
            context.cacheDir.deleteRecursively()
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }
}
