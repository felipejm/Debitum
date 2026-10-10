package app.debitum.ui.components

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

/** Opens the platform's native date picker. */
fun interface DatePickerLauncher {
    fun launch(initial: LocalDate, maxDate: LocalDate?, onSelected: (LocalDate) -> Unit)
}

@Composable
expect fun rememberDatePickerLauncher(): DatePickerLauncher
