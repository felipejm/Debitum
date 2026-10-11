package app.debitum.ui.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.debitum.domain.model.Bill
import app.debitum.domain.model.RecurrenceScope
import app.debitum.domain.repository.BillRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.time.Clock

/** Bottom sheets stacked over the bills list; the last one is on top. */
sealed interface BillSheet {
    data object NewBill : BillSheet

    data class EditBill(val billId: Long) : BillSheet

    data class Payment(val billId: Long) : BillSheet

    data class PaidBill(val billId: Long) : BillSheet
}

data class BillsUiState(
    val currentMonth: YearMonth,
    val selectedMonth: YearMonth,
    val today: LocalDate,
    val bills: List<Bill> = emptyList(),
    val paidTotalCents: Long = 0,
    val pendingCount: Int = 0,
    val overdueCount: Int = 0,
    val monthHasBills: Boolean = false,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class BillsViewModel(
    private val repository: BillRepository,
) : ViewModel() {
    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val selectedMonth = MutableStateFlow(today.yearMonth)
    private val _sheets = MutableStateFlow<List<BillSheet>>(emptyList())

    val sheets: StateFlow<List<BillSheet>> = _sheets.asStateFlow()

    val uiState: StateFlow<BillsUiState> = combine(
        selectedMonth,
        selectedMonth.flatMapLatest(repository::observeBillsDueIn),
    ) { month, monthBills -> buildState(month, monthBills) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BillsUiState(currentMonth = today.yearMonth, selectedMonth = today.yearMonth, today = today),
        )

    fun selectMonth(month: YearMonth) = selectedMonth.update { month }

    fun observeBill(id: Long): Flow<Bill?> = repository.observeBill(id)

    fun observeLastPaidAmount(bill: Bill): Flow<Long?> {
        val seriesId = bill.seriesId ?: return flowOf(null)
        return repository.observeLastPaidAmount(seriesId, bill.id)
    }

    fun openNewBill() = push(BillSheet.NewBill)

    fun openBill(bill: Bill) = push(if (bill.isPaid) BillSheet.PaidBill(bill.id) else BillSheet.Payment(bill.id))

    fun openEditBill(id: Long) = push(BillSheet.EditBill(id))

    fun openEditPayment(id: Long) = push(BillSheet.Payment(id))

    fun closeSheet(sheet: BillSheet) = _sheets.update { it - sheet }

    fun closeAllSheets() = _sheets.update { emptyList() }

    fun createBill(name: String, expectedAmountCents: Long?, dueDate: LocalDate, recurring: Boolean) {
        viewModelScope.launch { repository.createBill(name.trim(), expectedAmountCents, dueDate, recurring) }
    }

    fun updateBill(id: Long, name: String, expectedAmountCents: Long?, dueDate: LocalDate, scope: RecurrenceScope) {
        viewModelScope.launch { repository.updateBill(id, name.trim(), expectedAmountCents, dueDate, scope) }
    }

    fun deleteBill(id: Long, scope: RecurrenceScope) {
        viewModelScope.launch { repository.deleteBill(id, scope) }
    }

    fun registerPayment(id: Long, amountCents: Long?, date: LocalDate) {
        viewModelScope.launch { repository.registerPayment(id, amountCents, date) }
    }

    fun undoPayment(id: Long) {
        viewModelScope.launch { repository.undoPayment(id) }
    }

    private fun push(sheet: BillSheet) = _sheets.update { if (sheet in it) it else it + sheet }

    private fun buildState(month: YearMonth, monthBills: List<Bill>): BillsUiState {
        val pending = monthBills.filter { !it.isPaid }
        return BillsUiState(
            currentMonth = today.yearMonth,
            selectedMonth = month,
            today = today,
            bills = monthBills.sortedWith(
                compareBy<Bill> { it.listStatusRank(today) }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name.trim() },
            ),
            paidTotalCents = monthBills.filter { it.isPaid }.sumOf { it.paidAmountCents ?: 0 },
            pendingCount = pending.size,
            overdueCount = pending.count { it.isOverdue(today) },
            monthHasBills = monthBills.isNotEmpty(),
            isLoading = false,
        )
    }
}

/** Paid → unpaid → overdue, then alphabetical. */
private fun Bill.listStatusRank(today: LocalDate): Int = when {
    isPaid -> 0
    isOverdue(today) -> 2
    else -> 1
}
