package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.SelectableId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.CreatureMenu
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.SelectableItem

@Preview
@Composable
fun CreaturePreview() {
    ActorsMenu(actors = composePreviewCreatureStub(), onAction = {})
}

@Composable
fun ActorsMenu(
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
                        FightAction.SelectSomething(
                            selectableHolderId = creature.id,
                            selectableId = it,
                        )
                    )
                },
                onControlClick = { onAction(FightAction.SelectControlledActor(actorId = creature.id)) })
        }
    }
}

@Composable
fun Creature(
    creature: CreatureMenu,
    modifier: Modifier = Modifier,
    onClick: (id: SelectableId) -> Unit,
    onControlClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Absolute.spacedBy(8.dp),
        modifier = modifier
            .background(
                when (creature.isControlled) {
                    true -> Color.White
                    false -> Color(0x40000000).compositeOver(Color.White)
                }
            )
            .let { modifier ->
                when (creature.isTarget) {
                    true -> modifier
                        .padding(2.dp)
                        .background(Color.Black)
                        .padding(2.dp)
                        .background(Color.LightGray)
                    false -> modifier
                }
            }
            .padding(4.dp)
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
            verticalArrangement = Arrangement.Absolute.spacedBy(8.dp),
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
                .filterIsInstance<SelectableItem.BodyPartItem>()
                .filter { bodyPart ->
                    creature.bodyParts.filterIsInstance<SelectableItem.BodyPartItem>()
                        .none { otherBodyPart ->
                            bodyPart.id in otherBodyPart.contained.map(SelectableItem::id)
                        }
                }
                .forEach { bodyPartItem ->
                    SelectableItem(
                        item = bodyPartItem,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClick(it) }
                    )
                }
        }
    }
}
