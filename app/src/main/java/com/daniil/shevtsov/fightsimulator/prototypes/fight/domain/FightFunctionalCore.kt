package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

fun fightFunctionalCore(
    state: FightState,
    action: FightAction,
): FightState {
    return when (action) {
        FightAction.Init -> createInitialState()
        is FightAction.SelectBodyPart -> selectBodyPart(state, action)
        is FightAction.SelectCommand -> selectCommand(state, action)
        is FightAction.SelectActor -> selectActor(state, action)
    }
}

fun selectActor(state: FightState, action: FightAction.SelectActor): FightState {
    return state.copy(controlledActorId = action.actorId)
}

fun selectCommand(state: FightState, action: FightAction.SelectCommand): FightState {
    val item = state.controlledCreature.bodyParts
        .find { it.name == state.controlledBodyPart.name }?.holding

    val newControlledCreature = when (action.attackAction) {
        AttackAction.Throw -> {
            val thrownItem = state.controlledBodyPart.holding
            state.controlledCreature.copy(
                bodyParts = state.controlledCreature.bodyParts.map { bodyPart ->
                    when (bodyPart.holding) {
                        thrownItem -> bodyPart.copy(holding = null)
                        else -> bodyPart
                    }
                }
            )
        }
        else -> state.controlledCreature
    }

    val newTargetCreature = when (action.attackAction) {
        AttackAction.Throw -> {
            val thrownItem = state.controlledBodyPart.holding
            state.targetCreature.copy(bodyParts = state.targetCreature.bodyParts.map { bodyPart ->
                when (bodyPart) {
                    state.targetBodyPart -> bodyPart.copy(holding = thrownItem)
                    else -> bodyPart
                }
            })
        }
        AttackAction.Pommel -> state.targetCreature.copy(
            brokenPartsSet = state.targetCreature.brokenPartsSet + (state.targetBodyPartBone?.name
                ?: state.targetBodyPart.name)
        )
        AttackAction.Slash -> {
            state.targetCreature.copy(
                missingPartsSet = state.targetCreature.missingPartsSet.plus(
                    state.targetBodyPart.name
                )
            )
        }
        else -> state.targetCreature
    }

    val actionName = when (action.attackAction) {
        AttackAction.Strike -> "strikes"
        AttackAction.Slash -> "slashes"
        AttackAction.Stab -> "stabs"
        AttackAction.Pommel -> "pommels"
        AttackAction.Punch -> "punches"
        AttackAction.Kick -> "kicks"
        AttackAction.Throw -> "throws"
    }

    val controlledName = state.controlledCreature.name
    val itemName = item?.name?.toLowerCase()
    val targetName = state.targetCreature.name
    val targetPartName = state.targetBodyPart.name.toLowerCase()
    val controlledPartName = state.controlledBodyPart.name.toLowerCase()
    val controlledAttackSource = when {
        item != null -> "$itemName held by their $controlledPartName"
        else -> "their $controlledPartName"
    }
    val newEntry = when (action.attackAction) {
        AttackAction.Throw -> {
            "$controlledName throws $itemName at $targetName's $targetPartName with their $controlledPartName.\n" +
                    "The $itemName has lodged firmly in the wound!"
        }
        AttackAction.Slash -> {
            "$controlledName $actionName at $targetName's $targetPartName with $controlledAttackSource.\nSevered $targetPartName flies off in an arc!"
        }
        AttackAction.Pommel -> {
            val generalMessage =
                "$controlledName $actionName $targetName's $targetPartName with $controlledAttackSource."
            when {
                state.targetBodyPart.containedBodyParts.isNotEmpty() -> newTargetCreature.bodyParts.find { it.name == state.targetBodyPart.containedBodyParts.first() }!!.name.toLowerCase()
                    .let { containedBodyPartName ->
                        "$generalMessage The $containedBodyPartName is fractured!"
                    }
                else -> generalMessage
            }
        }
        else -> "$controlledName $actionName $targetName's $targetPartName with $controlledAttackSource"
    }

    return state.copy(
        actionLog = state.actionLog + listOf(actionEntry(text = newEntry)),
        actors = state.actors.map { actor ->
            when (actor.id) {
                state.controlledCreature.id -> newControlledCreature
                state.targetCreature.id -> newTargetCreature
                else -> actor
            }
        }
    )
}

fun selectBodyPart(state: FightState, action: FightAction.SelectBodyPart): FightState {
    val creatureToSelect = state.actors.find { it.id == action.creatureId }
    val newSelections =
        if (creatureToSelect != null && action.partName in creatureToSelect.functionalParts
                .map(BodyPart::name)
        ) {
            state.selections.toMutableMap()
                .apply {
                    put(action.creatureId, BodyPartId(action.partName))
                }
        } else {
            state.selections
        }


    return state.copy(
        selections = newSelections,
    )
}

private fun createInitialState(): FightState {
    val player = Creature(
        id = "Player",
        actor = Actor.Player,
        name = "Player",
        bodyParts = createDefaultBodyParts().map { bodyPart ->
            when (bodyPart.name) {
                "Right Hand" -> bodyPart.copy(
                    holding = item(
                        name = "Knife",
                        attackActions = listOf(
                            AttackAction.Slash,
                            AttackAction.Stab,
                            AttackAction.Pommel
                        )
                    )
                )
                else -> bodyPart
            }
        }
    )
    val enemy = Creature(
        id = "Enemy",
        name = "Enemy",
        actor = Actor.Enemy,
        bodyParts = createDefaultBodyParts(),
    )
    return FightState(
        controlledActorId = player.id,
        selections = mapOf(
            player.id to BodyPartId("Left Hand"),
            enemy.id to BodyPartId("Head")
        ),
        actors = listOf(player, enemy),
        actionLog = emptyList(),
    )
}

private fun createDefaultBodyParts() = listOf(
    bodyPart(
        id = 0L,
        name = "Head",
        containedBodyParts = setOf("Skull")
    ),
    bodyPart(
        id = 1L,
        name = "Skull"
    ),
    bodyPart(
        id = 2L,
        name = "Body"
    ),
    bodyPart(
        id = 3L,
        name = "Right Arm"
    ),
    bodyPart(
        id = 4L,
        name = "Right Hand",
        attackActions = listOf(AttackAction.Punch)
    ),
    bodyPart(
        id = 5L,
        name = "Left Arm"
    ),
    bodyPart(
        id = 6L,
        name = "Left Hand",
        attackActions = listOf(AttackAction.Punch)
    ),
    bodyPart(
        id = 7L,
        name = "Right Leg"
    ),
    bodyPart(
        id = 8L,
        name = "Right Foot",
        attackActions = listOf(AttackAction.Kick)
    ),
    bodyPart(
        id = 9L,
        name = "Left Leg"
    ),
    bodyPart(
        id = 10L,
        name = "Left Foot",
        attackActions = listOf(AttackAction.Kick)
    ),
)
