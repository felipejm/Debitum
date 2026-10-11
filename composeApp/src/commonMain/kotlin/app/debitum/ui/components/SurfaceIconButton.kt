package app.debitum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.debitum.ui.theme.DebitumColors

@Composable
fun SurfaceIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    showDot: Boolean = false,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(modifier = Modifier.size(44.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(
                    elevation = if (DebitumColors.isDark) 0.dp else 8.dp,
                    shape = shape,
                    ambientColor = DebitumColors.Card.copy(alpha = 0.08f),
                    spotColor = DebitumColors.Card.copy(alpha = 0.12f),
                )
                .clip(shape)
                .background(DebitumColors.GearBackground)
                .border(1.dp, DebitumColors.GearBorder, shape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = DebitumColors.GearIcon,
                modifier = Modifier.size(20.dp),
            )
        }
        if (showDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(12.dp)
                    .background(DebitumColors.GearBackground, CircleShape)
                    .padding(2.dp)
                    .background(DebitumColors.Accent, CircleShape),
            )
        }
    }
}
