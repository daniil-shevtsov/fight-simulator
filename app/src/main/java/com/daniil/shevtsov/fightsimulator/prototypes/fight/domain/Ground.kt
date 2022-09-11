package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Ground(
    val bodyParts: List<BodyPart>,
    val items: List<Item>,
)

fun ground(
    bodyParts: List<BodyPart> = emptyList(),
    items: List<Item> = emptyList(),
) = Ground(
    bodyParts = bodyParts,
    items = items,
)
