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
            brokenPartsSet = state.targetCreature.brokenPartsSet + (state.targetBodyPartBone?.id
                ?: state.targetBodyPart.id)
        )
        AttackAction.Slash -> {
            state.targetCreature.copy(
                missingPartsSet = state.targetCreature.missingPartsSet.plus(
                    state.targetBodyPart.id
                ) + state.targetBodyPart.containedBodyParts
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
                state.targetBodyPart.containedBodyParts.isNotEmpty() -> newTargetCreature.bodyParts.find { it.id == state.targetBodyPart.containedBodyParts.first() }!!.name.toLowerCase()
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
        if (creatureToSelect != null && action.partId in creatureToSelect.functionalParts
                .map(BodyPart::id)
        ) {
            state.selections.toMutableMap()
                .apply {
                    put(action.creatureId, action.partId)
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
            player.id to createDefaultBodyParts().find { it.name == "Left Hand" }?.id!!,
            enemy.id to createDefaultBodyParts().find { it.name == "Head" }?.id!!
        ),
        actors = listOf(player, enemy),
        actionLog = emptyList(),
        world = World(ground = ground())
    )
}

private fun createDefaultBodyParts(): List<BodyPart> {
    val initialMainParts = listOf(
        bodyPart(
            name = "Head",
        ),
        bodyPart(
            name = "Body"
        ),
        bodyPart(
            name = "Right Arm"
        ),
        bodyPart(
            name = "Right Hand",
            attackActions = listOf(AttackAction.Punch)
        ),
        bodyPart(
            name = "Left Arm"
        ),
        bodyPart(
            name = "Left Hand",
            attackActions = listOf(AttackAction.Punch)
        ),
        bodyPart(
            name = "Right Leg"
        ),
        bodyPart(
            name = "Right Foot",
            attackActions = listOf(AttackAction.Kick)
        ),
        bodyPart(
            name = "Left Leg"
        ),
        bodyPart(
            name = "Left Foot",
            attackActions = listOf(AttackAction.Kick)
        ),
    ).mapIndexed { index, bodyPart -> bodyPart.copy(id = BodyPartId(index.toLong())) }

    val bones = initialMainParts.map { mainPart ->
        val boneName = when (mainPart.name) {
            "Head" -> "Skull"
            "Body" -> "Ribs"
            else -> "Bone"
        }
        bodyPart(
            id = mainPart.id.raw + initialMainParts.size,
            name = boneName,
            parentId = mainPart.id,
        )
    }

    val mainParts = initialMainParts.map { mainPart ->
        val mainPartBone = bones.find { it.parentId == mainPart.id }

        when (mainPartBone) {
            null -> mainPart
            else -> mainPart.copy(containedBodyParts = mainPart.containedBodyParts + mainPartBone.id)
        }
    }


    val allParts = mainParts + bones

    return allParts
}
