package app.debitum.data.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidThemeStore(context: Context) : ThemeStore {
    private val prefs = context.getSharedPreferences(SETTINGS_PREFS_NAME, Context.MODE_PRIVATE)
    private val state = MutableStateFlow(prefs.getBoolean(DARK_MODE_KEY, false))

    override val darkMode: StateFlow<Boolean> = state.asStateFlow()

    override suspend fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(DARK_MODE_KEY, enabled).apply()
        state.value = enabled
    }
}
