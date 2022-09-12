package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.GroundId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Item

data class GroundMenu(
    val id: GroundId,
    val items: List<Item>,
    val isSelected: Boolean,
)
