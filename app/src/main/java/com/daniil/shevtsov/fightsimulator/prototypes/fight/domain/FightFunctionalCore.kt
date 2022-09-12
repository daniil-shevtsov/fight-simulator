package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

fun fightFunctionalCore(
    state: FightState,
    action: FightAction,
): FightState {
    return when (action) {
        FightAction.Init -> createInitialState()
        is FightAction.SelectSomething -> selectBodyPart(state, action)
        is FightAction.SelectCommand -> selectCommand(state, action)
        is FightAction.SelectControlledActor -> selectActor(state, action)
        is FightAction.SelectTarget -> selectTarget(state, action)
    }
}

fun selectTarget(state: FightState, action: FightAction.SelectTarget): FightState {
    return state.copy(
        targetId = action.id
    )
}

fun selectActor(state: FightState, action: FightAction.SelectControlledActor): FightState {
    return state.copy(controlledActorId = action.actorId)
}

fun selectCommand(state: FightState, action: FightAction.SelectCommand): FightState {
    val attackerWeapon = state.controlledCreature.bodyParts
        .find { it.name == state.controlledBodyPart.name }?.holding
    val targetBodyPart = state.targetBodyPart ?: return state


    val targetWeapon = targetBodyPart.holding

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

    val shouldKnockOutWeapon = action.attackAction in setOf(
        AttackAction.Punch,
        AttackAction.Kick
    ) && targetBodyPart.holding != null

    val newTargetCreature = when (action.attackAction) {
        AttackAction.Throw -> {
            val thrownItem = state.controlledBodyPart.holding
            state.targetCreature.copy(bodyParts = state.targetCreature.bodyParts.map { bodyPart ->
                when (bodyPart) {
                    targetBodyPart -> bodyPart.copy(holding = thrownItem)
                    else -> bodyPart
                }
            })
        }
        AttackAction.Pommel -> state.targetCreature.copy(
            brokenPartsSet = state.targetCreature.brokenPartsSet + setOfNotNull(
                state.targetBodyPartBone?.id
                    ?: targetBodyPart.id
            )
        )
        AttackAction.Punch, AttackAction.Kick -> {
            when {
                shouldKnockOutWeapon -> state.targetCreature.copy(
                    bodyParts = state.targetCreature.bodyParts.map { bodyPart ->
                        when (bodyPart.id) {
                            targetBodyPart.id -> bodyPart.copy(holding = null)
                            else -> bodyPart
                        }
                    }
                )
                else -> state.targetCreature
            }

        }
        AttackAction.Slash -> {
            state.targetCreature.copy(
                missingPartsSet = state.targetCreature.missingPartsSet.plus(
                    targetBodyPart.id
                ) + targetBodyPart.containedBodyParts
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
    val itemName = attackerWeapon?.name?.toLowerCase()
    val targetName = state.targetCreature.name
    val targetPartName = targetBodyPart.name.toLowerCase()
    val controlledPartName = state.controlledBodyPart.name.toLowerCase()
    val controlledAttackSource = when {
        attackerWeapon != null -> "$itemName held by their $controlledPartName"
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
                targetBodyPart.containedBodyParts.isNotEmpty() -> newTargetCreature.bodyParts.find { it.id == targetBodyPart.containedBodyParts.first() }!!.name.toLowerCase()
                    .let { containedBodyPartName ->
                        "$generalMessage The $containedBodyPartName is fractured!"
                    }
                else -> generalMessage
            }
        }
        else -> when {
            shouldKnockOutWeapon -> "$controlledName $actionName $targetName's $targetPartName with $controlledAttackSource. $targetName's ${targetWeapon?.name?.toLowerCase()} is knocked out to the ground!"
            else -> "$controlledName $actionName $targetName's $targetPartName with $controlledAttackSource"
        }

    }

    val newWorld = state.world.copy(
        ground = when {
            shouldKnockOutWeapon -> state.world.ground.copy(
                items = state.world.ground.items + targetBodyPart.holding!!
            )
            else -> state.world.ground
        }
    )

    return state.copy(
        actors = state.actors.map { actor ->
            when (actor.id) {
                state.controlledCreature.id -> newControlledCreature
                state.targetCreature.id -> newTargetCreature
                else -> actor
            }
        },
        world = newWorld,
        actionLog = state.actionLog + listOf(actionEntry(text = newEntry))
    )
}

fun selectBodyPart(state: FightState, action: FightAction.SelectSomething): FightState {
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
    val knife = item(
        name = "Knife",
        attackActions = listOf(
            AttackAction.Slash,
            AttackAction.Stab,
            AttackAction.Pommel
        )
    )
    val playerKnife = knife.copy(id = itemId(0L))
    val enemyKnife = knife.copy(id = itemId(1L))
    val playerBodyParts = createDefaultBodyParts(idOffset = 0L).map { bodyPart ->
        when (bodyPart.name) {
            "Right Hand" -> bodyPart.copy(
                holding = playerKnife
            )
            else -> bodyPart
        }
    }
    val enemyBodyParts = createDefaultBodyParts(
        idOffset = playerBodyParts.size.toLong()
    ).map { bodyPart ->
        when (bodyPart.name) {
            "Right Hand" -> bodyPart.copy(
                holding = enemyKnife
            )
            else -> bodyPart
        }
    }

    val player = Creature(
        id = creatureId("Player".hashCode().toLong()),
        actor = Actor.Player,
        name = "Player",
        bodyParts = playerBodyParts,
    )
    val enemy = Creature(
        id = creatureId("Enemy".hashCode().toLong()),
        name = "Enemy",
        actor = Actor.Enemy,
        bodyParts = enemyBodyParts,
    )
    return FightState(
        controlledActorId = player.id,
        targetId = creatureTargetId(enemy.id.raw),
        selections = mapOf(
            player.id to playerBodyParts.find { it.name == "Left Hand" }?.id!!,
            enemy.id to enemyBodyParts.find { it.name == "Head" }?.id!!
        ),
        actors = listOf(player, enemy),
        actionLog = emptyList(),
        world = World(ground = ground())
    )
}

private fun createDefaultBodyParts(idOffset: Long): List<BodyPart> {
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
    ).mapIndexed { index, bodyPart -> bodyPart.copy(id = bodyPartId(idOffset + index.toLong())) }

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
