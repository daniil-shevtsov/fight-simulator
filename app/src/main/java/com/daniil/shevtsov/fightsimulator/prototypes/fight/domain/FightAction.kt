package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed class FightAction {
    data class SelectBodyPart(
        val creatureId: CreatureId,
        val partId: SelectableId.BodyPart,
    ) : FightAction()

    data class SelectCommand(val attackAction: AttackAction) : FightAction()

    data class SelectActor(val actorId: CreatureId) : FightAction()

    object Init : FightAction()
}
