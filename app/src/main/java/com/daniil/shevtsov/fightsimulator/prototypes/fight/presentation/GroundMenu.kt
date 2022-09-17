package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.GroundId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Selectable

data class GroundMenu(
    val id: GroundId,
    val selectables: List<Selectable>,
    val isSelected: Boolean,
)
