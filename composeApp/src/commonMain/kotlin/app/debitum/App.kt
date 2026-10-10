package app.debitum

import androidx.compose.runtime.Composable
import app.debitum.ui.navigation.DebitumNavHost
import app.debitum.ui.theme.DebitumTheme

@Composable
fun App() {
    DebitumTheme {
        DebitumNavHost()
    }
}
