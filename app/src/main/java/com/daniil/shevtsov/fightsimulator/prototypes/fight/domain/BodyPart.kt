package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class BodyPart(
    override val id: BodyPartId,
    val name: String,
    val attackActions: List<AttackAction>,
    val holding: Item? = null,
    val containedBodyParts: Set<BodyPartId> = setOf(),
    val parentId: BodyPartId? = null,
) : Selectable

fun bodyPart(
    id: Long = 0L,
    name: String = "",
    holding: Item? = null,
    attackActions: List<AttackAction> = emptyList(),
    containedBodyParts: Set<BodyPartId> = setOf(),
    parentId: BodyPartId? = null,
) = BodyPart(
    id = bodyPartId(id),
    name = name,
    holding = holding,
    attackActions = attackActions,
    containedBodyParts = containedBodyParts,
    parentId = parentId,
)
