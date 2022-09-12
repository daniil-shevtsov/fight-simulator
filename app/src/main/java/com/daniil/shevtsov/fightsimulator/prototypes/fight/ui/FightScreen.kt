package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.*
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.*

@Preview(widthDp = 400, heightDp = 600)
@Composable
fun FightScreenPreview() {
    FightScreen(
        state = FightViewState.Content(
            actors = listOf(
                creatureMenu(
                    actor = Actor.Player,
                    isControlled = true,
                    bodyParts = defaultBodyParts().map { bodyPart ->
                        when (bodyPart.name) {
                            "Right Hand" -> bodyPart.copy(holding = item(name = "Knife"))
                            else -> bodyPart
                        }
                    }
                ),

                creatureMenu(
                    actor = Actor.Enemy,
                    bodyParts = defaultBodyParts().map { bodyPart ->
                        when (bodyPart.name) {
                            "Head" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Missing))
                            "Skull" -> bodyPart.copy(
                                statuses = listOf(
                                    BodyPartStatus.Broken,
                                    BodyPartStatus.Missing
                                )
                            )
                            "Right Arm" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Broken))
                            "Left Leg" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Bleeding))
                            "Right Hand" -> bodyPart.copy(holding = item(name = "Mace"))
                            "Left Hand" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Missing))
                            else -> bodyPart
                        }
                    }
                ),
            ),
            commandsMenu = commandsMenu(
                commands = listOf(
                    commandItem(name = "Slash"),
                    commandItem(name = "Stab"),
                    commandItem(name = "Pummel"),
                    commandItem(name = "Throw"),
                    commandItem(name = "Kick"),
                    commandItem(name = "Punch"),
                )
            ),
            actionLog = listOf(
                actionEntryModel("You slap enemy's head with your right hand"),
                actionEntryModel("You done did it"),
            ),
            ground = GroundMenu(items = listOf(item(name = "Spear"), item(name = "Helmet"))),
        ),
        onAction = {},
    )
}

@Composable
fun FightScreen(state: FightViewState, onAction: (FightAction) -> Unit) {
    when (state) {
        is FightViewState.Loading -> Unit
        is FightViewState.Content -> {
            Column(
                verticalArrangement = spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(8.dp)
            ) {
                ActorsMenu(
                    actors = state.actors,
                    modifier = Modifier.weight(1f),
                    onAction = onAction
                )
                GroundMenu(
                    menu = state.ground,
                )
                CommandsMenu(
                    menu = state.commandsMenu,
                    onClick = { onAction(FightAction.SelectCommand(it.attackAction)) })
                ActionLog(
                    actionLog = state.actionLog
                )
            }
        }
    }
}

@Composable
private fun ActionLog(
    actionLog: List<ActionEntryModel>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        verticalArrangement = spacedBy(8.dp),
        state = listState,
        reverseLayout = true,
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.Gray)
            .padding(8.dp)
            .background(Color.White)
            .padding(8.dp)
    ) {
        items(actionLog) { actionEntry ->
            Text(
                text = "* ${actionEntry.text}",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
    LaunchedEffect(actionLog) {
        if (actionLog.isNotEmpty()) {
            listState.animateScrollToItem(index = actionLog.size - 1)
        }
    }

}

@Composable
private fun ActorsMenu(
    actors: List<CreatureMenu>,
    modifier: Modifier = Modifier,
    onAction: (FightAction) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
    ) {
        actors.forEach { creature ->
            Creature(
                creature,
                onClick = {
                    onAction(
                        FightAction.SelectBodyPart(
                            creatureId = creature.id,
                            partId = it.id,
                        )
                    )
                }, onControlClick = { onAction(FightAction.SelectControlledActor(actorId = creature.id)) })
        }
    }
}

@Composable
private fun Creature(
    creature: CreatureMenu,
    modifier: Modifier = Modifier,
    onClick: (bodyPart: BodyPartItem) -> Unit,
    onControlClick: () -> Unit,
) {
    Column(
        verticalArrangement = spacedBy(8.dp),
        modifier = modifier
            .background(
                when (creature.isControlled) {
                    true -> Color.White
                    false -> Color(0x40000000).compositeOver(Color.White)
                }
            )
            .clickable { onControlClick() }
            .padding(8.dp)
    ) {
        Text(
            text = when (creature.isControlled) {
                true -> "Controlled"
                false -> "Click to Control"
            }, modifier = Modifier
        )

        Column(
            verticalArrangement = spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .let { modifier ->
                    when (creature.isControlled) {
                        true -> modifier
                        false -> modifier
                    }
                }
                .background(Color.LightGray)
                .width(120.dp)
                .padding(6.dp)
        ) {
            creature.bodyParts
                .filter { bodyPart ->
                    creature.bodyParts.none { otherBodyPart ->
                        bodyPart.id in otherBodyPart.contained
                    }
                }
                .forEach { bodyPartItem ->
                    BodyPart(
                        bodyPartItem = bodyPartItem,
                        onClick = { onClick(bodyPartItem) },
                        contained = creature.bodyParts.filter { it.id in bodyPartItem.contained })

                }
        }
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
//                        .background(Color.Black)
//                        .padding(4.dp)
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

@Composable
fun Item(
    item: Item,
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier,
) {
    Text(
        text = item.name,
        color = textColor,
        modifier = modifier
            .padding(4.dp)
            .background(Color.LightGray)
            .padding(2.dp)
            .background(Color.Gray)
            .padding(4.dp)
    )
}

@Composable
fun GroundMenu(
    menu: GroundMenu,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .background(Color.DarkGray)
        ) {
            Text(
                text = "Ground", modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )
            Row(
                modifier = Modifier
            ) {
                menu.items.forEach { item ->
                    Item(item = item)
                }
            }
        }

    }
}


@Composable
fun CommandsMenu(menu: CommandsMenu, onClick: (item: CommandItem) -> Unit) {
    Pane(
        items = menu.commands,
        modifier = Modifier
            .background(Color.LightGray)
            .padding(8.dp)
    ) { command, modifier ->
        Text(
            text = command.name,
            textAlign = TextAlign.Center,
            modifier = modifier
                .background(Color.Gray)
                .clickable { onClick(command) }
                .padding(4.dp)
                .fillMaxWidth()
        )
    }
}

private fun defaultBodyParts(): List<BodyPartItem> {
    val skull = bodyPartItem(id = 1L, name = "Skull")
    return listOf(
        bodyPartItem(id = 0L, name = "Head", contained = setOf(skull.id)),
        skull,
        bodyPartItem(id = 2L, name = "Body"),
        bodyPartItem(id = 3L, name = "Right Arm"),
        bodyPartItem(id = 4L, name = "Right Hand", isSelected = true),
        bodyPartItem(id = 5L, name = "Left Arm"),
        bodyPartItem(id = 6L, name = "Left Hand"),
        bodyPartItem(id = 7L, name = "Right Leg"),
        bodyPartItem(id = 8L, name = "Right Foot"),
        bodyPartItem(id = 9L, name = "Left Leg"),
        bodyPartItem(id = 10L, name = "Left Foot"),
    )
}

@Composable
fun <T> Pane(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: T, modifier: Modifier) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        (items.indices step 2)
            .map { index -> items[index] to items.getOrNull(index + 1) }
            .forEach { (startAction, endAction) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    itemContent(startAction, Modifier.let { modifier ->
                        if (endAction == null) {
                            modifier.fillMaxWidth()
                        } else {
                            modifier
                        }
                            .weight(1f)
                            .fillMaxHeight()
                    })
                    if (endAction != null) {
                        itemContent(
                            endAction, Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
    }
}
