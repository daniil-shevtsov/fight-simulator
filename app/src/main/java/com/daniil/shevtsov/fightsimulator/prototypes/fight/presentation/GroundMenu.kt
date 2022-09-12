package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Item

data class GroundMenu(
    val items: List<Item>,
    val isSelected: Boolean,
)
