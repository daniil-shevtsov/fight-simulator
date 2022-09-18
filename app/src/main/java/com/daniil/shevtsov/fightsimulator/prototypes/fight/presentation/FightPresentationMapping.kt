package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightState

fun fightPresentationMapping(state: FightState): FightViewState {
    return FightViewState.Content(
        actors = state.actors.map { creature ->
            CreatureMenu(
                id = creature.id,
                actor = creature.actor,
                bodyParts = creature.bodyParts.map { bodyPart ->
                    SelectableItem.BodyPartItem(
                        id = bodyPart.id,
                        name = bodyPart.name,
                        holding = bodyPart.holding,
                        contained = bodyPart.containedBodyParts,
                        isSelected = when (creature.id) {
                            state.targetCreature.id -> state.targetBodyPart?.id == bodyPart.id
                            state.controlledCreature.id -> state.controlledBodyPart.id == bodyPart.id
                            else -> false
                        },
                        statuses = listOfNotNull(
                            BodyPartStatus.Missing.takeIf { bodyPart.id in creature.missingPartsSet },
                            BodyPartStatus.Broken.takeIf { bodyPart.id in creature.brokenPartsSet },
                        ),
                        canGrab = bodyPart.canGrab,
                        lodgedIn = state
                            .selectables.filter { it.id in bodyPart.lodgedInSelectables }
                            .toSet(),
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
                selectables = ground.selectables,
                isSelected = state.targetSelectableHolder.id == ground.id,
            )
        }
    )
}

