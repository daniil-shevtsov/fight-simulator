package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed class FightAction {
    data class SelectSomething(
        val creatureId: CreatureId,
        val partId: BodyPartId,
    ) : FightAction()

    data class SelectCommand(val attackAction: AttackAction) : FightAction()

    data class SelectControlledActor(val actorId: CreatureId) : FightAction()

    data class SelectTarget(val id: TargetId) : FightAction()

    object Init : FightAction()
}
