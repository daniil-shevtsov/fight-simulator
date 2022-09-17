package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface TargetId {
    val raw: Long
}

fun creatureTargetId(raw: Long): TargetId = creatureId(raw)
fun groundTargetId(raw: Long): TargetId = groundId(raw)
