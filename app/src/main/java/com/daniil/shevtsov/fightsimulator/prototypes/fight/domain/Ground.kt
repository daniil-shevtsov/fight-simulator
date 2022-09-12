package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Ground(
    val id: GroundId,
    val bodyParts: List<BodyPart>,
    val items: List<Item>,
) : Targetable {
    override val targetId: TargetId
        get() = id
}

fun ground(
    id: Long = 0L,
    bodyParts: List<BodyPart> = emptyList(),
    items: List<Item> = emptyList(),
) = Ground(
    id = groundId(id),
    bodyParts = bodyParts,
    items = items,
)
