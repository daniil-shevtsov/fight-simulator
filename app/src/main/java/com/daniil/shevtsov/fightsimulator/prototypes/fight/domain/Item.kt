package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Item(
    val id: ItemId,
    val name: String,
    val attackActions: List<AttackAction>,
)

@JvmInline
value class ItemId(val raw: Long)

fun item(
    id: Long = 0L,
    name: String = "",
    attackActions: List<AttackAction> = emptyList(),
) = Item(
    id = ItemId(id),
    name = name,
    attackActions = attackActions,
)
