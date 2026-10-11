package app.debitum.ui.splash

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import app.debitum.ui.theme.setLightBars

@Composable
actual fun SplashSystemBars(restoreDarkIcons: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, restoreDarkIcons) {
        val window = (view.context as? Activity)?.window
        if (window != null) setLightBars(window, view, darkIcons = false)
        onDispose {
            if (window != null) setLightBars(window, view, restoreDarkIcons)
        }
    }
}
