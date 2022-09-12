package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Ground(
    override val id: GroundId,
    val bodyParts: List<BodyPart>,
    val items: List<Item>,
) : Targetable, SelectableHolder

fun ground(
    id: Long = 0L,
    bodyParts: List<BodyPart> = emptyList(),
    items: List<Item> = emptyList(),
) = Ground(
    id = groundId(id),
    bodyParts = bodyParts,
    items = items,
)
