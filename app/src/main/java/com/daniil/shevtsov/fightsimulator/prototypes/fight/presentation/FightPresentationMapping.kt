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
                        bodyPart.toItem(state, creature)
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
                    .filter { it.id in ground.selectableIds }
                    .map { it.toItem(state.allSelectables, state.targetSelectable?.id) },
                isSelected = state.targetSelectableHolder.id == ground.id,
            )
        }
    )
}

private fun Selectable.toItem(
    allSelectables: List<Selectable>,
    targetSelectableId: SelectableId?,
): SelectableItem = when (this) {
    is Item -> SelectableItem.Item(
        id = id,
        name = name,
        isSelected = targetSelectableId == id
    )
    is BodyPart -> SelectableItem.BodyPartItem(
        id = id,
        name = name,
        holding = holding?.toItem(allSelectables, targetSelectableId),
        contained = containedBodyParts
            .mapNotNull { containedId ->
                allSelectables.find { it.id == containedId }?.toItem(allSelectables, targetSelectableId)
            }.toSet(),
        isSelected = id == targetSelectableId,
        statuses = statuses,
        canGrab = canGrab,
        lodgedIn = allSelectables.filter { it.id in lodgedInSelectables }
            .map { it.toItem(allSelectables, targetSelectableId) }
            .toSet(),
    )
}

private fun Selectable.toItem(
    state: FightState,
    creature: Creature,
): SelectableItem = when (this) {
    is Item -> SelectableItem.Item(
        id = id,
        name = name,
        isSelected = state.targetSelectable?.id == id
    )
    is BodyPart -> SelectableItem.BodyPartItem(
        id = id,
        name = name,
        holding = holding?.toItem(state = state, creature = creature),
        contained = containedBodyParts.mapNotNull { containedId ->
            state.selectables.find { it.id == containedId }?.toItem(state, creature)
        }.toSet(),
        isSelected = when (creature.id) {
            state.targetCreature.id -> state.targetBodyPart?.id == id
            state.controlledCreature.id -> state.controlledBodyPart.id == id
            else -> false
        },
        statuses = statuses + listOfNotNull(
//            BodyPartStatus.Missing.takeIf { id !in creature.bodyPartIds },
            BodyPartStatus.Missing.takeIf { id in creature.missingPartsSet },
        ),
        canGrab = canGrab,
        lodgedIn = state
            .selectables.filter { it.id in lodgedInSelectables }
            .map { it.toItem(state, creature) }
            .toSet(),
    )
    else -> throw Throwable("I think I have compiler version problem")
}

