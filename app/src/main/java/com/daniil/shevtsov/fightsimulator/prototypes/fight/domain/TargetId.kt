package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed interface TargetId {

    data class Creature(val id: CreatureId) : TargetId

    data class Ground(val id: GroundId) : TargetId
}
