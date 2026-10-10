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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.time.Clock

enum class BillsTab { Pending, Paid }

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
    val tab: BillsTab = BillsTab.Pending,
    val today: LocalDate,
    val bills: List<Bill> = emptyList(),
    val pendingTotalCents: Long = 0,
    val paidTotalCents: Long = 0,
    val pendingCount: Int = 0,
    val overdueCount: Int = 0,
    val paidCount: Int = 0,
    val monthHasBills: Boolean = false,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class BillsViewModel(
    private val repository: BillRepository,
) : ViewModel() {
    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val selectedMonth = MutableStateFlow(today.yearMonth)
    private val tab = MutableStateFlow(BillsTab.Pending)
    private val _sheets = MutableStateFlow<List<BillSheet>>(emptyList())

    val sheets: StateFlow<List<BillSheet>> = _sheets.asStateFlow()

    val uiState: StateFlow<BillsUiState> = combine(
        selectedMonth,
        tab,
        selectedMonth.flatMapLatest(repository::observeBillsDueIn),
    ) { month, tab, monthBills -> buildState(month, tab, monthBills) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BillsUiState(currentMonth = today.yearMonth, selectedMonth = today.yearMonth, today = today),
        )

    fun selectMonth(month: YearMonth) = selectedMonth.update { month }

    fun selectTab(tab: BillsTab) = this.tab.update { tab }

    fun observeBill(id: Long): Flow<Bill?> = repository.observeBill(id)

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

    private fun buildState(month: YearMonth, tab: BillsTab, monthBills: List<Bill>): BillsUiState {
        val (paid, pending) = monthBills.partition { it.isPaid }
        return BillsUiState(
            currentMonth = today.yearMonth,
            selectedMonth = month,
            tab = tab,
            today = today,
            bills = when (tab) {
                BillsTab.Pending -> pending.sortedWith(compareBy({ it.dueDate }, { it.name }))
                BillsTab.Paid -> paid.sortedByDescending { it.paidDate }
            },
            pendingTotalCents = pending.sumOf { it.expectedAmountCents ?: 0 },
            paidTotalCents = paid.sumOf { it.paidAmountCents ?: 0 },
            pendingCount = pending.size,
            overdueCount = pending.count { it.isOverdue(today) },
            paidCount = paid.size,
            monthHasBills = monthBills.isNotEmpty(),
            isLoading = false,
        )
    }
}
