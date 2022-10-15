package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private enum class JointOrientation {
    Horizontal,
    Vertical
}

private class Size(
    val width: Dp,
    val height: Dp,
)

private fun Modifier.size(size: Size) = size(
    width = size.width,
    height = size.height,
)

@Preview(heightDp = 800, widthDp = 200)
@Composable
fun CustomLayoutPreview() {
    val prototypeBody = listOf(
        BodyPart(id = 0L, name = "Head", parentId = 1L, type = BodyPartType.Head),
        BodyPart(id = 1L, name = "Body", childId = 0L, type = BodyPartType.Body),
        BodyPart(id = 2L, name = "Left Arm", parentId = 1L, type = BodyPartType.Arm),
        BodyPart(id = 3L, name = "Right Arm", parentId = 1L, type = BodyPartType.Arm),
    )
    Row {
        CustomBodyLayout(prototypeBody) {
            prototypeBody.forEach {
                PrototypeSimpleBodyPart(
                    part = it,
                    modifier = Modifier
                        .height(50.dp)
                        .layoutId(it)
                )
            }
        }
    }
}

data class BodyPart(
    val id: Long,
    val name: String,
    val parentId: Long? = null,
    val childId: Long? = null,
    val type: BodyPartType,
)

enum class BodyPartType {
    Head,
    Body,
    Arm,
    Leg
}

@Composable
fun PrototypeSimpleBodyPart(
    part: BodyPart,
    modifier: Modifier = Modifier,
) {
    Text(
        text = part.name,
        modifier = modifier
            .size(50.dp)
            .background(Color.Cyan)
            .layoutId(part)
    )
}

@Composable
fun CustomBodyLayout(
    prototypeBody: List<BodyPart>,
    content: @Composable () -> Unit
) = Layout(content) { measurables, constraints ->
    val unmeasured = measurables.map { measurable ->
        measurable to measurable.layoutId as BodyPart
    }.toMap()
    val numberOfArmRows =
        unmeasured.count { (_, bodyPart) -> bodyPart.type == BodyPartType.Arm } / 2

    val measuredPlaceables = unmeasured.map { (measurable, bodyPart) ->
        val measured = when (bodyPart.type) {
            BodyPartType.Body -> {
                measurable.measure(
                    constraints.copy(
                        minHeight = constraints.minHeight * numberOfArmRows,
                        maxHeight = constraints.maxHeight * numberOfArmRows,
                    )
                )
            }
            else -> {
                measurable.measure(constraints)
            }
        }

        bodyPart to measured
    }.toMap()

    // 2. The sizing phase.
    layout(constraints.maxWidth, constraints.maxHeight) {
        // 3. The placement phase.
        var lastBodyPosition = Offset(0f, 0f)
        val nonLimbPlaceablesWithPositions = measuredPlaceables
            .filter { (bodyPart, _) -> bodyPart.type != BodyPartType.Arm && bodyPart.type != BodyPartType.Leg }
            .map { (bodyPart, placeable) ->
                val position = when (bodyPart.type) {
                    BodyPartType.Head -> Offset(
                        x = constraints.maxWidth.toFloat() / 2 - placeable.width.toFloat() / 2,
                        y = 0f
                    )
                    else -> {
                        Offset(
                            x = constraints.maxWidth.toFloat() / 2 - placeable.width.toFloat() / 2,
                            y = lastBodyPosition.y + placeable.height
                        )
                    }
                }
                placeable to position
            }

        val limbPlaceablesWithPositions = measuredPlaceables
            .filter { (bodyPart, _) -> bodyPart.type == BodyPartType.Arm && bodyPart.type == BodyPartType.Leg }
            .map { (bodyPart, placeable) ->
                val position = when (bodyPart.type) {
                    else -> {
                        val bodyPosition =
                            nonLimbPlaceablesWithPositions.find { (placeable, position) ->
                                val (bodyPart, bodyPlaceable) = measuredPlaceables.entries.find { (bodyPart, placeable) -> bodyPart.type == BodyPartType.Body }
                                    ?.toPair()!!
                                placeable == bodyPlaceable
                            }?.second
                        if (bodyPosition != null) {
                            Offset(
                                x = bodyPosition.x - placeable.width,
                                y = bodyPosition.y
                            )
                        } else {
                            Offset(0f, 0f)
                        }
                    }
                }
                placeable to position
            }

        val placeablesWithPositions = nonLimbPlaceablesWithPositions + limbPlaceablesWithPositions

        placeablesWithPositions.forEach { (placeable, position) ->
            placeable.place(x = position.x.toInt(), y = position.y.toInt())
        }
    }
}

@Composable
fun PrototypeBodyPart(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(Color.LightGray)
    )
}
