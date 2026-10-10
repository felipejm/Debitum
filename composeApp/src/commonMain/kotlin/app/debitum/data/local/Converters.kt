package app.debitum.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

// ISO-8601 text keeps chronological order under SQL string comparison.
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun fromYearMonth(month: YearMonth?): String? = month?.toString()

    @TypeConverter
    fun toYearMonth(value: String?): YearMonth? = value?.let(YearMonth::parse)
}
