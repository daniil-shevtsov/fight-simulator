package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface TestState {
    val state: FightState

    val attacker: Creature
        get() = state.controlledCreature
    val attackerRightHand: BodyPart
        get() = attacker.bodyParts.find { it.name == "Right Hand" }!!
    val attackerLeftHand: BodyPart
        get() = attacker.bodyParts.find { it.name == "Left Hand" }!!
    val attackerHead: BodyPart
        get() = attacker.findBodyPart(withName = "Head")
    val target: Creature
        get() = state.targetCreature
    val attackerWeapon: Item
        get() = attacker.functionalParts.find { it.holding != null }?.holding!!
    val nonGrabbingPart: BodyPart
        get() = attacker.bodyParts.find { it.name == "Right Leg" }!!

    val otherCreature: Creature
        get() = state.actors.find { it.id != state.controlledCreature.id }!!
    val otherCreatureHead: BodyPart
        get() = otherCreature.findBodyPart(withName = "Head")

    val ground: Ground
        get() = state.world.ground

    private fun Creature.findBodyPart(withName: String) = bodyParts.find { it.name == withName }!!
}

data class ItemPickupTestState(
    override val state: FightState,
) : TestState {
    val spear: Item
        get() = state.selectables.find { it.name == "Spear" }!! as Item
    val sword: Item
        get() = state.selectables.find { it.name == "Sword" }!! as Item
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
