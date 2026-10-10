package app.debitum.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent

@Composable
actual fun SplashSystemBars() {
    DisposableEffect(Unit) {
        UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleLightContent, true)
        onDispose {
            UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleDarkContent, true)
        }
    }
}
