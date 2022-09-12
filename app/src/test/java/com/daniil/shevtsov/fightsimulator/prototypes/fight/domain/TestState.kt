package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface TestState {
    val state: FightState

    val attacker: Creature
        get() = state.controlledCreature
    val attackerRightHand: BodyPart
        get() = attacker.bodyParts.find { it.name == "Right Hand" }!!
    val attackerLeftHand: BodyPart
        get() = attacker.bodyParts.find { it.name == "Left Hand" }!!
    val target: Creature
        get() = state.targetCreature
    val attackerWeapon: Item
        get() = attacker.functionalParts.find { it.holding != null }?.holding!!

    val ground: Ground
        get() = state.world.ground
}

data class AttackWithItemTestState(
    override val state: FightState,
) : TestState {
    val targetHead: BodyPart
        get() = target.bodyParts.find { it.name == "Head" }!!
    val targetSkull: BodyPart
        get() = target.bodyParts.find { it.name == "Skull" }!!
    val targetRightHand: BodyPart
        get() = target.bodyParts.find { it.name == "Right Hand" }!!
    val targetWeapon: Item
        get() = target.functionalParts.find { it.holding != null }?.holding!!
}
