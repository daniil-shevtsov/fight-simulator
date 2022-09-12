package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed interface TargetId {

    data class Creature(val id: CreatureId) : TargetId

    data class Ground(val id: GroundId) : TargetId
}

fun creatureTargetId(raw: String) = TargetId.Creature(creatureId(raw))
fun groundTargetId(raw: Long) = TargetId.Ground(groundId(raw))
