package app.debitum.ui.theme

import androidx.compose.runtime.Composable

/** [darkIcons] matches a light background. Splash overrides this while its dark canvas is visible. */
@Composable
expect fun ApplySystemBars(darkIcons: Boolean)
