package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class BodyPart(
    val id: BodyPartId,
    val name: String,
    val holding: Item? = null,
    val containedBodyParts: Set<BodyPartId> = setOf(),
    val attackActions: List<AttackAction>,
)

fun bodyPart(
    id: Long,
    name: String = "",
    holding: Item? = null,
    attackActions: List<AttackAction> = emptyList(),
    containedBodyParts: Set<BodyPartId> = setOf(),
) = BodyPart(
    id = BodyPartId(id),
    name = name,
    holding = holding,
    attackActions = attackActions,
    containedBodyParts = containedBodyParts,
)
