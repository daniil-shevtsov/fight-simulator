package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Creature(
    override val id: CreatureId,
    val actor: Actor,
    val name: String,
    val bodyParts: List<BodyPart>,
    val bodyPartIds: List<BodyPartId>,
    val missingPartsSet: Set<BodyPartId> = setOf(),
) : SelectableHolder {


    val missingParts: List<BodyPart>
        get() = bodyParts.filter { it.id in missingPartsSet }

    val brokenParts: List<BodyPart>
        get() = bodyParts.filter { it.statuses.contains(BodyPartStatus.Broken)}
    val functionalParts: List<BodyPart>
        get() = bodyParts.filter { it.id !in missingPartsSet }

    override val selectables: List<Selectable>
        get() = functionalParts
    override val selectableIds: List<SelectableId>
        get() = bodyPartIds

    fun firstPart() = bodyParts.first()

}

fun creature(
    id: Long,
    actor: Actor = Actor.Enemy,
    name: String = "",
    bodyParts: List<BodyPart> = emptyList(),
    bodyPartIds: List<BodyPartId> = emptyList(),
    missingPartSet: Set<BodyPartId> = emptySet(),
) = Creature(
    id = creatureId(id),
    actor = actor,
    name = name,
    bodyParts = bodyParts,
    bodyPartIds = bodyPartIds,
    missingPartsSet = missingPartSet,
)
