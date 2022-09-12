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
                    BodyPartItem(
                        id = bodyPart.id,
                        name = bodyPart.name,
                        holding = bodyPart.holding,
                        contained = bodyPart.containedBodyParts,
                        isSelected = when (creature.id) {
                            state.targetCreature.id -> state.targetBodyPart?.name == bodyPart.name
                            state.controlledCreature.id -> state.controlledBodyPart.name == bodyPart.name
                            else -> false
                        },
                        statuses = listOfNotNull(
                            BodyPartStatus.Missing.takeIf { bodyPart.id in creature.missingPartsSet },
                            BodyPartStatus.Broken.takeIf { bodyPart.id in creature.brokenPartsSet },
                        ),
                    )
                },
                isControlled = creature.id == state.controlledActorId,
                isTarget = creature.id == state.targetId,
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
                items = ground.items,
                isSelected = state.targetId == ground.id,
            )
        }
    )
}

