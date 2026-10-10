package app.debitum.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.debitum.domain.model.Bill
import app.debitum.resources.Res
import app.debitum.resources.bill_no_amount
import app.debitum.resources.bill_status_overdue
import app.debitum.resources.bill_status_paid
import app.debitum.resources.bills_all_clear
import app.debitum.resources.bills_empty_add_first
import app.debitum.resources.bills_empty_paid_message
import app.debitum.resources.bills_empty_pending_message
import app.debitum.resources.bills_empty_title
import app.debitum.resources.bills_new
import app.debitum.resources.bills_overdue_count
import app.debitum.resources.bills_paid_count
import app.debitum.resources.bills_pending_count
import app.debitum.resources.bills_section_title
import app.debitum.resources.bills_tab_paid
import app.debitum.resources.bills_tab_pending
import app.debitum.resources.bills_total_paid
import app.debitum.resources.bills_total_to_pay
import app.debitum.resources.month_names
import app.debitum.resources.month_with_year
import app.debitum.domain.monthsBetween
import app.debitum.domain.plusMonths
import app.debitum.ui.format.formatBrl
import app.debitum.ui.format.paidOnText
import app.debitum.ui.format.shortDateText
import app.debitum.ui.icons.DebitumIcons
import app.debitum.ui.theme.DebitumColors
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val ScreenPadding = 24.dp
private const val MONTHS_AROUND_CURRENT = 120

@Composable
fun BillsScreen(viewModel: BillsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheets by viewModel.sheets.collectAsStateWithLifecycle()
    BillsContent(
        uiState = uiState,
        onMonthSelected = viewModel::selectMonth,
        onTabSelected = viewModel::selectTab,
        onNewBill = viewModel::openNewBill,
        onBillClick = viewModel::openBill,
    )
    sheets.forEach { sheet ->
        key(sheet) { BillSheetHost(sheet, viewModel, uiState.today) }
    }
}

@Composable
fun BillsContent(
    uiState: BillsUiState,
    onMonthSelected: (YearMonth) -> Unit,
    onTabSelected: (BillsTab) -> Unit,
    onNewBill: () -> Unit,
    onBillClick: (Bill) -> Unit,
) {
    Scaffold(containerColor = DebitumColors.Background) { insets ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = insets.calculateTopPadding() + 24.dp,
                bottom = insets.calculateBottomPadding() + 32.dp,
            ),
        ) {
            item {
                MonthSelector(
                    currentMonth = uiState.currentMonth,
                    selectedMonth = uiState.selectedMonth,
                    onMonthSelected = onMonthSelected,
                )
                Spacer(Modifier.height(12.dp))
            }
            item {
                SummaryCard(uiState, Modifier.padding(horizontal = ScreenPadding))
                Spacer(Modifier.height(28.dp))
            }
            item {
                TabsRow(uiState.tab, onTabSelected, Modifier.padding(horizontal = ScreenPadding))
                Spacer(Modifier.height(24.dp))
            }
            item {
                SectionHeader(onNewBill, Modifier.padding(horizontal = ScreenPadding))
                Spacer(Modifier.height(16.dp))
            }
            if (uiState.bills.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyState(
                        tab = uiState.tab,
                        monthName = monthLabel(uiState.selectedMonth, uiState.currentMonth),
                        showAddButton = !uiState.monthHasBills,
                        onNewBill = onNewBill,
                        modifier = Modifier.padding(horizontal = ScreenPadding),
                    )
                }
            } else {
                items(uiState.bills, key = { it.id }) { bill ->
                    BillRow(
                        bill = bill,
                        today = uiState.today,
                        onClick = { onBillClick(bill) },
                        modifier = Modifier.padding(horizontal = ScreenPadding, vertical = 7.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    currentMonth: YearMonth,
    selectedMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit,
) {
    val firstMonth = currentMonth.plusMonths(-MONTHS_AROUND_CURRENT)
    val months = List(MONTHS_AROUND_CURRENT * 2 + 1) { firstMonth.plusMonths(it) }
    val selectedIndex = monthsBetween(firstMonth, selectedMonth)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedIndex - 2).coerceAtLeast(0))
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem((selectedIndex - 2).coerceAtLeast(0))
    }
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = ScreenPadding - 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(months) { month ->
            val selected = month == selectedMonth
            Text(
                text = monthLabel(month, currentMonth),
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) Color.White else DebitumColors.TextMuted,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) DebitumColors.Accent else Color.Transparent)
                    .clickable { onMonthSelected(month) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun SummaryCard(uiState: BillsUiState, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(32.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = shape,
                ambientColor = DebitumColors.Card.copy(alpha = 0.28f),
                spotColor = DebitumColors.Card.copy(alpha = 0.28f),
            )
            .background(DebitumColors.Card, shape)
            .padding(24.dp),
    ) {
        val isPending = uiState.tab == BillsTab.Pending
        Text(
            text = stringResource(if (isPending) Res.string.bills_total_to_pay else Res.string.bills_total_paid),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = formatBrl(if (isPending) uiState.pendingTotalCents else uiState.paidTotalCents),
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = Color.White,
        )
        Spacer(Modifier.height(16.dp))
        when {
            !isPending -> Pill(
                text = "• " + pluralStringResource(Res.plurals.bills_paid_count, uiState.paidCount, uiState.paidCount),
                textColor = DebitumColors.PaidOnCard,
                background = DebitumColors.Paid.copy(alpha = 0.2f),
            )
            uiState.pendingCount == 0 -> Pill(
                text = stringResource(Res.string.bills_all_clear),
                textColor = DebitumColors.AccentOnCard,
                background = DebitumColors.Accent.copy(alpha = 0.2f),
            )
            uiState.overdueCount > 0 -> Pill(
                text = "• " +
                    pluralStringResource(Res.plurals.bills_pending_count, uiState.pendingCount, uiState.pendingCount) +
                    " · " +
                    pluralStringResource(Res.plurals.bills_overdue_count, uiState.overdueCount, uiState.overdueCount),
                textColor = DebitumColors.OverdueOnCard,
                background = DebitumColors.Overdue.copy(alpha = 0.2f),
            )
            else -> Pill(
                text = "• " + pluralStringResource(Res.plurals.bills_pending_count, uiState.pendingCount, uiState.pendingCount),
                textColor = DebitumColors.AccentOnCard,
                background = DebitumColors.Accent.copy(alpha = 0.2f),
            )
        }
    }
}

@Composable
private fun TabsRow(selected: BillsTab, onTabSelected: (BillsTab) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TabPill(stringResource(Res.string.bills_tab_pending), selected == BillsTab.Pending) {
            onTabSelected(BillsTab.Pending)
        }
        TabPill(stringResource(Res.string.bills_tab_paid), selected == BillsTab.Paid) {
            onTabSelected(BillsTab.Paid)
        }
    }
}

@Composable
private fun TabPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        letterSpacing = 0.3.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        color = if (selected) Color.White else DebitumColors.TextSecondary,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) DebitumColors.Accent else DebitumColors.Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 10.dp),
    )
}

@Composable
private fun SectionHeader(onNewBill: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.bills_section_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp,
            color = DebitumColors.TextDark,
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(DebitumColors.Accent.copy(alpha = 0.2f))
                .clickable(onClick = onNewBill)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(DebitumIcons.Plus, contentDescription = null, tint = DebitumColors.Accent, modifier = Modifier.size(14.dp))
            Text(
                text = stringResource(Res.string.bills_new),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DebitumColors.Accent,
            )
        }
    }
}

@Composable
private fun BillRow(bill: Bill, today: LocalDate, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val overdue = bill.isOverdue(today)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(modifier)
            .heightIn(min = 50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(DebitumColors.Surface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = bill.name.trim().take(1).uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DebitumColors.Accent,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = bill.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DebitumColors.TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = bill.paidDate?.let { paidOnText(it) } ?: shortDateText(bill.dueDate),
                fontSize = 12.sp,
                color = DebitumColors.TextMuted,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            val amount = if (bill.isPaid) bill.paidAmountCents else bill.expectedAmountCents
            Text(
                text = amount?.let(::formatBrl) ?: stringResource(Res.string.bill_no_amount),
                fontSize = 14.sp,
                fontWeight = if (amount != null) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    amount == null -> DebitumColors.TextMuted
                    bill.isPaid -> DebitumColors.Paid
                    overdue -> DebitumColors.Overdue
                    else -> DebitumColors.TextDark
                },
            )
            when {
                bill.isPaid -> StatusPill(
                    text = stringResource(Res.string.bill_status_paid),
                    textColor = DebitumColors.PaidStrong,
                    background = DebitumColors.PaidSoft,
                )
                overdue -> StatusPill(
                    text = stringResource(Res.string.bill_status_overdue),
                    textColor = DebitumColors.OverdueStrong,
                    background = DebitumColors.Accent.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, textColor: Color, background: Color) {
    Spacer(Modifier.height(2.dp))
    Pill(text, textColor, background, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, horizontalPadding = 8.dp)
}

@Composable
private fun Pill(
    text: String,
    textColor: Color,
    background: Color,
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    horizontalPadding: Dp = 10.dp,
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = textColor,
        modifier = Modifier
            .background(background, CircleShape)
            .padding(horizontal = horizontalPadding, vertical = 2.dp),
    )
}

@Composable
private fun EmptyState(
    tab: BillsTab,
    monthName: String,
    showAddButton: Boolean,
    onNewBill: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(DebitumColors.Accent.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(DebitumIcons.ShieldCheck, contentDescription = null, tint = DebitumColors.Accent, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.bills_empty_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DebitumColors.TextDark,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(
                if (tab == BillsTab.Pending) Res.string.bills_empty_pending_message else Res.string.bills_empty_paid_message,
                monthName,
            ),
            fontSize = 12.sp,
            lineHeight = 19.sp,
            color = DebitumColors.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(260.dp),
        )
        if (showAddButton && tab == BillsTab.Pending) {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DebitumColors.Accent)
                    .clickable(onClick = onNewBill)
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(DebitumIcons.Plus, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.bills_empty_add_first),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun monthLabel(month: YearMonth, currentMonth: YearMonth): String {
    val name = stringArrayResource(Res.array.month_names)[month.month.ordinal]
    return if (month.year == currentMonth.year) {
        name
    } else {
        stringResource(Res.string.month_with_year, name, month.year.toString())
    }
}