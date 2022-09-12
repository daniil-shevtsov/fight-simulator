package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

@JvmInline
value class CreatureId(val raw: Long)

fun creatureId(raw: Long) = CreatureId(raw)
