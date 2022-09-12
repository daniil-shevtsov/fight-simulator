package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Creature(
    override val id: CreatureId,
    val actor: Actor,
    val name: String,
    val bodyParts: List<BodyPart>,
    val missingPartsSet: Set<BodyPartId> = setOf(),
    val brokenPartsSet: Set<BodyPartId> = setOf(),
) : Targetable, SelectableHolder {

    val missingParts: List<BodyPart>
        get() = bodyParts.filter { it.id in missingPartsSet }

    val brokenParts: List<BodyPart>
        get() = bodyParts.filter { it.id in brokenPartsSet }
    val functionalParts: List<BodyPart>
        get() = bodyParts.filter { it.id !in missingPartsSet }

    override val selectables: List<Selectable>
        get() = functionalParts

    fun firstPart() = bodyParts.first()

}

fun creature(
    id: Long,
    actor: Actor = Actor.Enemy,
    name: String = "",
    bodyParts: List<BodyPart> = emptyList(),
    missingPartSet: Set<BodyPartId> = emptySet(),
    brokenPartSet: Set<BodyPartId> = emptySet(),
) = Creature(
    id = creatureId(id),
    actor = actor,
    name = name,
    bodyParts = bodyParts,
    brokenPartsSet = brokenPartSet,
    missingPartsSet = missingPartSet,
)
