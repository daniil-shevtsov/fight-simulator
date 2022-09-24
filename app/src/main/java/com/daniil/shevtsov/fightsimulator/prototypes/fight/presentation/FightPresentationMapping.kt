package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.*

fun fightPresentationMapping(state: FightState): FightViewState {
    return FightViewState.Content(
        actors = state.actors.map { creature ->
            CreatureMenu(
                id = creature.id,
                actor = creature.actor,
                bodyParts = state
                    .allBodyParts
                    .filter { it.id in creature.bodyPartIds }
                    .map { bodyPart ->
                        bodyPart.toItem(
                            state.allSelectables,
                            state.controlledBodyPart.id,
                            state.targetSelectable?.id
                        )
                    },
                isControlled = creature.id == state.controlledCreature.id,
                isTarget = creature.id == state.targetCreature.id,
            )
        },
        commandsMenu = CommandsMenu(
            commands = state.availableCommands.map {
                CommandItem(
                    attackAction = it.attackAction,
                    name = it.attackAction.name,
                )
            }
        ),
        actionLog = state.actionLog.map { action ->
            ActionEntryModel(
                text = action.text,
            )
        },
        ground = state.world.ground.let { ground ->
            GroundMenu(
                id = ground.id,
                selectables = state.allSelectables
                    .filterKeys { it in ground.selectableIds }
                    .values
                    .map {
                        it.toItem(
                            state.allSelectables,
                            state.controlledBodyPart.id,
                            state.targetSelectable?.id
                        )
                    },
                isSelected = state.targetSelectableHolder.id == ground.id,
            )
        }
    )
}

private fun Selectable.toItem(
    allSelectables: Map<SelectableId, Selectable>,
    controlledSelectableId: SelectableId,
    targetSelectableId: SelectableId?,
): SelectableItem {
    val toItem = { selectable: Selectable ->
        selectable.toItem(
            allSelectables,
            controlledSelectableId,
            targetSelectableId
        )
    }
    return when (this) {
        is Item -> SelectableItem.Item(
            id = id,
            name = name,
            isSelected = targetSelectableId == id
        )
        is BodyPart -> SelectableItem.BodyPartItem(
            id = id,
            name = name,
            holding = holding?.let { id ->
                allSelectables[id]?.let { toItem(it) }
            },
            contained = containedBodyParts
                .mapNotNull { containedId ->
                    allSelectables[containedId]?.let { toItem(it) }
                }.toSet(),
            isSelected = id == targetSelectableId || id == controlledSelectableId,
            statuses = statuses,
            canGrab = canGrab,
            lodgedIn = allSelectables.values.filter { it.id in lodgedInSelectables }
                .map { toItem(it) },
        )
    }
}
