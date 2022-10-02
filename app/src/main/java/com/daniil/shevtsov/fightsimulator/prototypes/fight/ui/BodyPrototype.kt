package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private enum class JointOrientation {
    Horizontal,
    Vertical
}

@Preview(heightDp = 800, widthDp = 200)
@Composable
fun ConnectedBodyPrototype() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val headHeight = 50.dp
        val headWidth = 50.dp
        val upperArmLength = headHeight * 1.5f
        val lowerArmLength = headHeight
        val handWidth = headWidth / 2f
        val handLength = headHeight * 0.75f
        val armWidth = 25.dp
        PrototypeBodyPart(
            name = "Head",
            modifier = Modifier
                .size(height = headHeight, width = headWidth)
        )
        PrototypeJoint(orientation = JointOrientation.Horizontal)
        Row() {
            Column {
                PrototypeBodyPart(
                    name = "Right Upper Arm",
                    modifier = Modifier
                        .width(armWidth)
                        .height(upperArmLength)
                )
                PrototypeJoint(orientation = JointOrientation.Horizontal)
                PrototypeBodyPart(
                    name = "Right Lower Arm",
                    modifier = Modifier
                        .width(armWidth)
                        .height(lowerArmLength)
                )
                PrototypeJoint(orientation = JointOrientation.Horizontal)
                PrototypeBodyPart(
                    name = "Left Hand",
                    modifier = Modifier
                        .width(handWidth)
                        .height(handLength)
                )
            }
            PrototypeJoint(orientation = JointOrientation.Vertical)

            PrototypeBodyPart(
                name = "Body",
                modifier = Modifier
                    .width(75.dp)
                    .height(100.dp)
            )
            PrototypeJoint(orientation = JointOrientation.Vertical)
            Column {
                PrototypeBodyPart(
                    name = "Left Upper Arm",
                    modifier = Modifier
                        .width(armWidth)
                        .height(upperArmLength)
                )
                PrototypeJoint(orientation = JointOrientation.Horizontal)
                PrototypeBodyPart(
                    name = "Left Lower Arm",
                    modifier = Modifier
                        .width(armWidth)
                        .height(lowerArmLength)
                )
                PrototypeJoint(orientation = JointOrientation.Horizontal)
                PrototypeBodyPart(
                    name = "Left Hand",
                    modifier = Modifier
                        .width(handWidth)
                        .height(handLength)
                )
            }

        }
    }
}

@Composable
private fun PrototypeJoint(
    modifier: Modifier = Modifier,
    orientation: JointOrientation = JointOrientation.Horizontal,
) {
    val jointWidth: Dp
    val jointHeight: Dp

    when(orientation) {
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
