package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class Command(
    val attackAction: AttackAction,
)

fun command(
    attackAction: AttackAction = AttackAction.Strike,
) = Command(
    attackAction = attackAction,
)
