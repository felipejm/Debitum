package app.debitum.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import app.debitum.ui.theme.DebitumColors
import kotlin.math.min

private const val LogoMinX = 206f
private const val LogoMinY = 261f
private const val LogoWidth = 411f
private const val LogoHeight = 309f

@Composable
fun DebitumLogo(
    modifier: Modifier = Modifier,
    markColor: Color = Color.White,
    checkColor: Color = DebitumColors.Accent,
) {
    val dPath = remember { debitumDPath() }
    val checkPath = remember { debitumCheckPath() }
    val cutoutPath = remember { debitumCutoutPath() }
    Canvas(modifier.aspectRatio(LogoWidth / LogoHeight)) {
        val scale = min(size.width / LogoWidth, size.height / LogoHeight)
        val dx = (size.width - LogoWidth * scale) / 2f
        val dy = (size.height - LogoHeight * scale) / 2f
        translate(dx, dy) {
            scale(scale, scale, pivot = Offset.Zero) {
                translate(-LogoMinX, -LogoMinY) {
                    clipPath(cutoutPath, ClipOp.Difference) {
                        drawPath(dPath, markColor)
                    }
                    drawPath(checkPath, checkColor)
                }
            }
        }
    }
}

private fun debitumDPath() = Path().apply {
    fillType = PathFillType.EvenOdd
    moveTo(233f, 277f)
    lineTo(370f, 277f)
    arcTo(Rect(229f, 277f, 511f, 570f), -90f, 180f, false)
    lineTo(233f, 570f)
    close()
    moveTo(294f, 325f)
    lineTo(350f, 325f)
    arcTo(Rect(245f, 325f, 455f, 515f), -90f, 180f, false)
    lineTo(294f, 515f)
    close()
}

private fun debitumCheckPath() = Path().apply {
    moveTo(244f, 394f)
    lineTo(317f, 394f)
    lineTo(353f, 430f)
    lineTo(506f, 277f)
    lineTo(579f, 277f)
    lineTo(353f, 503f)
    close()
}

private fun debitumCutoutPath() = Path().apply {
    moveTo(206f, 378f)
    lineTo(343f, 515f)
    lineTo(363f, 515f)
    lineTo(617f, 261f)
    lineTo(500f, 261f)
    lineTo(353f, 408f)
    lineTo(323f, 378f)
    close()
}
