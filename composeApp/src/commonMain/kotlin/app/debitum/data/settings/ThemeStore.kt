package app.debitum.data.settings

import kotlinx.coroutines.flow.StateFlow

const val SETTINGS_PREFS_NAME = "debitum_settings"
const val DARK_MODE_KEY = "dark_mode"

interface ThemeStore {
    val darkMode: StateFlow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
}
