package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed class FightAction {
    data class SelectSomething(
        val selectableHolderId: SelectableHolderId,
        val selectableId: SelectableId,
    ) : FightAction()

    data class SelectCommand(val attackAction: AttackAction) : FightAction()

    data class SelectControlledActor(val actorId: CreatureId) : FightAction()

    data class SelectTarget(val id: TargetId) : FightAction()

    object Init : FightAction()
}
