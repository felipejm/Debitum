package app.debitum.domain.model

import kotlinx.datetime.LocalDate

data class Bill(
    val id: Long,
    val name: String,
    val expectedAmountCents: Long?,
    val dueDate: LocalDate,
    val paidAmountCents: Long?,
    val paidDate: LocalDate?,
    val seriesId: Long?,
) {
    val isPaid: Boolean get() = paidDate != null

    val isRecurring: Boolean get() = seriesId != null

    fun isOverdue(today: LocalDate): Boolean = !isPaid && dueDate < today
}

/** Which occurrences of a recurring bill an edit or deletion applies to. */
enum class RecurrenceScope { ThisMonth, ThisAndFollowing }
