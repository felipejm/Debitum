package app.debitum.ui.splash

import androidx.compose.runtime.Composable

@Composable
actual fun SplashSystemBars() {
    // UIApplication.setStatusBarStyle is gone from Kotlin/Native UIKit bindings.
    // The launch screen and splash background already match, so no extra work is needed.
}
