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
    val attackerFoot: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Right Foot" }!!
    val target: Creature
        get() = state.targetSelectableHolder as Creature
    val targetHead: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Head" }!!
    val targetSkull: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Skull" }!!
    val targetRightHand: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Right Hand" }!!
    val attackerWeapon: Selectable
        get() = state.controlledCreatureBodyParts.find { it.holding != null }?.holding?.let { id -> state.allSelectables[id] }!!
    val nonGrabbingPart: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Right Leg" }!!

    val otherCreature: Creature
        get() = state.actors[state.targetSelectableHolder.id]!!
    val otherCreatureHead: BodyPart
        get() = state.targetCreatureBodyParts.find { it.name == "Head" }!!

    val ground: Ground
        get() = state.ground
}

data class ItemPickupTestState(
    override val state: FightState,
) : TestState {
    val spear: Item
        get() = state.allSelectables.values.find { it.name == "Spear" }!! as Item
    val sword: Item
        get() = state.allSelectables.values.find { it.name == "Sword" }!! as Item
}

data class LodgedInItemTestState(
    override val state: FightState,
) : TestState {
    val arrow: Item
        get() = state.allSelectables.values.find { it.name == "Arrow" }!! as Item
    val bodyPartWithLodgedInItem: BodyPart
        get() = state.controlledCreatureBodyParts.find { it.name == "Body" }!!
}

data class AttackWithItemTestState(
    override val state: FightState,
) : TestState {

    val targetWeapon: Item
        get() = state.targetCreatureBodyParts.find { it.holding != null }?.holding?.let { id -> state.allItems.find { it.id == id } }!!
}
