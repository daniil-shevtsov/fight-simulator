package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface TestState {
    val state: FightState

    val attacker: Creature
        get() = state.controlledCreature
    val attackerRightHand: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Right Hand" }!!
    val attackerLeftHand: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Left Hand" }!!
    val attackerHead: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Head" }!!
    val target: Creature
        get() = state.targetCreature
    val targetHead: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Head" }!!
    val targetSkull: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Skull" }!!
    val targetRightHand: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Right Hand" }!!
    val attackerWeapon: Selectable
        get() = state.controlledCreatureBodyParts.find { it.holding != null }?.holding?.let { id -> state.allSelectables.find { it.id == id } }!!
    val nonGrabbingPart: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Right Leg" }!!

    val otherCreature: Creature
        get() = state.actors.find { it.id != state.controlledCreature.id }!!
    val otherCreatureHead: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Head" }!!

    val ground: Ground
        get() = state.world.ground
}

data class ItemPickupTestState(
    override val state: FightState,
) : TestState {
    val spear: Item
        get() = state.selectables.find { it.name == "Spear" }!! as Item
    val sword: Item
        get() = state.selectables.find { it.name == "Sword" }!! as Item
}

data class LodgedInItemTestState(
    override val state: FightState,
) : TestState {
    val arrow: Item
        get() = state.selectables.find { it.name == "Arrow" }!! as Item
    val bodyPartWithLodgedInItem: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Body" }!!
}

data class AttackWithItemTestState(
    override val state: FightState,
) : TestState {

    val targetWeapon: Item
        get() = state.targetCreatureBodyParts.find { it.holding != null }?.holding?.let { id -> state.allItems.find { it.id == id } }!!
}
