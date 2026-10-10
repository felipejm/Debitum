package app.debitum.ui.format

import androidx.compose.runtime.Composable
import app.debitum.resources.Res
import app.debitum.resources.bill_paid_on
import app.debitum.resources.date_long
import app.debitum.resources.date_short
import app.debitum.resources.date_today
import app.debitum.resources.month_short_names
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/** "10 Nov, 2026" */
@Composable
fun shortDateText(date: LocalDate): String =
    stringResource(Res.string.date_short, date.day.twoDigits(), shortMonthName(date), date.year.toString())

/** "10 Nov 2026", or "Hoje, 10 Nov 2026" when [date] is [today]. */
@Composable
fun longDateText(date: LocalDate, today: LocalDate): String {
    val text = stringResource(Res.string.date_long, date.day.twoDigits(), shortMonthName(date), date.year.toString())
    return if (date == today) stringResource(Res.string.date_today, text) else text
}

/** "Pago em 10 Nov" */
@Composable
fun paidOnText(date: LocalDate): String =
    stringResource(Res.string.bill_paid_on, date.day.twoDigits(), shortMonthName(date))

@Composable
private fun shortMonthName(date: LocalDate): String =
    stringArrayResource(Res.array.month_short_names)[date.month.ordinal]

private fun Int.twoDigits() = toString().padStart(2, '0')
