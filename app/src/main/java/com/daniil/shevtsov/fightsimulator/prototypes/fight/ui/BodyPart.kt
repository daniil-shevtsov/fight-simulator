package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.bodyPartId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.item
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.BodyPartItem
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.bodyPartItem

@Preview
@Composable
fun BodyPartPreview() {
    Row() {
        BodyPart(
            bodyPartItem = bodyPartItem(id = 0L, name = "Head", contained = setOf(bodyPartId(1L))),
            onClick = {},
            contained = listOf(
                bodyPartItem(id = 1L, name = "Skull")
            ),
            modifier = Modifier.width(100.dp)
        )
        BodyPart(
            bodyPartItem = bodyPartItem(id = 2L, name = "Hand"),
            onClick = {},
            contained = emptyList(),
            modifier = Modifier.width(100.dp)
        )
        BodyPart(
            bodyPartItem = bodyPartItem(
                id = 3L, name = "Hand",
                holding = item(id = 4L, name = "Knife")
            ),
            onClick = {},
            contained = emptyList(),
            modifier = Modifier.width(100.dp)
        )
        BodyPart(
            bodyPartItem = bodyPartItem(
                id = 5L,
                name = "Hand",
                contained = setOf(bodyPartId(6L)),
                holding = item(id = 7L, name="Dagger"),
            ),
            contained = listOf(bodyPartItem(id = 6L, name = "Bone")),
            onClick = {},
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
fun BodyPart(
    bodyPartItem: BodyPartItem,
    onClick: () -> Unit,
    contained: List<BodyPartItem>,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        bodyPartItem.statuses.contains(BodyPartStatus.Broken) -> Color(
            0xFF876CEF
        )
        else -> Color.Gray
    }
    val textColor = when {
        bodyPartItem.statuses.contains(BodyPartStatus.Missing) -> Color(0x80FFFFFF)
        bodyPartItem.statuses.contains(BodyPartStatus.Broken) -> Color.White
        else -> Color.Black
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .let { modifier ->
                when (bodyPartItem.isSelected) {
                    true -> modifier
                        .background(Color.White)
                        .padding(6.dp)
                    false
                    -> modifier
                }
            }
            .background(backgroundColor, shape = when {
                bodyPartItem.statuses.contains(BodyPartStatus.Broken) -> GenericShape { size, direction ->
                    val fractureWidth = 10f
                    val fractureOffset = 10f
                    val fractureAngle = 45f

                    val fractureX = size.width / 2
                    val leftPartEnd = fractureX - fractureWidth
                    val rightPartStart = fractureX + fractureWidth
                    lineTo(leftPartEnd + fractureOffset, 0f)
                    lineTo(leftPartEnd - fractureOffset, size.height)
                    lineTo(0f, size.height)
                    lineTo(0f, 0f)

                    moveTo(rightPartStart + fractureOffset, 0f)

                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(rightPartStart - fractureOffset, size.height)
                    lineTo(rightPartStart + fractureOffset, 0f)

                    close()
                }
                else -> RectangleShape
            })
    ) {
        if (bodyPartItem.statuses.contains(BodyPartStatus.Bleeding)) {
            Surface(color = Color(0xAAA80202)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )
            }
        }
        if (bodyPartItem.statuses.contains(BodyPartStatus.Missing)) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xAAA80202))
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(0.1f)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xAB252222))
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .weight(0.9f)
                )
            }
        }

        Column(modifier = Modifier) {
            if (bodyPartItem.statuses.contains(BodyPartStatus.Broken)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .clickable { onClick() }
                    .padding(4.dp)
                    .fillMaxWidth()) {
                    val left: String
                    val right: String
                    when {
                        bodyPartItem.name.contains(" ") -> {
                            left = bodyPartItem.name.substringBefore(" ")
                            right = bodyPartItem.name.substringAfter(" ")
                        }
                        else -> {
                            left = bodyPartItem.name.substring(0, bodyPartItem.name.length / 2)
                            right = bodyPartItem.name.substring(
                                bodyPartItem.name.length / 2,
                                bodyPartItem.name.length
                            )
                        }
                    }
                    Text(
                        text = left,
                        color = textColor,
                        modifier = Modifier
                    )
                    Text(
                        text = right,
                        color = textColor,
                        modifier = Modifier
                    )
                }
            } else {
                Text(
                    text = bodyPartItem.name,
                    color = textColor,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(4.dp)
                        .fillMaxWidth()
                )
            }

            if (bodyPartItem.holding != null) {
                Item(
                    item = bodyPartItem.holding,
                    textColor = textColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (bodyPartItem.contained.isNotEmpty()) {
                val bone = contained.first()
                BodyPart(
                    bodyPartItem = bone,
                    onClick = { },
                    contained = emptyList(),
                    modifier = Modifier
                        .padding(4.dp)
                        .background(Color.LightGray)
                        .padding(2.dp)
                        .background(Color.Gray)
                        .padding(4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
