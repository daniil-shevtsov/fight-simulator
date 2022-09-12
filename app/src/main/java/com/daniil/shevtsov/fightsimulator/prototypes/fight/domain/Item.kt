package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Item(
    val id: SelectableId.Item,
    val name: String,
    val attackActions: List<AttackAction>,
)

fun item(
    id: Long = 0L,
    name: String = "",
    attackActions: List<AttackAction> = emptyList(),
) = Item(
    id = itemId(id),
    name = name,
    attackActions = attackActions,
)
