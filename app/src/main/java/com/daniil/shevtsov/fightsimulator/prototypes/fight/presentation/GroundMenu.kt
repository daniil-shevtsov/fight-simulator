package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.GroundId

data class GroundMenu(
    val id: GroundId,
    val selectables: List<SelectableItem>,
    val isSelected: Boolean,
)
