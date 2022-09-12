package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Item(
    override val id: ItemId,
    val name: String,
    val attackActions: List<AttackAction>,
) : Selectable

fun item(
    id: Long = 0L,
    name: String = "",
    attackActions: List<AttackAction> = emptyList(),
) = Item(
    id = itemId(id),
    name = name,
    attackActions = attackActions,
)
