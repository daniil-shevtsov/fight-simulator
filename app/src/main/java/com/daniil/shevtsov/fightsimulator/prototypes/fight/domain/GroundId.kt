package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

@JvmInline
value class GroundId(val raw: Long)

fun groundId(raw: Long) = GroundId(raw)
