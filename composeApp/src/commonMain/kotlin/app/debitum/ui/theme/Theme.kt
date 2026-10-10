package app.debitum.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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

object DebitumColors {
    val Background = Color(0xFFFFFFFF)
    val Card = Color(0xFF2C2036)
    val Accent = Color(0xFFDD785D)
    val AccentOnCard = Color(0xFFFFA084)
    val TextDark = Color(0xFF1E1926)
    val TextMuted = Color(0xFF9CA3AF)
    val TextSecondary = Color(0xFF6B7280)
    val Surface = Color(0xFFF7F6F9)
    val SheetBackground = Color(0xFFFAF9FC)
    val Handle = Color(0xFFD1D5DB)
    val CloseButton = Color(0xFFF3F4F6)
    val FieldBorder = Color(0xFFEEEBF2)
    val DangerSoft = Color(0xFFFFF1F2)
    val Paid = Color(0xFF10B981)
    val PaidStrong = Color(0xFF059669)
    val PaidSoft = Color(0xFFECFDF5)
    val PaidOnCard = Color(0xFF34D399)
    val Overdue = Color(0xFFEF4444)
    val OverdueStrong = Color(0xFFF43F5E)
    val OverdueOnCard = Color(0xFFFF8A8A)
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
fun DebitumTheme(content: @Composable () -> Unit) {
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
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = DebitumColors.Accent,
            onPrimary = Color.White,
            background = DebitumColors.Background,
            onBackground = DebitumColors.TextDark,
            surface = DebitumColors.Background,
            onSurface = DebitumColors.TextDark,
            surfaceVariant = DebitumColors.Surface,
            onSurfaceVariant = DebitumColors.TextSecondary,
            error = DebitumColors.Overdue,
        ),
        typography = typography,
        content = content,
    )
}

private fun TextStyle.withFamily(family: FontFamily) = copy(fontFamily = family)
