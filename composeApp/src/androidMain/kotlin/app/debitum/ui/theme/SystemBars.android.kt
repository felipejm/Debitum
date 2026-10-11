package app.debitum.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

@Composable
actual fun ApplySystemBars(darkIcons: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, darkIcons) {
        val window = (view.context as? Activity)?.window
        if (window != null) setLightBars(window, view, darkIcons)
        onDispose { }
    }
}

internal fun setLightBars(window: Window, view: View, darkIcons: Boolean) {
    if (Build.VERSION.SDK_INT >= 30) {
        val flags = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        window.insetsController?.setSystemBarsAppearance(if (darkIcons) flags else 0, flags)
    } else {
        @Suppress("DEPRECATION")
        var visibility = view.systemUiVisibility
        visibility = if (darkIcons) {
            visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        if (Build.VERSION.SDK_INT >= 26) {
            visibility = if (darkIcons) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
        @Suppress("DEPRECATION")
        view.systemUiVisibility = visibility
    }
}
