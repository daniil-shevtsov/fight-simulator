package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed class FightAction {
    data class SelectBodyPart(
        val creatureId: String,
        val partId: SelectableId.BodyPart,
    ) : FightAction()

    data class SelectCommand(val attackAction: AttackAction) : FightAction()

    data class SelectActor(val actorId: String) : FightAction()

    object Init : FightAction()
}
