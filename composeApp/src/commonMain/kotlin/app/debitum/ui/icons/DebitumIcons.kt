package app.debitum.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

object DebitumIcons {
    val Plus: ImageVector by lazy {
        strokeIcon("Plus", strokeWidth = 2.5f, "M12 5v14", "M5 12h14")
    }

    val ShieldCheck: ImageVector by lazy {
        strokeIcon(
            "ShieldCheck",
            strokeWidth = 1.8f,
            "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z",
            "m9 12 2 2 4-4",
        )
    }

    val Close: ImageVector by lazy {
        strokeIcon("Close", strokeWidth = 2.2f, "M18 6L6 18", "M6 6l12 12")
    }

    val Calendar: ImageVector by lazy {
        strokeIcon(
            "Calendar",
            strokeWidth = 2f,
            "M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
            "M16 2v4",
            "M8 2v4",
            "M3 10h18",
        )
    }

    val Repeat: ImageVector by lazy {
        strokeIcon(
            "Repeat",
            strokeWidth = 2.2f,
            "M23 4v6h-6",
            "M1 20v-6h6",
            "M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15",
        )
    }

    val Check: ImageVector by lazy {
        strokeIcon("Check", strokeWidth = 2.4f, "M20 6L9 17l-5-5")
    }

    val Trash: ImageVector by lazy {
        strokeIcon(
            "Trash",
            strokeWidth = 2f,
            "M3 6h18",
            "M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2",
            "M10 11v6",
            "M14 11v6",
        )
    }
}

private fun strokeIcon(name: String, strokeWidth: Float, vararg paths: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        paths.forEach { data ->
            addPath(
                pathData = addPathNodes(data),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = strokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
