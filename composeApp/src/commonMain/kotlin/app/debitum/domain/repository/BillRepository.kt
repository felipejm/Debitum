package app.debitum.domain.repository

import app.debitum.domain.model.Bill
import app.debitum.domain.model.RecurrenceScope
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

interface BillRepository {
    fun observeBillsDueIn(month: YearMonth): Flow<List<Bill>>

    fun observeBill(id: Long): Flow<Bill?>

    fun observeLastPaidAmount(seriesId: Long, excludeBillId: Long): Flow<Long?>

    suspend fun createBill(name: String, expectedAmountCents: Long?, dueDate: LocalDate, recurring: Boolean)

    /** [scope] is ignored for bills that are not recurring. */
    suspend fun updateBill(
        id: Long,
        name: String,
        expectedAmountCents: Long?,
        dueDate: LocalDate,
        scope: RecurrenceScope,
    )

    /** [scope] is ignored for bills that are not recurring. */
    suspend fun deleteBill(id: Long, scope: RecurrenceScope)

    suspend fun registerPayment(id: Long, amountCents: Long?, date: LocalDate)

    suspend fun undoPayment(id: Long)
}
