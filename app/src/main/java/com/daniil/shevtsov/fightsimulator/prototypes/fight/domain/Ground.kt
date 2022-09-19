package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Ground(
    override val id: GroundId,
    override val selectables: List<Selectable>
) : SelectableHolder {
    override val selectableIds: List<SelectableId>
        get() = selectables.map(Selectable::id)
}

fun ground(
    id: Long = 0L,
    bodyParts: List<BodyPart> = emptyList(),
    items: List<Item> = emptyList(),
) = Ground(
    id = groundId(id),
    selectables = bodyParts + items
)
