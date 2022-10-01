package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class BodyPart(
    override val id: BodyPartId,
    override val name: String,
    val attackActions: List<AttackAction>,
    val statuses: List<BodyPartStatus>,
    val holding: SelectableId? = null,
    val containedBodyParts: Set<BodyPartId> = setOf(),
    val lodgedInSelectables: Set<SelectableId> = setOf(),
    val containerPartId: BodyPartId? = null,
    val parentPartId: BodyPartId? = null,
    val canGrab: Boolean,
) : Selectable

fun bodyPart(
    id: Long = 0L,
    name: String = "",
    holding: SelectableId? = null,
    attackActions: List<AttackAction> = emptyList(),
    statuses: List<BodyPartStatus> = emptyList(),
    containedBodyParts: Set<BodyPartId> = setOf(),
    containerPartId: BodyPartId? = null,
    parentPartId: BodyPartId? = null,
    canGrab: Boolean = false,
) = BodyPart(
    id = bodyPartId(id),
    name = name,
    holding = holding,
    attackActions = attackActions,
    statuses = statuses,
    containedBodyParts = containedBodyParts,
    containerPartId = containerPartId,
    parentPartId = parentPartId,
    canGrab = canGrab,
)
