package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Actor
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.GroundId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.*

fun composePreviewCreatureStub() = listOf(
    creatureMenu(
        actor = Actor.Player,
        isControlled = true,
        bodyParts = defaultBodyParts().map { bodyPart ->
            when (bodyPart.name) {
                "Right Hand" -> bodyPart.copy(holding = selectableItem(name = "Knife"))
                else -> bodyPart
            }
        }
    ),

    creatureMenu(
        actor = Actor.Enemy,
        isTarget = true,
        bodyParts = defaultBodyParts().map { bodyPart ->
            when (bodyPart.name) {
                "Head" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Missing))
                "Body" -> bodyPart.copy(
                    lodgedIn = listOf(selectableItem(id = 604L, name = "Arrow"))
                )
                "Skull" -> bodyPart.copy(
                    statuses = listOf(
                        BodyPartStatus.Broken,
                        BodyPartStatus.Missing
                    )
                )
                "Right Arm" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Broken))
                "Left Leg" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Bleeding))
                "Right Hand" -> bodyPart.copy(holding = selectableItem(name = "Mace"))
                "Left Hand" -> bodyPart.copy(statuses = listOf(BodyPartStatus.Missing))
                else -> bodyPart
            }
        }
    ),
)

@Preview(widthDp = 412, heightDp = 732)
@Composable
fun FightScreenPreview() {
    FightScreen(
        state = FightViewState.Content(
            actors = composePreviewCreatureStub(),
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
            ground = GroundMenu(
                id = GroundId(0L),
                selectables = listOf(
                    selectableItem(name = "Spear"),
                    selectableItem(name = "Helmet"),
                    bodyPartItem(
                        id = 1L,
                        name = "Head",
                        contained = setOf(bodyPartItem(id = 2L, name = "Skull")),
                        lodgedIn = listOf(selectableItem(id = 3L, name = "Arrow")),
                        isSelected = true
                    )
                ),
                isSelected = true
            ),
        ),
        onAction = {},
    )
}
@Preview(widthDp = 412, heightDp = 732)
@Composable
fun FightScreenDebugPreview() {
    FightScreen(
        state = FightViewState.Content(
            actors = composePreviewCreatureStub(),
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
            ground = GroundMenu(
                id = GroundId(0L),
                selectables = listOf(
                    bodyPartItem(
                        id = 1L,
                        name = "Head",
                        contained = setOf(bodyPartItem(id = 2L, name = "Skull")),
                        isSelected = false
                    ),
                    bodyPartItem(
                        id = 2L,
                        name = "Right Hand",
                        contained = setOf(bodyPartItem(id = 2L, name = "Bone")),
                        holding = selectableItem(id = 3L, name = "Knife"),
                        isSelected = false
                    ),
                ),
                isSelected = true
            ),
        ),
        onAction = {},
    )
}

//@Preview(widthDp = 412, heightDp = 732)
//@Composable
//fun InitialFightScreenPreview() {
//    FightScreen(
//        state = fightPresentationMapping(
//            state = fightFunctionalCore(
//                fightState(),
//                FightAction.Init
//            )
//        ),
//        onAction = {},
//    )
//}

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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    GroundMenu(
                        ground = state.ground,
                        onAction = onAction,
                        modifier = Modifier
                    )
                    CommandsMenu(
                        menu = state.commandsMenu,
                        onClick = { onAction(FightAction.SelectCommand(it.attackAction)) },
                        modifier = Modifier,
                    )
                    ActionLog(
                        actionLog = state.actionLog,
                        modifier = Modifier.height(IntrinsicSize.Min)
                    )
                }
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
            .defaultMinSize(minHeight = 50.dp)
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
fun CommandsMenu(
    menu: CommandsMenu,
    modifier: Modifier = Modifier,
    onClick: (item: CommandItem) -> Unit
) {
    Pane(
        items = menu.commands,
        modifier = modifier
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

private fun defaultBodyParts(): List<SelectableItem.BodyPartItem> {
    val skull = bodyPartItem(id = 1L, name = "Skull")
    return listOf(
        bodyPartItem(id = 0L, name = "Head", contained = setOf(skull)),
        skull,
        bodyPartItem(id = 2L, name = "Body"),
        bodyPartItem(id = 3L, name = "Right Arm"),
        bodyPartItem(id = 4L, name = "Right Hand", isSelected = true, canGrab = true),
        bodyPartItem(id = 5L, name = "Left Arm"),
        bodyPartItem(id = 6L, name = "Left Hand", canGrab = true),
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
