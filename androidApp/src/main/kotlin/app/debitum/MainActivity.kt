package app.debitum

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.debitum.data.settings.DARK_MODE_KEY
import app.debitum.data.settings.SETTINGS_PREFS_NAME

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val darkMode = getSharedPreferences(SETTINGS_PREFS_NAME, MODE_PRIVATE)
            .getBoolean(DARK_MODE_KEY, false)
        val barStyle = if (darkMode) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
        enableEdgeToEdge(
            statusBarStyle = barStyle,
            navigationBarStyle = barStyle,
        )
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
