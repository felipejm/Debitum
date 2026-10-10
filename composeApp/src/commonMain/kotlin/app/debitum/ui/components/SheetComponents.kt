package app.debitum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.debitum.resources.Res
import app.debitum.resources.action_change
import app.debitum.resources.action_close
import app.debitum.resources.amount_placeholder
import app.debitum.resources.currency_symbol
import app.debitum.ui.format.formatAmountInput
import app.debitum.ui.icons.DebitumIcons
import app.debitum.ui.theme.DebitumColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val MAX_AMOUNT_DIGITS = 11

/** Hides the sheet with its exit animation, then runs [then]. */
fun interface SheetDismisser {
    fun dismiss(then: () -> Unit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebitumBottomSheet(
    onDismissed: () -> Unit,
    content: @Composable ColumnScope.(SheetDismisser) -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val dismisser = remember(state) {
        SheetDismisser { then -> scope.launch { state.hide() }.invokeOnCompletion { then() } }
    }
    ModalBottomSheet(
        onDismissRequest = onDismissed,
        sheetState = state,
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        containerColor = DebitumColors.SheetBackground,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 14.dp, bottom = 4.dp)
                    .size(width = 48.dp, height = 6.dp)
                    .background(DebitumColors.Handle, CircleShape),
            )
        },
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // The iOS number pad has no key to close it, so tapping outside a field does.
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
        ) {
            content(dismisser)
        }
    }
}

@Composable
fun SheetHeader(
    title: String,
    onClose: () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
                color = DebitumColors.TextDark,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                subtitle()
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DebitumColors.CloseButton)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = DebitumIcons.Close,
                contentDescription = stringResource(Res.string.action_close),
                tint = DebitumColors.TextSecondary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun SheetSubtitle(text: String) {
    Text(text = text, fontSize = 12.sp, color = DebitumColors.TextSecondary)
}

@Composable
fun SheetLink(prefix: String?, link: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (prefix != null) {
            Text(
                text = "$prefix · ",
                fontSize = 12.sp,
                color = DebitumColors.TextSecondary,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        Text(
            text = link,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DebitumColors.Accent,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier, optionalText: String? = null, uppercase: Boolean = false) {
    Row(modifier.padding(bottom = 6.dp)) {
        Text(
            text = if (uppercase) text.uppercase() else text,
            fontSize = 12.sp,
            fontWeight = if (uppercase) FontWeight.Bold else FontWeight.SemiBold,
            letterSpacing = if (uppercase) 0.4.sp else TextUnit.Unspecified,
            color = DebitumColors.TextDark,
        )
        if (optionalText != null) {
            Text(
                text = " $optionalText",
                fontSize = 12.sp,
                color = DebitumColors.TextMuted,
            )
        }
    }
}

@Composable
fun FieldContainer(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, shape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.05f))
            .clip(shape)
            .background(Color.White)
            .border(1.dp, DebitumColors.FieldBorder, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun NameField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    FieldContainer {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DebitumColors.TextDark,
            ),
            cursorBrush = SolidColor(DebitumColors.Accent),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) {
                        Text(placeholder, fontSize = 16.sp, color = DebitumColors.TextMuted)
                    }
                    inner()
                }
            },
        )
    }
}

/** Amount input where digits fill in from the cents: typing 1, 2, 0, 0 shows 12,00. */
@Composable
fun AmountField(cents: Long?, onCentsChange: (Long?) -> Unit, fontSize: TextUnit = 16.sp) {
    val text = cents?.let(::formatAmountInput).orEmpty()
    var field by remember { mutableStateOf(TextFieldValue(text, TextRange(text.length))) }
    if (field.text != text) field = TextFieldValue(text, TextRange(text.length))
    FieldContainer {
        Text(
            text = stringResource(Res.string.currency_symbol),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DebitumColors.Accent,
        )
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value = field,
            onValueChange = { new ->
                val digits = new.text.filter(Char::isDigit).trimStart('0').take(MAX_AMOUNT_DIGITS)
                val newCents = digits.toLongOrNull()
                val newText = newCents?.let(::formatAmountInput).orEmpty()
                field = TextFieldValue(newText, TextRange(newText.length))
                if (newCents != cents) onCentsChange(newCents)
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                color = DebitumColors.TextDark,
            ),
            cursorBrush = SolidColor(DebitumColors.Accent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { inner ->
                Box {
                    if (field.text.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.amount_placeholder),
                            fontSize = fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = DebitumColors.TextMuted,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

@Composable
fun DateField(text: String, onClick: () -> Unit) {
    FieldContainer(onClick = onClick) {
        Box(
            modifier = Modifier.size(32.dp).background(DebitumColors.Surface, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(DebitumIcons.Calendar, contentDescription = null, tint = DebitumColors.Card, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DebitumColors.TextDark,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(Res.string.action_change),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DebitumColors.TextMuted,
        )
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, icon: ImageVector? = DebitumIcons.Check, enabled: Boolean = true) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .alpha(if (enabled) 1f else 0.45f)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = shape,
                ambientColor = DebitumColors.Accent.copy(alpha = 0.55f),
                spotColor = DebitumColors.Accent.copy(alpha = 0.55f),
            )
            .clip(shape)
            .background(DebitumColors.Accent)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp, color = Color.White)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.7f))
            .border(1.dp, DebitumColors.FieldBorder, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DebitumColors.TextSecondary)
    }
}

@Composable
fun DangerButton(text: String, onClick: () -> Unit, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DebitumColors.DangerSoft)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = DebitumColors.OverdueStrong, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            color = DebitumColors.OverdueStrong,
        )
    }
}

@Composable
fun FieldSpacer() = Spacer(Modifier.height(16.dp))
