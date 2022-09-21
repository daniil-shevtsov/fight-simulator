package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.SelectableItem
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.bodyPartItem
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.selectableItem

//@Preview
//@Composable
//fun BodyPartPreview() {
//    Column(verticalArrangement = spacedBy(8.dp)) {
//        val skull = bodyPartItem(id = 1L, name = "Skull")
//        BodyPart(
//            bodyPartItem = bodyPartItem(id = 0L, name = "Head", contained = setOf(skull)),
//            onClick = {},
//            modifier = Modifier
//        )
//        BodyPart(
//            bodyPartItem = bodyPartItem(id = 2L, name = "Hand", canGrab = true),
//            onClick = {},
//            modifier = Modifier
//        )
//        BodyPart(
//            bodyPartItem = bodyPartItem(
//                id = 3L, name = "Hand", canGrab = true,
//                holding = selectableItem(id = 4L, name = "Knife")
//            ),
//            onClick = {},
//            modifier = Modifier
//        )
//        val bone = bodyPartItem(id = 6L, name = "Bone")
//        BodyPart(
//            bodyPartItem = bodyPartItem(
//                id = 5L,
//                name = "Hand",
//                canGrab = true,
//                contained = setOf(bone),
//                holding = selectableItem(id = 7L, name = "Dagger"),
//            ),
//            onClick = {},
//            modifier = Modifier
//        )
//        BodyPart(
//            bodyPartItem = bodyPartItem(
//                id = 0L,
//                name = "Head",
//                contained = setOf(skull),
//                lodgedIn = listOf(selectableItem(id = 2L, name = "Arrow"))
//            ),
//            onClick = {},
//            modifier = Modifier
//        )
//    }
//}

private fun generateBodyParts() = (0..5).toList()
    .map {
        bodyPartItem(
            id = it.toLong(),
            name = "Part$it",
            holding = when {
                it % 2 == 0 -> selectableItem(id = it + 100L, name = "item$it")
                else -> null
            },
            contained = setOf(bodyPartItem(id = it + 200L, name = "bone$it")),
            lodgedIn = when {
                it % 3 == 0 -> listOf(selectableItem(id = it + 300L, name = "arrow$it"))
                else -> emptyList()
            }
        )
    }

@Preview
@Composable
fun BodyPartColumnPreview() {
    Column(verticalArrangement = spacedBy(8.dp), modifier = Modifier.width(IntrinsicSize.Max)) {
        generateBodyParts().forEach { part ->
            BodyPart(
                bodyPartItem = part,
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ })
        }
    }
}

@Preview(widthDp = 600)
@Composable
fun BodyPartRowPreview() {
    Row(horizontalArrangement = spacedBy(8.dp)) {
        generateBodyParts().forEach { part ->
            BodyPart(
                bodyPartItem = part,
                modifier = Modifier.weight(1f),//.width(90.dp),
                onClick = { /*TODO*/ })
        }
    }
}

@Composable
fun BodyPart(
    bodyPartItem: SelectableItem.BodyPartItem,
    onClick: () -> Unit,
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

    Column(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
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
            })) {
        if (bodyPartItem.statuses.contains(BodyPartStatus.Broken)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .clickable { onClick() }
                .padding(4.dp)
//                .fillMaxWidth()
            ) {
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

        if (bodyPartItem.contained.isNotEmpty()) {
            val bone = bodyPartItem.contained.first()
            if (bone is SelectableItem.BodyPartItem) {
                BodyPart(
                    bodyPartItem = bone,
                    onClick = { },
                    modifier = Modifier
                        .padding(4.dp)
                        .background(Color.LightGray)
                        .padding(2.dp)
                        .background(Color.Gray)
                        .padding(4.dp)
//                        .fillMaxWidth()
                )
            }
        }

        bodyPartItem.lodgedIn.forEach { lodgedIn ->
            SelectableItem(
                item = lodgedIn,
                modifier = Modifier
                    .padding(4.dp)
                    .background(Color(0xAAA80202))
                    .padding(2.dp)
                    .background(Color.DarkGray)
                    .fillMaxWidth()
            )
        }

        if (bodyPartItem.canGrab) {
            if (bodyPartItem.holding != null) {
                SelectableItem(
                    item = bodyPartItem.holding,
                    modifier = Modifier
                        .padding(4.dp)
                        .background(Color.DarkGray)
                        .fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(Color.DarkGray)
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}
