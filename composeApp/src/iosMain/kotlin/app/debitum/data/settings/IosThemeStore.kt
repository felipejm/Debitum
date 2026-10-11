package app.debitum.data.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

class IosThemeStore : ThemeStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val state = MutableStateFlow(defaults.boolForKey(DARK_MODE_KEY))

    override val darkMode: StateFlow<Boolean> = state.asStateFlow()

    override suspend fun setDarkMode(enabled: Boolean) {
        defaults.setBool(enabled, forKey = DARK_MODE_KEY)
        state.value = enabled
    }
}
