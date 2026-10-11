package app.debitum

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.debitum.data.settings.ThemeStore
import app.debitum.ui.navigation.DebitumNavHost
import app.debitum.ui.theme.DebitumTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val themeStore = koinInject<ThemeStore>()
    val darkMode by themeStore.darkMode.collectAsStateWithLifecycle()
    DebitumTheme(dark = darkMode) {
        DebitumNavHost()
    }
}
