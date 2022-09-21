package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed interface Selectable {
    val id: SelectableId
    val name: String
}
