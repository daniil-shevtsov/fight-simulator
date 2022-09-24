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
    }
}

fun selectActor(state: FightState, action: FightAction.SelectControlledActor): FightState {
    val oldControlled = state.controlledCreature
    val oldTarget = state.targetCreature
    val newControlledId = when (action.actorId) {
        oldControlled.id -> oldControlled.id
        else -> oldTarget.id
    }
    val newTargetId = when (action.actorId) {
        oldControlled.id -> oldTarget.id
        else -> oldControlled.id
    }
    val newControlled = state.actors.find { it.id == newControlledId }!!

    val newTarget = state.actors.find { it.id == newTargetId }!!
    return state.copy(
        lastSelectedControlledPartId = newControlled.bodyPartIds.first(),
        lastSelectedTargetHolderId = newTarget.id,
        lastSelectedTargetPartId = state.controlledBodyPart.id,
    )
}

fun selectCommand(state: FightState, action: FightAction.SelectCommand): FightState {
    val attackerWeaponId = state.allBodyParts
        .find { bodyPart -> bodyPart.id == state.controlledBodyPart.id }
        ?.holding
    val attackerWeapon = state.allSelectables[attackerWeaponId]
    state.targetBodyPart ?: state.targetSelectable ?: return state
    val targetBodyPart = state.targetBodyPart


    val targetWeaponId = targetBodyPart?.holding
    val targetWeapon = state.allSelectables[targetWeaponId]

    val shouldKnockOutWeapon = action.attackAction in setOf(
        AttackAction.Punch,
        AttackAction.Kick
    ) && targetBodyPart?.holding != null

    val shouldGrabItem =
        action.attackAction == AttackAction.Grab && state.controlledBodyPart.holding == null

    val newSlashedParts: List<BodyPart> = state.allBodyParts
        .filter { bodyPart -> bodyPart.id in state.targetCreature.bodyPartIds && bodyPart.id == state.targetBodyPart?.id }
        .takeIf { action.attackAction == AttackAction.Slash }
        .orEmpty()
        .map { bodyPart: BodyPart ->
            bodyPart.copy(
                statuses = bodyPart.statuses + BodyPartStatus.Missing
            )
        }


    val newSelectables = state.allSelectables.values.map { selectable ->
        when (action.attackAction) {
            AttackAction.Throw -> {
                when {
                    selectable is BodyPart && state.controlledBodyPart.id == selectable.id -> selectable.copy(
                        holding = null
                    )
                    selectable is BodyPart && state.targetBodyPart?.id == selectable.id -> selectable.copy(
                        holding = state.controlledBodyPart.holding,
                        lodgedInSelectables = selectable.lodgedInSelectables + setOfNotNull(state.controlledBodyPart.holding)
                    )
                    else -> selectable
                }
            }
            AttackAction.Grab -> {
                val itemFromTheGround = state.targetSelectable
                val targetSelectableId = state.targetSelectable?.id
                when {
                    selectable is BodyPart && state.controlledBodyPart.id == selectable.id && itemFromTheGround != null -> selectable.copy(
                        holding = itemFromTheGround.id
                    )
                    selectable is BodyPart && targetSelectableId != null && selectable.lodgedInSelectables.contains(targetSelectableId) -> selectable.copy(
                        lodgedInSelectables = selectable.lodgedInSelectables - targetSelectableId
                    )
                    else -> selectable
                }
            }
            AttackAction.Pommel -> {
                val brokenPart = state.targetBodyPartBone ?: targetBodyPart
                when {
                    selectable is BodyPart && selectable.id == brokenPart?.id -> {
                        selectable.copy(
                            statuses = selectable.statuses + BodyPartStatus.Broken
                        )
                    }
                    else -> selectable
                }
            }
            AttackAction.Punch, AttackAction.Kick -> {
                when {
                    selectable is BodyPart && selectable.id == targetBodyPart?.id && shouldKnockOutWeapon -> {
                        selectable.copy(
                            holding = null
                        )
                    }
                    else -> selectable
                }
            }
            AttackAction.Slash -> {
                when {
                    else -> selectable
                }
            }
            else -> selectable
        }
    }

    val newControlledCreature = state.controlledCreature
    val newTargetCreature = state.targetCreature.copy(
        missingPartsSet = state.targetCreature.missingPartsSet + newSlashedParts.map { it.id }
            .toSet(),
        bodyPartIds = state.targetCreature.bodyPartIds.filter {
            val notSlashedPart = it !in newSlashedParts.map(BodyPart::id)
            val notContainedInSlashedPart =
                it !in newSlashedParts.flatMap(BodyPart::containedBodyParts)

            notSlashedPart && notContainedInSlashedPart
        },
    )

    val actionName = when (action.attackAction) {
        AttackAction.Strike -> "strikes"
        AttackAction.Slash -> "slashes"
        AttackAction.Stab -> "stabs"
        AttackAction.Pommel -> "pommels"
        AttackAction.Punch -> "punches"
        AttackAction.Kick -> "kicks"
        AttackAction.Throw -> "throws"
        AttackAction.Grab -> "grabs"
        AttackAction.Headbutt -> "headbutts"
    }

    val controlledName = state.controlledCreature.name
    val itemName = attackerWeapon?.name?.toLowerCase()
    val targetName = state.targetCreature.name
    val targetPartName = targetBodyPart?.name?.toLowerCase().orEmpty()
    val controlledPartName = state.controlledBodyPart.name.toLowerCase()
    val containedPartName =
        newSelectables.find { it.id in targetBodyPart?.containedBodyParts.orEmpty() }?.name?.toLowerCase()
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
                !targetBodyPart?.containedBodyParts.isNullOrEmpty() ->
                    containedPartName.let { containedBodyPartName ->
                        "$generalMessage The $containedBodyPartName is fractured!"
                    }
                else -> generalMessage
            }
        }
        AttackAction.Grab -> {
            "$controlledName picks up the ${state.targetSelectable?.name?.toLowerCase()} from the ground."
        }
        else -> when {
            shouldKnockOutWeapon -> "$controlledName $actionName $targetName's $targetPartName with $controlledAttackSource. $targetName's ${targetWeapon?.name?.toLowerCase()} is knocked out to the ground!"
            else -> "$controlledName $actionName $targetName's $targetPartName with $controlledAttackSource"
        }

    }


    val newGround = when {
        shouldKnockOutWeapon -> state.ground.copy(
            selectableIds = state.ground.selectableIds + targetWeapon!!.id
        )
        newSlashedParts.isNotEmpty() -> state.ground.copy(
            selectableIds = state.ground.selectableIds + newSlashedParts.map(Selectable::id)
        )
        action.attackAction == AttackAction.Grab
        -> state.ground.copy(
            selectableIds = state.ground.selectableIds.filter { it != state.targetSelectable?.id }
        )
        else -> state.ground
    }

    return state.copy(
        selectableHolders = state.selectableHolders.mapValues { (holderId, holder) ->
            when(holderId) {
                state.controlledCreature.id -> newControlledCreature
                state.targetCreature.id -> newTargetCreature
                state.ground.id -> newGround
                else -> holder
            }
        },
        allSelectables = newSelectables.associateBy { it.id },
        actionLog = state.actionLog + listOf(actionEntry(text = newEntry))
    )
}

fun selectBodyPart(state: FightState, action: FightAction.SelectSomething): FightState {
    val targetIsLodgedInItem = state.controlledCreatureBodyParts
        .flatMap { it.lodgedInSelectables.toList() }
        .contains(action.selectableId)
    return state.copy(
        lastSelectedControlledPartId = when {
            targetIsLodgedInItem -> state.lastSelectedControlledPartId
            action.selectableHolderId == state.controlledCreature.id -> action.selectableId
            else -> state.lastSelectedControlledPartId
        },
        lastSelectedTargetPartId = when {
            targetIsLodgedInItem -> action.selectableId
            action.selectableHolderId != state.controlledCreature.id -> action.selectableId
            else -> state.lastSelectedTargetPartId
        },
        lastSelectedTargetHolderId = when  {
            targetIsLodgedInItem -> action.selectableHolderId
            action.selectableHolderId == state.controlledCreature.id -> state.lastSelectedTargetHolderId
            else -> action.selectableHolderId
        },
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
    val playerKnife = knife.copy(id = itemId(100L))
    val enemyKnife = knife.copy(id = itemId(101L))
    val playerBodyParts = createDefaultBodyParts(idOffset = 0L).map { bodyPart ->
        when (bodyPart.name) {
            "Right Hand" -> bodyPart.copy(
                holding = playerKnife.id
            )
            else -> bodyPart
        }
    }
    val enemyBodyParts = createDefaultBodyParts(
        idOffset = playerBodyParts.size.toLong()
    ).map { bodyPart ->
        when (bodyPart.name) {
            "Right Hand" -> bodyPart.copy(
                holding = enemyKnife.id
            )
            else -> bodyPart
        }
    }

    val player = Creature(
        id = creatureId("Player".hashCode().toLong()),
        actor = Actor.Player,
        name = "Player",
        bodyPartIds = playerBodyParts.map(BodyPart::id),
    )
    val enemy = Creature(
        id = creatureId("Enemy".hashCode().toLong()),
        name = "Enemy",
        actor = Actor.Enemy,
        bodyPartIds = enemyBodyParts.map(BodyPart::id),
    )
    return FightState(
        lastSelectedControlledPartId = playerBodyParts.find { it.name == "Left Hand" }?.id!!,
        lastSelectedTargetHolderId = enemy.id,
        lastSelectedTargetPartId = enemyBodyParts.find { it.name == "Head" }?.id!!,
        selectableHolders = listOf(
            player,
            enemy,
            ground(),
        ).associateBy { it.id },
        allSelectables = (playerBodyParts + enemyBodyParts + listOf(
            playerKnife,
            enemyKnife
        )).associateBy { it.id },
        actionLog = emptyList(),
    )
}

private fun createDefaultBodyParts(idOffset: Long): List<BodyPart> {
    val initialMainParts = listOf(
        bodyPart(
            name = "Head",
            attackActions = listOf(AttackAction.Headbutt)
        ),
        bodyPart(
            name = "Body"
        ),
        bodyPart(
            name = "Right Arm"
        ),
        bodyPart(
            name = "Right Hand",
            attackActions = listOf(AttackAction.Punch),
            canGrab = true,
        ),
        bodyPart(
            name = "Left Arm"
        ),
        bodyPart(
            name = "Left Hand",
            attackActions = listOf(AttackAction.Punch),
            canGrab = true,
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
