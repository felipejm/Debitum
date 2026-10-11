package app.debitum.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import app.debitum.resources.Res
import app.debitum.resources.plus_jakarta_sans_bold
import app.debitum.resources.plus_jakarta_sans_extrabold
import app.debitum.resources.plus_jakarta_sans_medium
import app.debitum.resources.plus_jakarta_sans_regular
import app.debitum.resources.plus_jakarta_sans_semibold
import org.jetbrains.compose.resources.Font

class DebitumPalette(
    val background: Color,
    val card: Color,
    val accent: Color,
    val accentOnCard: Color,
    val textDark: Color,
    val textMuted: Color,
    val textSecondary: Color,
    val surface: Color,
    val sheetBackground: Color,
    val handle: Color,
    val closeButton: Color,
    val fieldBorder: Color,
    val dangerSoft: Color,
    val paid: Color,
    val paidStrong: Color,
    val paidSoft: Color,
    val paidOnCard: Color,
    val overdue: Color,
    val overdueStrong: Color,
    val overdueOnCard: Color,
    val gearBackground: Color,
    val gearBorder: Color,
    val gearIcon: Color,
    val isDark: Boolean,
)

private val LightPalette = DebitumPalette(
    background = Color(0xFFFFFFFF),
    card = Color(0xFF2C2036),
    accent = Color(0xFFDD785D),
    accentOnCard = Color(0xFFFFA084),
    textDark = Color(0xFF1E1926),
    textMuted = Color(0xFF9CA3AF),
    textSecondary = Color(0xFF6B7280),
    surface = Color(0xFFF7F6F9),
    sheetBackground = Color(0xFFFAF9FC),
    handle = Color(0xFFD1D5DB),
    closeButton = Color(0xFFF3F4F6),
    fieldBorder = Color(0xFFEEEBF2),
    dangerSoft = Color(0xFFFFF1F2),
    paid = Color(0xFF10B981),
    paidStrong = Color(0xFF059669),
    paidSoft = Color(0xFFECFDF5),
    paidOnCard = Color(0xFF34D399),
    overdue = Color(0xFFEF4444),
    overdueStrong = Color(0xFFF43F5E),
    overdueOnCard = Color(0xFFFF8A8A),
    gearBackground = Color(0xFFFFFFFF),
    gearBorder = Color(0xFFF3F4F6),
    gearIcon = Color(0xFF291E34),
    isDark = false,
)

private val DarkPalette = DebitumPalette(
    background = Color(0xFF141117),
    card = Color(0xFF2C2036),
    accent = Color(0xFFDD785D),
    accentOnCard = Color(0xFFFFA084),
    textDark = Color(0xFFF4F1F6),
    textMuted = Color(0xFF9B93A6),
    textSecondary = Color(0xFFC8C0D1),
    surface = Color(0xFF221C29),
    sheetBackground = Color(0xFF1C1722),
    handle = Color(0xFF4A4254),
    closeButton = Color(0xFF2A2432),
    fieldBorder = Color(0xFF3A3344),
    dangerSoft = Color(0xFF3A2428),
    paid = Color(0xFF34D399),
    paidStrong = Color(0xFF6EE7B7),
    paidSoft = Color(0xFF14352C),
    paidOnCard = Color(0xFF34D399),
    overdue = Color(0xFFF87171),
    overdueStrong = Color(0xFFFB7185),
    overdueOnCard = Color(0xFFFF8A8A),
    gearBackground = Color(0xFF221C29),
    gearBorder = Color(0xFF3A3344),
    gearIcon = Color(0xFFF4F1F6),
    isDark = true,
)

val LocalDebitumColors = staticCompositionLocalOf { LightPalette }

object DebitumColors {
    val Background: Color @Composable get() = LocalDebitumColors.current.background
    val Card: Color @Composable get() = LocalDebitumColors.current.card
    val Accent: Color @Composable get() = LocalDebitumColors.current.accent
    val AccentOnCard: Color @Composable get() = LocalDebitumColors.current.accentOnCard
    val TextDark: Color @Composable get() = LocalDebitumColors.current.textDark
    val TextMuted: Color @Composable get() = LocalDebitumColors.current.textMuted
    val TextSecondary: Color @Composable get() = LocalDebitumColors.current.textSecondary
    val Surface: Color @Composable get() = LocalDebitumColors.current.surface
    val SheetBackground: Color @Composable get() = LocalDebitumColors.current.sheetBackground
    val Handle: Color @Composable get() = LocalDebitumColors.current.handle
    val CloseButton: Color @Composable get() = LocalDebitumColors.current.closeButton
    val FieldBorder: Color @Composable get() = LocalDebitumColors.current.fieldBorder
    val DangerSoft: Color @Composable get() = LocalDebitumColors.current.dangerSoft
    val Paid: Color @Composable get() = LocalDebitumColors.current.paid
    val PaidStrong: Color @Composable get() = LocalDebitumColors.current.paidStrong
    val PaidSoft: Color @Composable get() = LocalDebitumColors.current.paidSoft
    val PaidOnCard: Color @Composable get() = LocalDebitumColors.current.paidOnCard
    val Overdue: Color @Composable get() = LocalDebitumColors.current.overdue
    val OverdueStrong: Color @Composable get() = LocalDebitumColors.current.overdueStrong
    val OverdueOnCard: Color @Composable get() = LocalDebitumColors.current.overdueOnCard
    val GearBackground: Color @Composable get() = LocalDebitumColors.current.gearBackground
    val GearBorder: Color @Composable get() = LocalDebitumColors.current.gearBorder
    val GearIcon: Color @Composable get() = LocalDebitumColors.current.gearIcon
    val isDark: Boolean @Composable get() = LocalDebitumColors.current.isDark

    fun avatarLetter(name: String): Color {
        val letter = name.trim().firstOrNull()?.baseLetter() ?: return LightPalette.accent
        return AvatarLetters[letter - 'A']
    }
}

private val AvatarLetters = listOf(
    Color(0xFFF43F5E), // A — rose, like Aluguel in Stitch
    Color(0xFF3B82F6), // B
    Color(0xFF9333EA), // C — purple, like Cartão
    Color(0xFF14B8A6), // D
    Color(0xFFF59E0B), // E — amber, like Energia
    Color(0xFF8B5CF6), // F — violet, like Fatura
    Color(0xFF22C55E), // G
    Color(0xFFF97316), // H
    Color(0xFFDD785D), // I — terracotta, like Internet
    Color(0xFF06B6D4), // J
    Color(0xFF6366F1), // K
    Color(0xFF65A30D), // L
    Color(0xFFEC4899), // M
    Color(0xFF7C3AED), // N
    Color(0xFFEA580C), // O
    Color(0xFF0EA5E9), // P
    Color(0xFFDB2777), // Q
    Color(0xFF10B981), // R — emerald, like Reembolso
    Color(0xFFCA8A04), // S
    Color(0xFF4F46E5), // T
    Color(0xFF0891B2), // U
    Color(0xFF16A34A), // V
    Color(0xFFC026D3), // W
    Color(0xFFDC2626), // X
    Color(0xFFD97706), // Y
    Color(0xFF2563EB), // Z
)

private fun Char.baseLetter(): Char = when (uppercaseChar()) {
    'Á', 'À', 'Â', 'Ã' -> 'A'
    'É', 'Ê' -> 'E'
    'Í' -> 'I'
    'Ó', 'Ô', 'Õ' -> 'O'
    'Ú', 'Ü' -> 'U'
    'Ç' -> 'C'
    else -> uppercaseChar().let { if (it in 'A'..'Z') it else 'A' }
}

@Composable
fun plusJakartaSans() = FontFamily(
    Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(Res.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

@Composable
fun DebitumTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    val palette = if (dark) DarkPalette else LightPalette
    val family = plusJakartaSans()
    val base = Typography()
    val typography = Typography(
        displayLarge = base.displayLarge.withFamily(family),
        displayMedium = base.displayMedium.withFamily(family),
        displaySmall = base.displaySmall.withFamily(family),
        headlineLarge = base.headlineLarge.withFamily(family),
        headlineMedium = base.headlineMedium.withFamily(family),
        headlineSmall = base.headlineSmall.withFamily(family),
        titleLarge = base.titleLarge.withFamily(family),
        titleMedium = base.titleMedium.withFamily(family),
        titleSmall = base.titleSmall.withFamily(family),
        bodyLarge = base.bodyLarge.withFamily(family),
        bodyMedium = base.bodyMedium.withFamily(family),
        bodySmall = base.bodySmall.withFamily(family),
        labelLarge = base.labelLarge.withFamily(family),
        labelMedium = base.labelMedium.withFamily(family),
        labelSmall = base.labelSmall.withFamily(family),
    )
    CompositionLocalProvider(LocalDebitumColors provides palette) {
        ApplySystemBars(darkIcons = !dark)
        MaterialTheme(
            colorScheme = palette.materialColors(dark),
            typography = typography,
            content = content,
        )
    }
}

private fun DebitumPalette.materialColors(dark: Boolean) = if (dark) {
    darkColorScheme(
        primary = accent,
        onPrimary = Color.White,
        background = background,
        onBackground = textDark,
        surface = sheetBackground,
        onSurface = textDark,
        surfaceVariant = surface,
        onSurfaceVariant = textSecondary,
        surfaceContainer = sheetBackground,
        surfaceContainerHigh = surface,
        surfaceContainerLow = background,
        outline = fieldBorder,
        error = overdue,
    )
} else {
    lightColorScheme(
        primary = accent,
        onPrimary = Color.White,
        background = background,
        onBackground = textDark,
        surface = background,
        onSurface = textDark,
        surfaceVariant = surface,
        onSurfaceVariant = textSecondary,
        surfaceContainer = sheetBackground,
        surfaceContainerHigh = sheetBackground,
        outline = fieldBorder,
        error = overdue,
    )
}

private fun TextStyle.withFamily(family: FontFamily) = copy(fontFamily = family)
