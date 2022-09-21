package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.bodyPartId

fun bodyPartItem(
    id: Long,
    name: String = "",
    holding: SelectableItem? = null,
    contained: Set<SelectableItem> = setOf(),
    lodgedIn: List<SelectableItem> = emptyList(),
    statuses: List<BodyPartStatus> = emptyList(),
    canGrab: Boolean = false,
    isSelected: Boolean = false,
) = SelectableItem.BodyPartItem(
    id = bodyPartId(id),
    name = name,
    holding = holding,
    contained = contained,
    lodgedIn = lodgedIn,
    statuses = statuses,
    canGrab = canGrab,
    isSelected = isSelected,
)
