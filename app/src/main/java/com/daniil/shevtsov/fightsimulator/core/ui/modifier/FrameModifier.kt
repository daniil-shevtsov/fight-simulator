package com.daniil.shevtsov.fightsimulator.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/* See: https://proandroiddev.com/custom-modifiers-jetpack-compose-deep-dive-976d8ec8bf9e */
interface Decoration {
    fun drawDecoration(contentDrawScope: ContentDrawScope)
}

sealed class Frames : Decoration {
    class StrokeTriangleCorners(
        val color: Color = Color.White,
        private val length: Dp = 50.dp,
        private val strokeWidth: Dp = 1.dp
    ) : Frames() {
        override fun drawDecoration(contentDrawScope: ContentDrawScope) {
            with(contentDrawScope) {
                val lengthPx = length.toPx()
                val strokePx = strokeWidth.toPx()
                val strokeOffset = strokePx / 2

                drawRect(color = color, topLeft = Offset(0f, 0f), size = size)
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(lengthPx, lengthPx),
                    size = Size(size.width, size.height - lengthPx * 2),
                    blendMode = BlendMode.Clear
                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(-strokeOffset, strokeOffset),
//                    end = Offset(lengthPx, strokeOffset),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(0f, 0f),
//                    end = Offset(0f, lengthPx),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(contentDrawScope.size.width - lengthPx, 0f),
//                    end = Offset(contentDrawScope.size.width, 0f),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(contentDrawScope.size.width, 0f),
//                    end = Offset(contentDrawScope.size.width, lengthPx),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(0f, contentDrawScope.size.height - lengthPx),
//                    end = Offset(0f, contentDrawScope.size.height),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(0f, contentDrawScope.size.height),
//                    end = Offset(lengthPx, contentDrawScope.size.height),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(
//                        contentDrawScope.size.width - lengthPx,
//                        contentDrawScope.size.height
//                    ),
//                    end = Offset(contentDrawScope.size.width, contentDrawScope.size.height),
//                    strokeWidth = strokePx,
//                )
//                /*
//                 __
//                |
//                 */
//                contentDrawScope.drawLine(
//                    color = color,
//                    start = Offset(
//                        contentDrawScope.size.width,
//                        contentDrawScope.size.height - lengthPx
//                    ),
//                    end = Offset(contentDrawScope.size.width, contentDrawScope.size.height),
//                    strokeWidth = strokePx,
//                )
            }
        }

    }
}

fun Modifier.addDecoration(decoration: Decoration) = then(
    object : DrawModifier {
        override fun ContentDrawScope.draw() {
            drawContent()
            decoration.drawDecoration(this)
        }
    }
)
