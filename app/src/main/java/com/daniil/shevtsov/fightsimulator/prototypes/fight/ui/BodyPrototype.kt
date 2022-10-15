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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
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

data class BodyMeasurable(
    val bodyPart: BodyPart,
    val measurable: Measurable
)

data class BodyPlaceable(
    val bodyPart: BodyPart,
    val placeable: Placeable,
)

data class BodyPositionedPlaceable(
    val bodyPart: BodyPart,
    val placeable: Placeable,
    val position: Offset,
)

@Composable
fun CustomBodyLayout(
    prototypeBody: List<BodyPart>,
    content: @Composable () -> Unit
) = Layout(content) { measurables, constraints ->
    val unmeasured: Map<Long, BodyMeasurable> = measurables.map { measurable ->
        val bodyPart = measurable.layoutId as BodyPart
        bodyPart.id to BodyMeasurable(
            bodyPart = bodyPart,
            measurable = measurable,
        )
    }.toMap()

    val numberOfArmRows = unmeasured.count { (_, bodyMeasurable) ->
        bodyMeasurable.bodyPart.type == BodyPartType.Arm
    } / 2
    val armIndices = unmeasured.filter { (_, bodyMeasurable) ->
        bodyMeasurable.bodyPart.type == BodyPartType.Arm
    }.toList().mapIndexed { index, (_, bodyMeasurable) ->
        bodyMeasurable.bodyPart.id to index
    }.toMap()

    val measuredPlaceables: Map<Long, BodyPlaceable> =
        unmeasured.map { (bodyPartId, bodyMeasurable) ->
            val placeable = when (bodyMeasurable.bodyPart.type) {
                BodyPartType.Body -> {
                    bodyMeasurable.measurable.measure(
                        constraints.copy(
                            minHeight = constraints.minHeight * numberOfArmRows,
                            maxHeight = constraints.maxHeight * numberOfArmRows,
                        )
                    )
                }
                else -> {
                    bodyMeasurable.measurable.measure(constraints)
                }
            }

            bodyPartId to BodyPlaceable(
                bodyPart = bodyMeasurable.bodyPart,
                placeable = placeable,
            )
        }.toMap()

    // 2. The sizing phase.
    layout(constraints.maxWidth, constraints.maxHeight) {
        // 3. The placement phase.
        val nonLimbPlaceablesWithPositions: Map<Long, BodyPositionedPlaceable> = measuredPlaceables
            .filter { (_, bodyPlaceable) ->
                bodyPlaceable.bodyPart.type != BodyPartType.Arm && bodyPlaceable.bodyPart.type != BodyPartType.Leg
            }
            .map { (bodyPartId, bodyPlaceable) ->
                val position = when (bodyPlaceable.bodyPart.type) {
                    BodyPartType.Head -> Offset(
                        x = constraints.maxWidth.toFloat() / 2 - bodyPlaceable.placeable.width.toFloat() / 2,
                        y = 0f
                    )
                    else -> {
                        Offset(
                            x = constraints.maxWidth.toFloat() / 2 - bodyPlaceable.placeable.width.toFloat() / 2,
                            y = 0f + bodyPlaceable.placeable.height
                        )
                    }
                }
                bodyPartId to BodyPositionedPlaceable(
                    bodyPart = bodyPlaceable.bodyPart,
                    placeable = bodyPlaceable.placeable,
                    position = position,
                )
            }.toMap()

        val limbPlaceablesWithPositions: Map<Long, BodyPositionedPlaceable> = measuredPlaceables
            .filter { (_, bodyPlaceable) ->
                bodyPlaceable.bodyPart.type == BodyPartType.Arm || bodyPlaceable.bodyPart.type == BodyPartType.Leg
            }.map { (bodyPartId, bodyPlaceable) ->
                val armIndex = armIndices[bodyPlaceable.bodyPart.id] ?: 0
                val isLeftSide = armIndex % 2 == 0
                val row = armIndex / 2
                val position = when (bodyPlaceable.bodyPart.type) {
                    else -> {
                        val (bodyPartId, bodyPositionedPlaceable) = nonLimbPlaceablesWithPositions.entries.find { (bodyPartId, bodyPositionedPlaceable) ->
                            bodyPositionedPlaceable.bodyPart.type == BodyPartType.Body
                        }!!
                        val xOffset = when {
                            isLeftSide -> bodyPositionedPlaceable.placeable.width
                            else -> -bodyPlaceable.placeable.width
                        }
                        val yOffset = bodyPositionedPlaceable.placeable.height * row
                        Offset(
                            x = bodyPositionedPlaceable.position.x + xOffset,
                            y = bodyPositionedPlaceable.position.y + yOffset
                        )
                    }
                }
                bodyPartId to BodyPositionedPlaceable(
                    bodyPart = bodyPlaceable.bodyPart,
                    placeable = bodyPlaceable.placeable,
                    position = position,
                )
            }.toMap()

        val placeablesWithPositions = nonLimbPlaceablesWithPositions + limbPlaceablesWithPositions

        placeablesWithPositions.forEach { (bodyPartId, bodyPositionedPlaceable) ->
            bodyPositionedPlaceable.placeable.place(
                x = bodyPositionedPlaceable.position.x.toInt(),
                y = bodyPositionedPlaceable.position.y.toInt()
            )
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
