package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed class TestState {
    data class AttackWithItem(
        val state: FightState,
    ) : TestState() {
        val attacker: Creature
            get() = state.controlledCreature
        val target: Creature
            get() = state.targetCreature
        val attackerWeapon: Item
            get() = attacker.functionalParts.find { it.holding != null }?.holding!!
        val targetHead: BodyPart
            get() = target.bodyParts.find { it.name == "Head" }!!
        val targetSkull: BodyPart
            get() = target.bodyParts.find { it.name == "Skull" }!!
        val targetRightHand: BodyPart
            get() = target.bodyParts.find { it.name == "Right Hand" }!!
        val targetWeapon: Item
            get() = target.functionalParts.find { it.holding != null }?.holding!!

    }
}
