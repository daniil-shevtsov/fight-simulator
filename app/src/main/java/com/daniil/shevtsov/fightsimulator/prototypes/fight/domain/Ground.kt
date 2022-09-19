package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Ground(
    override val id: GroundId,
    override val selectableIds: List<SelectableId>
) : SelectableHolder

fun ground(
    id: Long = 0L,
    selectableIds: List<SelectableId> = emptyList(),
) = Ground(
    id = groundId(id),
    selectableIds = selectableIds,
)
