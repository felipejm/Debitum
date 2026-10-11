package app.debitum.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.debitum.resources.Res
import app.debitum.resources.app_name
import app.debitum.ui.icons.DebitumLogo
import app.debitum.ui.theme.DebitumColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val SplashHoldMs = 1_150L

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    SplashSystemBars(restoreDarkIcons = !DebitumColors.isDark)

    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.88f) }
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffset = remember { Animatable(14f) }

    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f, tween(520, easing = FastOutSlowInEasing)) }
        launch {
            logoScale.animateTo(1f, spring(dampingRatio = 0.78f, stiffness = 220f))
        }
        delay(200)
        launch { wordmarkAlpha.animateTo(1f, tween(420, easing = FastOutSlowInEasing)) }
        launch { wordmarkOffset.animateTo(0f, tween(420, easing = FastOutSlowInEasing)) }
        delay(SplashHoldMs)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DebitumColors.Card),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DebitumLogo(
                modifier = Modifier
                    .width(92.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    },
            )
            Spacer(Modifier.height(22.dp))
            Text(
                text = stringResource(Res.string.app_name),
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
                modifier = Modifier.graphicsLayer {
                    alpha = wordmarkAlpha.value
                    translationY = wordmarkOffset.value
                },
            )
        }
    }
}
