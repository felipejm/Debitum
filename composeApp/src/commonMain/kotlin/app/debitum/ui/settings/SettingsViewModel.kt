package app.debitum.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.debitum.data.settings.ThemeStore
import kotlinx.coroutines.launch

class SettingsViewModel(private val themeStore: ThemeStore) : ViewModel() {
    val darkMode = themeStore.darkMode

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { themeStore.setDarkMode(enabled) }
    }
}
