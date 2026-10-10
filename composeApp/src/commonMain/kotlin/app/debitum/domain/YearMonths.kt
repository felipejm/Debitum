package app.debitum.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

fun YearMonth.plusMonths(months: Int): YearMonth {
    val index = year * 12 + month.ordinal + months
    return YearMonth(index.floorDiv(12), index.mod(12) + 1)
}

fun monthsBetween(from: YearMonth, to: YearMonth): Int =
    (to.year * 12 + to.month.ordinal) - (from.year * 12 + from.month.ordinal)

/** The given day in this month, moved back to the last day when the month is shorter. */
fun YearMonth.dayClamped(day: Int): LocalDate = LocalDate(year, month, minOf(day, lastDay.day))
