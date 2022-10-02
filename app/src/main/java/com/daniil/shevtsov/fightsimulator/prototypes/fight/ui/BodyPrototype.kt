package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        BodyPart(id = 0L, name = "Head", parentId = 1L),
        BodyPart(id = 1L, name = "Upper Body", childId = 0L)
    )
    Row {
        Column {
            prototypeBody.forEach {
                PrototypeSimpleBodyPart(
                    part = it,
                    modifier = Modifier.height(50.dp)
                )
            }
        }
        CustomBodyLayout(prototypeBody) {
            prototypeBody.forEach {
                PrototypeSimpleBodyPart(
                    part = it,
                    modifier = Modifier.height(50.dp)
                )
            }
        }
    }
}
//@Preview(heightDp = 800, widthDp = 200)
//@Composable
//fun ConnectedBodyPrototype() {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        val headSize = Size(width = 50.dp, height = 50.dp)
//        val upperArmSize = Size(width = 25.dp, height = headSize.height * 1.5f)
//        val lowerArmSize = Size(width = 25.dp, height = headSize.height)
//        val handSize = Size(width = headSize.width / 2f, height = headSize.height * 0.5f)
//        val upperBodySize = Size(width = headSize.width * 2f, height = headSize.height)
//        val lowerBodySize = Size(width = headSize.width * 1.5f, height = headSize.height)
//        val groinSize = Size(width = headSize.width, height = headSize.height)
//        val upperLegSize = Size(width = 25.dp, height = headSize.height * 1.5f)
//        val lowerLegSize = Size(width = 25.dp, height = headSize.height)
//        val feetSize = Size(width = 50.dp, height = 25.dp)
//        PrototypeBodyPart(
//            name = "Head",
//            modifier = Modifier
//                .size(height = headSize.height, width = headSize.width)
//        )
//        PrototypeJoint(orientation = JointOrientation.Horizontal)
//        Row() {
//            Column {
//                PrototypeBodyPart(
//                    name = "Right Upper Arm",
//                    modifier = Modifier.size(upperArmSize)
//                )
//                PrototypeJoint(orientation = JointOrientation.Horizontal)
//                PrototypeBodyPart(
//                    name = "Right Lower Arm",
//                    modifier = Modifier.size(lowerArmSize)
//                )
//                PrototypeJoint(orientation = JointOrientation.Horizontal)
//                PrototypeBodyPart(
//                    name = "Left Hand",
//                    modifier = Modifier.size(handSize)
//                )
//            }
//            PrototypeJoint(orientation = JointOrientation.Vertical)
//
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    PrototypeBodyPart(
//                        name = "Upper Body",
//                        modifier = Modifier.size(upperBodySize)
//                    )
//                    PrototypeJoint(orientation = JointOrientation.Horizontal)
//                    PrototypeBodyPart(
//                        name = "Lower Body",
//                        modifier = Modifier.size(lowerBodySize)
//                    )
//                }
//                PrototypeJoint(orientation = JointOrientation.Horizontal)
//                Row {
////                    Column {
////                        PrototypeBodyPart(
////                            name = "Right Upper Leg",
////                            modifier = Modifier.size(upperLegSize)
////                        )
////                        PrototypeJoint(orientation = JointOrientation.Horizontal)
////                        PrototypeBodyPart(
////                            name = "Right Lower Leg",
////                            modifier = Modifier.size(lowerLegSize)
////                        )
////                        PrototypeJoint(orientation = JointOrientation.Horizontal)
////                        PrototypeBodyPart(
////                            name = "Right Foot",
////                            modifier = Modifier.size(feetSize)
////                        )
////                    }
////                    PrototypeJoint(orientation = JointOrientation.Vertical)
//                    PrototypeBodyPart(
//                        name = "Groin",
//                        modifier = Modifier.size(groinSize)
//                    )
////                    PrototypeJoint(orientation = JointOrientation.Vertical)
////                    Column {
////                        PrototypeBodyPart(
////                            name = "Left Upper Leg",
////                            modifier = Modifier.size(upperLegSize)
////                        )
////                        PrototypeJoint(orientation = JointOrientation.Horizontal)
////                        PrototypeBodyPart(
////                            name = "Left Lower Leg",
////                            modifier = Modifier.size(lowerLegSize)
////                        )
////                        PrototypeJoint(orientation = JointOrientation.Horizontal)
////                        PrototypeBodyPart(
////                            name = "Left Foot",
////                            modifier = Modifier.size(feetSize)
////                        )
////                    }
//                }
//
//            }
//
//            PrototypeJoint(orientation = JointOrientation.Vertical)
//            Column {
//                PrototypeBodyPart(
//                    name = "Left Upper Arm",
//                    modifier = Modifier.size(upperArmSize)
//                )
//                PrototypeJoint(orientation = JointOrientation.Horizontal)
//                PrototypeBodyPart(
//                    name = "Left Lower Arm",
//                    modifier = Modifier.size(lowerArmSize)
//                )
//                PrototypeJoint(orientation = JointOrientation.Horizontal)
//                PrototypeBodyPart(
//                    name = "Left Hand",
//                    modifier = Modifier.size(handSize)
//                )
//            }
//
//        }
//    }
//}


data class BodyPart(
    val id: Long,
    val name: String,
    val parentId: Long? = null,
    val childId: Long? = null,
)

data class Joint(
    val id: Long,
    val start: Long,
    val end: Long,
)

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
private fun PrototypeJoint(
    modifier: Modifier = Modifier,
    orientation: JointOrientation = JointOrientation.Horizontal,
) {
    val jointWidth: Dp
    val jointHeight: Dp

    when (orientation) {
        JointOrientation.Horizontal -> {
            jointWidth = 25.dp
            jointHeight = 10.dp
        }
        JointOrientation.Vertical -> {
            jointWidth = 10.dp
            jointHeight = 25.dp
        }
    }

    Box(
        modifier = modifier
            .background(Color.Cyan)
            .size(width = jointWidth, height = jointHeight)
    ) {

    }
}

@Composable
fun CustomBodyLayout(
    prototypeBody: List<BodyPart>,
    content: @Composable () -> Unit
) = Layout(content) { measurables, constraints ->
    val mainAxisSpacing: Dp = 5.dp
    val crossAxisSpacing: Dp = 5.dp
    // 1. The measuring phase.
    val placeables = measurables.map { measurable ->
        measurable.measure(constraints)
    }

    // 2. The sizing phase.
    layout(constraints.maxWidth, constraints.maxHeight) {
        // 3. The placement phase.
        var yPosition = 0

        placeables.forEachIndexed { index, placeable ->
//            if (placeable.width < (xPosition + mainAxisSpacing.roundToPx())) {
//                xPosition -= (placeable.width + mainAxisSpacing.roundToPx())
//            } else {
//                yPosition += (placeable.height + crossAxisSpacing.roundToPx())
//                xPosition = constraints.maxWidth - placeable.width - mainAxisSpacing.roundToPx()
//            }
            placeable.placeRelative(x = 0, y = yPosition)
            yPosition += placeable.height
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
