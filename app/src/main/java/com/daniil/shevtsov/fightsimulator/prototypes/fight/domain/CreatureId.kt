package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

@JvmInline
value class CreatureId(val raw: String)

fun creatureId(raw: String) = CreatureId(raw)
