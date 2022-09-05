package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Item(
    val name: String,
    val attackActions: List<AttackAction>,
)

fun item(
    name: String = "",
    attackActions: List<AttackAction> = emptyList(),
) = Item(
    name = name,
    attackActions = attackActions,
)
