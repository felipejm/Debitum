package app.debitum.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.debitum.domain.model.Bill
import app.debitum.domain.model.RecurrenceScope
import app.debitum.resources.Res
import app.debitum.resources.action_cancel
import app.debitum.resources.bill_form_amount
import app.debitum.resources.bill_form_delete
import app.debitum.resources.bill_form_due_date
import app.debitum.resources.bill_form_edit_subtitle
import app.debitum.resources.bill_form_edit_title
import app.debitum.resources.bill_form_name
import app.debitum.resources.bill_form_name_placeholder
import app.debitum.resources.bill_form_new_subtitle
import app.debitum.resources.bill_form_new_title
import app.debitum.resources.bill_form_optional
import app.debitum.resources.bill_form_recurring
import app.debitum.resources.bill_form_recurring_hint
import app.debitum.resources.bill_form_recurring_locked
import app.debitum.resources.bill_form_save_edit
import app.debitum.resources.bill_form_save_new
import app.debitum.resources.bill_no_amount
import app.debitum.resources.delete_confirm
import app.debitum.resources.delete_message
import app.debitum.resources.delete_title
import app.debitum.resources.paid_edit_payment
import app.debitum.resources.paid_title
import app.debitum.resources.paid_undo_payment
import app.debitum.resources.payment_amount
import app.debitum.resources.payment_confirm
import app.debitum.resources.payment_date
import app.debitum.resources.payment_edit_bill
import app.debitum.resources.payment_edit_title
import app.debitum.resources.payment_title
import app.debitum.resources.scope_delete_message
import app.debitum.resources.scope_delete_title
import app.debitum.resources.scope_edit_message
import app.debitum.resources.scope_edit_title
import app.debitum.resources.scope_this_and_following
import app.debitum.resources.scope_this_month
import app.debitum.ui.components.AmountField
import app.debitum.ui.components.DangerButton
import app.debitum.ui.components.DateField
import app.debitum.ui.components.DebitumBottomSheet
import app.debitum.ui.components.FieldContainer
import app.debitum.ui.components.FieldLabel
import app.debitum.ui.components.FieldSpacer
import app.debitum.ui.components.NameField
import app.debitum.ui.components.PrimaryButton
import app.debitum.ui.components.SecondaryButton
import app.debitum.ui.components.SheetDismisser
import app.debitum.ui.components.SheetHeader
import app.debitum.ui.components.SheetLink
import app.debitum.ui.components.SheetSubtitle
import app.debitum.ui.components.rememberDatePickerLauncher
import app.debitum.ui.format.formatBrl
import app.debitum.ui.format.longDateText
import app.debitum.ui.format.shortDateText
import app.debitum.ui.icons.DebitumIcons
import app.debitum.ui.theme.DebitumColors
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun BillSheetHost(sheet: BillSheet, viewModel: BillsViewModel, today: LocalDate) {
    val onDismissed = { viewModel.closeSheet(sheet) }
    when (sheet) {
        BillSheet.NewBill -> BillFormSheet(
            bill = null,
            today = today,
            onDismissed = onDismissed,
            onCreate = viewModel::createBill,
            onUpdate = { _, _, _, _ -> },
            onDelete = {},
            onDeleted = viewModel::closeAllSheets,
        )
        is BillSheet.EditBill -> WithBill(sheet.billId, viewModel) { bill ->
            BillFormSheet(
                bill = bill,
                today = today,
                onDismissed = onDismissed,
                onCreate = { _, _, _, _ -> },
                onUpdate = { name, cents, date, scope -> viewModel.updateBill(bill.id, name, cents, date, scope) },
                onDelete = { scope -> viewModel.deleteBill(bill.id, scope) },
                onDeleted = viewModel::closeAllSheets,
            )
        }
        is BillSheet.Payment -> WithBill(sheet.billId, viewModel) { bill ->
            val lastPaid by remember(bill.id, bill.seriesId) {
                viewModel.observeLastPaidAmount(bill)
            }.collectAsStateWithLifecycle(initialValue = null)
            PaymentSheet(
                bill = bill,
                today = today,
                lastPaidCents = lastPaid,
                onDismissed = onDismissed,
                onEditBill = { viewModel.openEditBill(bill.id) },
                onConfirm = { cents, date -> viewModel.registerPayment(bill.id, cents, date) },
                onConfirmed = viewModel::closeAllSheets,
            )
        }
        is BillSheet.PaidBill -> WithBill(sheet.billId, viewModel) { bill ->
            PaidBillSheet(
                bill = bill,
                today = today,
                onDismissed = onDismissed,
                onEditBill = { viewModel.openEditBill(bill.id) },
                onEditPayment = { viewModel.openEditPayment(bill.id) },
                onUndo = { viewModel.undoPayment(bill.id) },
                onUndone = viewModel::closeAllSheets,
            )
        }
    }
}

@Composable
private fun WithBill(id: Long, viewModel: BillsViewModel, content: @Composable (Bill) -> Unit) {
    val bill by remember(id) { viewModel.observeBill(id) }.collectAsStateWithLifecycle(initialValue = null)
    bill?.let { content(it) }
}

@Composable
private fun BillFormSheet(
    bill: Bill?,
    today: LocalDate,
    onDismissed: () -> Unit,
    onCreate: (name: String, cents: Long?, dueDate: LocalDate, recurring: Boolean) -> Unit,
    onUpdate: (name: String, cents: Long?, dueDate: LocalDate, scope: RecurrenceScope) -> Unit,
    onDelete: (RecurrenceScope) -> Unit,
    onDeleted: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(bill?.name.orEmpty()) }
    var cents by remember(bill?.id) { mutableStateOf(bill?.expectedAmountCents) }
    var dueDate by remember { mutableStateOf(bill?.dueDate ?: today) }
    var recurring by rememberSaveable { mutableStateOf(bill?.isRecurring ?: true) }
    var dialog by remember { mutableStateOf<FormDialog?>(null) }
    val datePicker = rememberDatePickerLauncher()
    val isNew = bill == null

    DebitumBottomSheet(onDismissed = onDismissed) { sheet ->
        SheetHeader(
            title = stringResource(if (isNew) Res.string.bill_form_new_title else Res.string.bill_form_edit_title),
            onClose = { sheet.dismiss(onDismissed) },
            subtitle = {
                SheetSubtitle(
                    stringResource(if (isNew) Res.string.bill_form_new_subtitle else Res.string.bill_form_edit_subtitle),
                )
            },
        )
        Spacer(Modifier.height(20.dp))

        FieldLabel(stringResource(Res.string.bill_form_name))
        NameField(name, { name = it }, stringResource(Res.string.bill_form_name_placeholder))
        FieldSpacer()

        FieldLabel(stringResource(Res.string.bill_form_due_date))
        DateField(shortDateText(dueDate)) {
            datePicker.launch(dueDate, maxDate = null) { dueDate = it }
        }
        FieldSpacer()

        FieldLabel(stringResource(Res.string.bill_form_amount), optionalText = stringResource(Res.string.bill_form_optional))
        AmountField(cents, { cents = it })
        FieldSpacer()

        RecurringToggle(
            checked = recurring,
            enabled = isNew,
            onCheckedChange = { recurring = it },
        )
        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(if (isNew) Res.string.bill_form_save_new else Res.string.bill_form_save_edit),
            enabled = name.isNotBlank(),
            onClick = {
                when {
                    bill == null -> {
                        onCreate(name, cents, dueDate, recurring)
                        sheet.dismiss(onDismissed)
                    }
                    name.trim() == bill.name && cents == bill.expectedAmountCents && dueDate == bill.dueDate ->
                        sheet.dismiss(onDismissed)
                    bill.isRecurring -> dialog = FormDialog.EditScope
                    else -> {
                        onUpdate(name, cents, dueDate, RecurrenceScope.ThisMonth)
                        sheet.dismiss(onDismissed)
                    }
                }
            },
        )
        if (bill != null) {
            Spacer(Modifier.height(10.dp))
            DangerButton(
                text = stringResource(Res.string.bill_form_delete),
                icon = DebitumIcons.Trash,
                onClick = { dialog = if (bill.isRecurring) FormDialog.DeleteScope else FormDialog.DeleteConfirm },
            )
        }

        when (dialog) {
            FormDialog.EditScope -> ScopeDialog(
                title = stringResource(Res.string.scope_edit_title),
                message = stringResource(Res.string.scope_edit_message),
                onDismiss = { dialog = null },
                onScopeSelected = { scope ->
                    dialog = null
                    onUpdate(name, cents, dueDate, scope)
                    sheet.dismiss(onDismissed)
                },
            )
            FormDialog.DeleteScope -> ScopeDialog(
                title = stringResource(Res.string.scope_delete_title),
                message = stringResource(Res.string.scope_delete_message),
                onDismiss = { dialog = null },
                onScopeSelected = { scope ->
                    dialog = null
                    onDelete(scope)
                    sheet.dismiss(onDeleted)
                },
            )
            FormDialog.DeleteConfirm -> DeleteDialog(
                billName = bill?.name.orEmpty(),
                onDismiss = { dialog = null },
                onConfirm = {
                    dialog = null
                    onDelete(RecurrenceScope.ThisMonth)
                    sheet.dismiss(onDeleted)
                },
            )
            null -> Unit
        }
    }
}

private enum class FormDialog { EditScope, DeleteScope, DeleteConfirm }

@Composable
private fun RecurringToggle(checked: Boolean, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    FieldContainer(
        modifier = Modifier.alpha(if (enabled) 1f else 0.6f),
        onClick = if (enabled) ({ onCheckedChange(!checked) }) else null,
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(DebitumColors.Accent.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(DebitumIcons.Repeat, contentDescription = null, tint = DebitumColors.Accent, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.bill_form_recurring),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DebitumColors.TextDark,
            )
            Text(
                text = stringResource(if (enabled) Res.string.bill_form_recurring_hint else Res.string.bill_form_recurring_locked),
                fontSize = 11.sp,
                color = DebitumColors.TextMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DebitumColors.Accent,
                checkedThumbColor = Color.White,
                checkedBorderColor = DebitumColors.Accent,
                disabledCheckedTrackColor = DebitumColors.Accent,
                disabledCheckedThumbColor = Color.White,
            ),
        )
    }
}

@Composable
private fun PaymentSheet(
    bill: Bill,
    today: LocalDate,
    lastPaidCents: Long?,
    onDismissed: () -> Unit,
    onEditBill: () -> Unit,
    onConfirm: (cents: Long?, date: LocalDate) -> Unit,
    onConfirmed: () -> Unit,
) {
    key(bill.id) {
        // Follows the bill's amount (which may be edited from this sheet) until the user types one.
        var typedCents by rememberSaveable { mutableStateOf<Long?>(null) }
        var amountTyped by rememberSaveable { mutableStateOf(false) }
        val cents = if (amountTyped) {
            typedCents
        } else {
            bill.paidAmountCents ?: bill.expectedAmountCents ?: lastPaidCents
        }
        var date by remember { mutableStateOf(bill.paidDate ?: today) }
        val datePicker = rememberDatePickerLauncher()

        DebitumBottomSheet(onDismissed = onDismissed) { sheet ->
            SheetHeader(
                title = stringResource(if (bill.isPaid) Res.string.payment_edit_title else Res.string.payment_title),
                onClose = { sheet.dismiss(onDismissed) },
                subtitle = { SheetLink(bill.name, stringResource(Res.string.payment_edit_bill), onEditBill) },
            )
            Spacer(Modifier.height(20.dp))

            FieldLabel(stringResource(Res.string.payment_amount), uppercase = true)
            AmountField(
                cents = cents,
                onCentsChange = {
                    typedCents = it
                    amountTyped = true
                },
                fontSize = 20.sp,
            )
            FieldSpacer()

            FieldLabel(stringResource(Res.string.payment_date), uppercase = true)
            DateField(longDateText(date, today)) {
                datePicker.launch(date, maxDate = today) { date = it }
            }
            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(Res.string.payment_confirm),
                onClick = {
                    onConfirm(cents, date)
                    sheet.dismiss(onConfirmed)
                },
            )
            Spacer(Modifier.height(10.dp))
            SecondaryButton(stringResource(Res.string.action_cancel)) { sheet.dismiss(onDismissed) }
        }
    }
}

@Composable
private fun PaidBillSheet(
    bill: Bill,
    today: LocalDate,
    onDismissed: () -> Unit,
    onEditBill: () -> Unit,
    onEditPayment: () -> Unit,
    onUndo: () -> Unit,
    onUndone: () -> Unit,
) {
    DebitumBottomSheet(onDismissed = onDismissed) { sheet ->
        SheetHeader(
            title = stringResource(Res.string.paid_title),
            onClose = { sheet.dismiss(onDismissed) },
            subtitle = { SheetLink(bill.name, stringResource(Res.string.payment_edit_bill), onEditBill) },
        )
        Spacer(Modifier.height(20.dp))

        FieldLabel(stringResource(Res.string.payment_amount), uppercase = true)
        FieldContainer {
            Text(
                text = bill.paidAmountCents?.let(::formatBrl) ?: stringResource(Res.string.bill_no_amount),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (bill.paidAmountCents != null) DebitumColors.Paid else DebitumColors.TextMuted,
                modifier = Modifier.weight(1f),
            )
        }
        FieldSpacer()

        FieldLabel(stringResource(Res.string.payment_date), uppercase = true)
        FieldContainer {
            Text(
                text = bill.paidDate?.let { longDateText(it, today) }.orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DebitumColors.TextDark,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(24.dp))

        PrimaryButton(text = stringResource(Res.string.paid_edit_payment), icon = null, onClick = onEditPayment)
        Spacer(Modifier.height(10.dp))
        DangerButton(
            text = stringResource(Res.string.paid_undo_payment),
            onClick = {
                onUndo()
                sheet.dismiss(onUndone)
            },
        )
    }
}

@Composable
private fun ScopeDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onScopeSelected: (RecurrenceScope) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DebitumColors.SheetBackground,
        title = { Text(title, fontWeight = FontWeight.Bold, color = DebitumColors.TextDark) },
        text = { Text(message, color = DebitumColors.TextSecondary) },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                DialogButton(stringResource(Res.string.scope_this_month)) { onScopeSelected(RecurrenceScope.ThisMonth) }
                DialogButton(stringResource(Res.string.scope_this_and_following)) {
                    onScopeSelected(RecurrenceScope.ThisAndFollowing)
                }
                DialogButton(stringResource(Res.string.action_cancel), DebitumColors.TextSecondary, onDismiss)
            }
        },
    )
}

@Composable
private fun DeleteDialog(billName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DebitumColors.SheetBackground,
        title = { Text(stringResource(Res.string.delete_title), fontWeight = FontWeight.Bold, color = DebitumColors.TextDark) },
        text = { Text(stringResource(Res.string.delete_message, billName), color = DebitumColors.TextSecondary) },
        confirmButton = { DialogButton(stringResource(Res.string.delete_confirm), DebitumColors.OverdueStrong, onConfirm) },
        dismissButton = { DialogButton(stringResource(Res.string.action_cancel), DebitumColors.TextSecondary, onDismiss) },
    )
}

@Composable
private fun DialogButton(text: String, color: Color = DebitumColors.Accent, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, fontWeight = FontWeight.SemiBold, color = color)
    }
}
