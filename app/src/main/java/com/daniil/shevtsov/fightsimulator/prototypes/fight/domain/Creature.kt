package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Creature(
    val id: CreatureId,
    val actor: Actor,
    val name: String,
    val bodyParts: List<BodyPart>,
    val missingPartsSet: Set<SelectableId.BodyPart> = setOf(),
    val brokenPartsSet: Set<SelectableId.BodyPart> = setOf(),
) : Targetable {

    override val targetId: TargetId
        get() = TargetId.Creature(id)

    val missingParts: List<BodyPart>
        get() = bodyParts.filter { it.id in missingPartsSet }
    val brokenParts: List<BodyPart>
        get() = bodyParts.filter { it.id in brokenPartsSet }

    val functionalParts: List<BodyPart>
        get() = bodyParts.filter { it.id !in missingPartsSet }

    fun firstPart() = bodyParts.first()

}

fun creature(
    id: Long,
    actor: Actor = Actor.Enemy,
    name: String = "",
    bodyParts: List<BodyPart> = emptyList(),
    missingPartSet: Set<SelectableId.BodyPart> = emptySet(),
    brokenPartSet: Set<SelectableId.BodyPart> = emptySet(),
) = Creature(
    id = creatureId(id),
    actor = actor,
    name = name,
    bodyParts = bodyParts,
    brokenPartsSet = brokenPartSet,
    missingPartsSet = missingPartSet,
)
