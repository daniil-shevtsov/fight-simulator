package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Creature(
    override val id: CreatureId,
    val actor: Actor,
    val name: String,
    val bodyPartIds: List<BodyPartId>,
    val missingPartsSet: Set<BodyPartId> = setOf(),
) : SelectableHolder {


    val missingParts: List<BodyPartId>
        get() = bodyPartIds.filter { it in missingPartsSet }

    val functionalParts: List<BodyPartId>
        get() = bodyPartIds.filter { it !in missingPartsSet }

    override val selectableIds: List<SelectableId>
        get() = bodyPartIds

}

fun creature(
    id: Long,
    actor: Actor = Actor.Enemy,
    name: String = "",
    bodyPartIds: List<BodyPartId> = emptyList(),
    missingPartSet: Set<BodyPartId> = emptySet(),
) = Creature(
    id = creatureId(id),
    actor = actor,
    name = name,
    bodyPartIds = bodyPartIds,
    missingPartsSet = missingPartSet,
)
