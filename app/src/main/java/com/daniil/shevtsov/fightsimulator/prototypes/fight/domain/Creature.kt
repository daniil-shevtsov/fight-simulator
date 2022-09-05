package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Creature(
    val id: String,
    val actor: Actor,
    val name: String,
    val bodyParts: List<BodyPart>,
    val missingPartsSet: Set<String> = setOf(),
    val brokenPartsSet: Set<String> = setOf(),
) {
    val missingParts: List<BodyPart>
        get() = bodyParts.filter { it.name in missingPartsSet }
    val brokenParts: List<BodyPart>
        get() = bodyParts.filter { it.name in brokenPartsSet }

    fun firstPart() = bodyParts.first()

}

fun creature(
    id: String,
    actor: Actor = Actor.Enemy,
    name: String = "",
    bodyParts: List<BodyPart> = emptyList(),
    missingPartSet: Set<String> = emptySet(),
    brokenPartSet: Set<String> = emptySet(),
) = Creature(
    id = id,
    actor = actor,
    name = name,
    bodyParts = bodyParts,
    brokenPartsSet = brokenPartSet,
    missingPartsSet = missingPartSet,
)
