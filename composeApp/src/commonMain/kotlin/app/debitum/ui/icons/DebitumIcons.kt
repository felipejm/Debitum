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

    val Settings: ImageVector by lazy {
        strokeIcon(
            "Settings",
            strokeWidth = 2f,
            "M12 15a3 3 0 1 0 0-6 3 3 0 1 0 0 6",
            "M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z",
        )
    }

    val ChevronLeft: ImageVector by lazy {
        strokeIcon("ChevronLeft", strokeWidth = 2.2f, "M15 18l-6-6 6-6")
    }

    val Moon: ImageVector by lazy {
        strokeIcon("Moon", strokeWidth = 2f, "M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z")
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
