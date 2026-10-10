package app.debitum.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

/**
 * One bill due in one month. Occurrences of a recurring bill point to their [BillSeriesEntity]
 * and are created on demand; [isDeleted] keeps a removed occurrence from being created again.
 */
@Entity(
    tableName = "bills",
    indices = [
        Index("dueDate"),
        Index(value = ["seriesId", "seriesMonth"], unique = true),
    ],
)
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val expectedAmountCents: Long?,
    val dueDate: LocalDate,
    val paidAmountCents: Long? = null,
    val paidDate: LocalDate? = null,
    val seriesId: Long? = null,
    val seriesMonth: YearMonth? = null,
    val isDeleted: Boolean = false,
)

/** Template of a bill that repeats every month from [startMonth] through [endMonth] (open-ended when null). */
@Entity(tableName = "bill_series")
data class BillSeriesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val expectedAmountCents: Long?,
    val dayOfMonth: Int,
    val startMonth: YearMonth,
    val endMonth: YearMonth? = null,
)
