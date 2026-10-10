package app.debitum.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.datetime.LocalDate

/** Implemented in Swift; presents a SwiftUI graphical DatePicker. [maxYear] is 0 when there is no limit. */
interface IosDatePickerPresenter {
    fun present(
        initialYear: Int,
        initialMonth: Int,
        initialDay: Int,
        maxYear: Int,
        maxMonth: Int,
        maxDay: Int,
        callback: IosDatePickerCallback,
    )
}

interface IosDatePickerCallback {
    fun onDateSelected(year: Int, month: Int, day: Int)
}

val LocalIosDatePickerPresenter = staticCompositionLocalOf<IosDatePickerPresenter> {
    error("IosDatePickerPresenter not provided")
}

@Composable
actual fun rememberDatePickerLauncher(): DatePickerLauncher {
    val presenter = LocalIosDatePickerPresenter.current
    return remember(presenter) {
        DatePickerLauncher { initial, maxDate, onSelected ->
            presenter.present(
                initialYear = initial.year,
                initialMonth = initial.month.ordinal + 1,
                initialDay = initial.day,
                maxYear = maxDate?.year ?: 0,
                maxMonth = maxDate?.let { it.month.ordinal + 1 } ?: 0,
                maxDay = maxDate?.day ?: 0,
                callback = object : IosDatePickerCallback {
                    override fun onDateSelected(year: Int, month: Int, day: Int) {
                        onSelected(LocalDate(year, month, day))
                    }
                },
            )
        }
    }
}
