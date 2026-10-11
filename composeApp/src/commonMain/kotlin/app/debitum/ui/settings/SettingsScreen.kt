package app.debitum.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.debitum.resources.Res
import app.debitum.resources.settings_appearance
import app.debitum.resources.settings_back
import app.debitum.resources.settings_dark_mode
import app.debitum.resources.settings_dark_mode_hint
import app.debitum.resources.settings_title
import app.debitum.ui.components.SurfaceIconButton
import app.debitum.ui.icons.DebitumIcons
import app.debitum.ui.theme.DebitumColors
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    SettingsContent(
        darkMode = darkMode,
        onBack = onBack,
        onDarkModeChange = viewModel::setDarkMode,
    )
}

@Composable
fun SettingsContent(
    darkMode: Boolean,
    onBack: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(containerColor = DebitumColors.Background) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = insets.calculateTopPadding() + 16.dp,
                    bottom = insets.calculateBottomPadding() + 24.dp,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SurfaceIconButton(
                    icon = DebitumIcons.ChevronLeft,
                    contentDescription = stringResource(Res.string.settings_back),
                    onClick = onBack,
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = stringResource(Res.string.settings_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    color = DebitumColors.TextDark,
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = stringResource(Res.string.settings_appearance),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DebitumColors.TextMuted,
            )
            Spacer(Modifier.height(12.dp))
            DarkModeRow(checked = darkMode, onCheckedChange = onDarkModeChange)
        }
    }
}

@Composable
private fun DarkModeRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DebitumColors.Surface)
            .border(1.dp, DebitumColors.CardBorder, RoundedCornerShape(24.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(DebitumColors.Accent.copy(alpha = 0.14f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = DebitumIcons.Moon,
                contentDescription = null,
                tint = DebitumColors.Accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.settings_dark_mode),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DebitumColors.TextDark,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(Res.string.settings_dark_mode_hint),
                fontSize = 12.sp,
                color = DebitumColors.TextMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DebitumColors.Accent,
                checkedThumbColor = Color.White,
                checkedBorderColor = DebitumColors.Accent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = DebitumColors.Handle,
                uncheckedBorderColor = DebitumColors.Handle,
            ),
        )
    }
}
