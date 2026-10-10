package app.debitum.ui.components

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@Composable
actual fun rememberDatePickerLauncher(): DatePickerLauncher {
    val context = LocalContext.current
    return remember(context) {
        DatePickerLauncher { initial, maxDate, onSelected ->
            DatePickerDialog(
                context,
                { _, year, month, day -> onSelected(LocalDate(year, month + 1, day)) },
                initial.year,
                initial.month.ordinal,
                initial.day,
            ).apply {
                if (maxDate != null) {
                    datePicker.maxDate = maxDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                }
            }.show()
        }
    }
}
