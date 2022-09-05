package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class BodyPart(
    val name: String,
    val holding: Item? = null,
    val containedBodyParts: Set<String> = setOf(),
    val attackActions: List<AttackAction>,
)

fun bodyPart(
    name: String = "",
    holding: Item? = null,
    attackActions: List<AttackAction> = emptyList(),
    containedBodyParts: Set<String> = setOf(),
) = BodyPart(
    name = name,
    holding = holding,
    attackActions = attackActions,
    containedBodyParts = containedBodyParts,
)
