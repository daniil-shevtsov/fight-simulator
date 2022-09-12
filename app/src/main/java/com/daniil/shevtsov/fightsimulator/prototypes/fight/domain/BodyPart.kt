package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class BodyPart(
    val id: SelectableId.BodyPart,
    val name: String,
    val attackActions: List<AttackAction>,
    val holding: Item? = null,
    val containedBodyParts: Set<SelectableId.BodyPart> = setOf(),
    val parentId: SelectableId.BodyPart? = null,
)

fun bodyPart(
    id: Long = 0L,
    name: String = "",
    holding: Item? = null,
    attackActions: List<AttackAction> = emptyList(),
    containedBodyParts: Set<SelectableId.BodyPart> = setOf(),
    parentId: SelectableId.BodyPart? = null,
) = BodyPart(
    id = SelectableId.BodyPart(id),
    name = name,
    holding = holding,
    attackActions = attackActions,
    containedBodyParts = containedBodyParts,
    parentId = parentId,
)
