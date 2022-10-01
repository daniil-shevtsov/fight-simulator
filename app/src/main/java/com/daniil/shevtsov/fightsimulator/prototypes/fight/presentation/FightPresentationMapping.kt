package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.*

fun fightPresentationMapping(state: FightState): FightViewState {
    val controlledCreatureId = state.controlledCreature.id
    val controlledBodyPartId = state.controlledBodyPart.id
    val targetSelectableId = state.targetSelectable?.id
    val targetSelectableHolderId = state.targetSelectableHolder.id
    return FightViewState.Content(
        actors = state.actors.values.map { creature ->
            CreatureMenu(
                id = creature.id,
                actor = creature.actor,
                bodyParts = state
                    .allBodyParts
                    .values
                    .filter { it.id in creature.bodyPartIds }
                    .map { bodyPart ->
                        bodyPart.toItem(
                            allSelectables = state.allSelectables,
                            controlledSelectableId = controlledBodyPartId,
                            targetSelectableId = targetSelectableId,
                        )
                    },
                isControlled = creature.id == controlledCreatureId,
                isTarget = creature.id == targetSelectableHolderId,
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
        ground = state.ground.let { ground ->
            GroundMenu(
                id = ground.id,
                selectables = state.allSelectables
                    .filterKeys { it in ground.selectableIds }
                    .values
                    .map {
                        it.toItem(
                            allSelectables = state.allSelectables,
                            controlledSelectableId = controlledBodyPartId,
                            targetSelectableId = targetSelectableId,
                        )
                    },
                isSelected = targetSelectableHolderId == ground.id,
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
