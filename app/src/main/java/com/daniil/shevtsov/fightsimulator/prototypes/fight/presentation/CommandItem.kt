package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.AttackAction

data class CommandItem(
    val attackAction: AttackAction,
    val name: String,
)

fun commandItem(
    attackAction: AttackAction = AttackAction.Strike,
    name: String = "",
) = CommandItem(attackAction = attackAction, name = name)
