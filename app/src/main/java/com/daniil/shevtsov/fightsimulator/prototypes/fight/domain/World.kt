package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class World(
    val ground: Ground,
)

fun world(
    ground: Ground = ground()
) = World(
    ground = ground,
)
