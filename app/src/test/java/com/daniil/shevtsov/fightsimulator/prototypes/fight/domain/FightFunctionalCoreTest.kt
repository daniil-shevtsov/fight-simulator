package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

interface FightFunctionalCoreTest {

    val controlledActorName: String
    val targetActorName: String

    @Test
    fun `should select body part when clicked`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.attacker.id,
                selectableId = initialState.attackerLeftHand.id,
            )
        )

        assertThat(state)
            .controlledBodyPartName()
            .isEqualTo(initialState.attackerLeftHand.name)
    }

    private fun Assert<FightState>.controlledBodyPartName() = prop(FightState::controlledBodyPart)
        .prop(BodyPart::name)

    @Test
    fun `should not select slashed part when clicked`() {
        val initialState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = fightFunctionalCore(
                state = fightFunctionalCore(
                    state = initialState.state, action = FightAction.SelectSomething(
                        selectableHolderId = initialState.target.id,
                        selectableId = initialState.targetRightHand.id,
                    )
                ),
                action = FightAction.SelectCommand(attackAction = AttackAction.Slash)
            ),
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.target.id,
                selectableId = initialState.targetRightHand.id,
            )
        )

        assertThat(state)
            .all {
                prop(FightState::controlledBodyPart).prop(BodyPart::id)
                    .isEqualTo(initialState.attackerRightHand.id)
                prop(FightState::targetBodyPart).prop(BodyPart::id)
                    .isEqualTo(initialState.targetHead.id)
            }
    }

    @Test
    fun `should display body part actions when selected attacker and target body parts`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.attacker.id,
                selectableId = initialState.attackerLeftHand.id,
            )
        )

        assertThat(state)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .isEqualTo(initialState.attackerLeftHand.attackActions)
    }

    @Test
    fun `should add entry to log when command clicked`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = fightFunctionalCore(
                state = initialState.state,
                action = FightAction.SelectSomething(
                    selectableHolderId = initialState.attacker.id,
                    selectableId = initialState.attackerLeftHand.id
                )
            ),
            action = FightAction.SelectCommand(attackAction = AttackAction.Punch)
        )

        assertThat(state)
            .prop(FightState::actionLog)
            .extracting(ActionEntry::text)
            .containsExactly("$controlledActorName punches $targetActorName's head with their left hand")
    }

    @Test
    fun `should select who I am playing`() {
        val initialState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectControlledActor(actorId = initialState.target.id)
        )

        assertThat(state).all {
            prop(FightState::lastSelectedControlledHolderId)
                .isEqualTo(initialState.target.id)
        }
    }

    @Test
    fun `should change selections when changed controlled creature`() {
        val initialState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectControlledActor(actorId = initialState.target.id)
        )

        assertThat(state).all {
            prop(FightState::controlledBodyPart)
                .isEqualTo(initialState.otherCreatureHead)
            prop(FightState::targetSelectable)
                .isEqualTo(initialState.attackerHead)
            prop(FightState::availableCommands)
                .extracting(Command::attackAction)
                .contains(AttackAction.Headbutt)
        }
    }

    @Test
    fun `should display weapon commands when selected attacker and target parts`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.target.id,
                selectableId = initialState.targetHead.id,
            )
        )

        assertThat(state)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .containsExactly(
                AttackAction.Punch,
                AttackAction.Slash,
                AttackAction.Stab,
                AttackAction.Pommel,
                AttackAction.Throw,
            )
    }

    @Test
    fun `should use item in log entry when command clicked`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Stab)
        )

        assertThat(state)
            .prop(FightState::actionLog)
            .extracting(ActionEntry::text)
            .containsExactly("$controlledActorName stabs $targetActorName's head with knife held by their right hand")
    }

    @Test
    fun `should move item from attacker to target when throwing`() {
        val testState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = testState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Throw)
        )

        assertThat(state).all {
            prop(FightState::controlledCreature)
                .prop(Creature::bodyParts)
                .each { it.prop(BodyPart::holding).isNull() }
            prop(FightState::targetCreature)
                .prop(Creature::bodyParts)
                .any {
                    it.prop(BodyPart::holding)
                        .isNotNull()
                        .prop(Item::name)
                        .isEqualTo(testState.attackerWeapon.name)
                }
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("$controlledActorName throws knife at $targetActorName's head with their right hand.\nThe knife has lodged firmly in the wound!")
        }
    }

    @Test
    fun `should throw by controlled actor`() {
        val testState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = testState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Throw)
        )

        assertThat(state).all {
            prop(FightState::controlledCreature)
                .prop(Creature::bodyParts)
                .each { it.prop(BodyPart::holding).isNull() }
            prop(FightState::targetCreature)
                .prop(Creature::bodyParts)
                .any {
                    it.prop(BodyPart::holding)
                        .isNotNull()
                        .prop(Item::name)
                        .isEqualTo(testState.attackerWeapon.name)
                }
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("$controlledActorName throws knife at $targetActorName's head with their right hand.\nThe knife has lodged firmly in the wound!")
        }
    }

    @Test
    fun `should remove limb and contained parts when attacker is slashing`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Slash)
        )

        assertThat(state).all {
            prop(FightState::targetCreature)
                .prop(Creature::missingPartsSet)
                .containsAll(
                    initialState.targetHead.id,
                    initialState.targetSkull.id
                )
            prop(FightState::targetBodyPart)
                .prop(BodyPart::id)
                .isNotEqualTo(initialState.targetHead.id)

            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("$controlledActorName slashes at $targetActorName's head with knife held by their right hand.\nSevered head flies off in an arc!")
            prop(FightState::world)
                .prop(World::ground)
                .prop(Ground::selectables)
                .contains(initialState.targetHead)
        }
    }

    @Test
    fun `should move limb to ground when attacker is slashing`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Slash)
        )

        assertThat(state)
            .prop(FightState::world)
            .prop(World::ground)
            .prop(Ground::selectables)
            .contains(initialState.targetHead)
    }

    @Test
    fun `attack at head should break skull`() {
        val initialState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Pommel)
        )

        assertThat(state).all {
            prop(FightState::targetCreature)
                .prop(Creature::brokenPartsSet)
                .containsOnly(initialState.targetSkull.id)
            prop(FightState::actionLog)
                .extracting(ActionEntry::text)
                .index(0)
                .isEqualTo("$controlledActorName pommels $targetActorName's head with knife held by their right hand. The skull is fractured!")
        }
    }

    @Test
    fun `should knock out item from hand when attacking it`() {
        val initialState = stateForItemAttack()

        val stateWithTargetHand = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.target.id,
                selectableId = initialState.targetRightHand.id
            )
        )
        val stateWithBothSelections = fightFunctionalCore(
            state = stateWithTargetHand,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.attacker.id,
                selectableId = initialState.attackerLeftHand.id
            )
        )
        val state = fightFunctionalCore(
            state = stateWithBothSelections,
            action = FightAction.SelectCommand(attackAction = AttackAction.Punch)
        )

        assertThat(AttackWithItemTestState(state))
            .all {
                prop(AttackWithItemTestState::targetRightHand)
                    .prop(BodyPart::holding)
                    .isNull()
                prop(AttackWithItemTestState::state)
                    .all {
                        prop(FightState::world)
                            .prop(World::ground)
                            .prop(Ground::selectables)
                            .containsExactly(initialState.targetWeapon)
                        prop(FightState::actionLog)
                            .extracting(ActionEntry::text)
                            .index(0)
                            .isEqualTo("$controlledActorName punches $targetActorName's right hand with their left hand. $targetActorName's knife is knocked out to the ground!")
                    }
            }
    }

    @Test
    fun `should select the target when selecting its part`() {
        val initialState = stateForItemPickup()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.otherCreature.id,
                selectableId = initialState.otherCreatureHead.id,
            )
        )
        assertThat(AttackWithItemTestState(state))
            .all {
                prop(AttackWithItemTestState::state)
                    .prop(FightState::targetSelectableHolder)
                    .prop(SelectableHolder::id)
                    .isEqualTo(initialState.otherCreature.id)
            }
    }

    @Test
    fun `should select another target and part when last selected does not make sense`() {
        val initialState = stateForItemPickup()

        val stateWithSpearSelected = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.spear.id,
            )
        )
        val stateWithSpearGrabbed = fightFunctionalCore(
            state = stateWithSpearSelected,
            action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
        )
        val stateWithAnotherHandSelected = fightFunctionalCore(
            state = stateWithSpearGrabbed,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.attacker.id,
                selectableId = initialState.attackerLeftHand.id,
            )
        )
        val stateWithHelmetGrabbed = fightFunctionalCore(
            state = stateWithAnotherHandSelected,
            action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
        )

        assertThat(stateWithHelmetGrabbed)
            .all {
                prop(FightState::world)
                    .prop(World::ground)
                    .prop(Ground::selectables)
                    .isEmpty()
                prop(FightState::targetSelectable)
                    .isNotNull()
                    .prop(Selectable::id)
                    .isEqualTo(initialState.otherCreatureHead.id)
            }
    }

    @Test
    fun `should select item on the ground`() {
        val initialState = stateForItemPickup()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.spear.id,
            )
        )

        assertThat(state)
            .prop(FightState::targetSelectable)
            .isEqualTo(initialState.spear)
    }

    @Test
    fun `should should show command for picking up when selected item on the groun with grabber part`() {
        val initialState = stateForItemPickup()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.spear.id,
            )
        )

        assertThat(state)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .containsOnly(AttackAction.Grab)
    }

//    @Test
//    fun `should start holding body part when grabbed from the ground`() {
//        val initialState = stateForItemPickup()
//
//        val stateWithHeadSlashed = fightFunctionalCore(
//            initialState.state,
//            FightAction.SelectCommand(AttackAction.Slash)
//        )
//        val stateWithSpearSelected = fightFunctionalCore(
//            state = stateWithHeadSlashed,
//            action = FightAction.SelectSomething(
//                selectableHolderId = initialState.ground.id,
//                selectableId = initialState.targetHead.id,
//            )
//        )
//        val state = fightFunctionalCore(
//            state = stateWithSpearSelected,
//            action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
//        )
//
//        assertThat(state)
//            .all {
//                prop(FightState::world)
//                    .prop(World::ground)
//                    .prop(Ground::selectables)
//                    .containsNone(initialState.targetHead)
//                prop(FightState::controlledBodyPart)
//                    .prop(BodyPart::holding)
//                    .isEqualTo(initialState.targetHead)
//            }
//    }

    @Test
    fun `should start holding the item when grabbed from the ground`() {
        val initialState = stateForItemPickup()

        val stateWithSpearSelected = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.spear.id,
            )
        )
        val state = fightFunctionalCore(
            state = stateWithSpearSelected,
            action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
        )

        assertThat(state)
            .all {
                prop(FightState::world)
                    .prop(World::ground)
                    .prop(Ground::selectables)
                    .containsOnly(initialState.sword)
                prop(FightState::controlledBodyPart)
                    .prop(BodyPart::holding)
                    .isEqualTo(initialState.spear)
            }
    }

    @Test
    fun `should add log entry when grabbed item from the ground`() {
        val initialState = stateForItemPickup()

        val stateWithSpearSelected = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.spear.id,
            )
        )
        val state = fightFunctionalCore(
            state = stateWithSpearSelected,
            action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
        )

        assertThat(state)
            .prop(FightState::actionLog)
            .index(0)
            .prop(ActionEntry::text)
            .isEqualTo("$controlledActorName picks up the spear from the ground.")
    }

    @Test
    fun `should not show Grab action for body parts that can't grab`() {
        val initialState = stateForItemPickup()

        val stateWithNonGrabberBodyPart = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.attacker.id,
                selectableId = initialState.nonGrabbingPart.id,
            )
        )
        val stateWithSpearSelected = fightFunctionalCore(
            state = stateWithNonGrabberBodyPart,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.spear.id,
            )
        )

        assertThat(stateWithSpearSelected)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .containsNone(AttackAction.Grab)
    }

    private fun stateForItemPickup(): ItemPickupTestState {
        val initialState = fightFunctionalCore(state = fightState(), action = FightAction.Init)

        val leftActor = initialState.actors.first().copy(name = "Player")
        val rightActor = initialState.actors.last().copy(name = "Enemy")
        val controlled = when (controlledActorName) {
            leftActor.name -> leftActor.id
            rightActor.name -> rightActor.id
            else -> leftActor.id
        }

        val spear = item(id = 4L, name = "Spear")
        val sword = item(id = 5L, name = "Sword")
        val ground = ground(
            id = 1L,
            items = listOf(spear, sword)
        )

        val state = initialState.copy(
            world = initialState.world.copy(ground = ground),
            lastSelectedControlledHolderId = controlled,
            lastSelectedControlledPartId = when (leftActor.id) {
                controlled -> leftActor.bodyParts.find { it.name == "Right Hand" }!!.id
                else -> rightActor.bodyParts.find { it.name == "Right Hand" }!!.id
            },
            lastSelectedTargetPartId = when (leftActor.id) {
                controlled -> rightActor.bodyParts.find { it.name == "Head" }!!.id
                else -> leftActor.bodyParts.find { it.name == "Head" }!!.id
            },
            lastSelectedTargetHolderId = when (leftActor.id) {
                controlled -> rightActor.id
                else -> leftActor.id
            },
        )

        return ItemPickupTestState(
            state = state,
        )
    }

    private fun stateForItemAttack() = stateForItemAttack(controlledActorName)
}

fun stateForItemAttack(controlledActorName: String): AttackWithItemTestState {
    val initialState = fightFunctionalCore(state = fightState(), action = FightAction.Init)

    val leftActor = initialState.actors.first().copy(name = "Player")
    val rightActor = initialState.actors.last().copy(name = "Enemy")
    val controlled = when (controlledActorName) {
        leftActor.name -> leftActor.id
        rightActor.name -> rightActor.id
        else -> leftActor.id
    }

    val ground = ground(id = 1L)

    val state = initialState.copy(
        world = initialState.world.copy(ground = ground),
        lastSelectedControlledHolderId = controlled,
        lastSelectedControlledPartId = when (leftActor.id) {
            controlled -> leftActor.bodyParts.find { it.name == "Right Hand" }!!.id
            else -> rightActor.bodyParts.find { it.name == "Right Hand" }!!.id
        },
        lastSelectedTargetPartId = when (leftActor.id) {
            controlled -> rightActor.bodyParts.find { it.name == "Head" }!!.id
            else -> leftActor.bodyParts.find { it.name == "Head" }!!.id
        },
        lastSelectedTargetHolderId = when (leftActor.id) {
            controlled -> rightActor.id
            else -> leftActor.id
        },
    )

    return AttackWithItemTestState(
        state = state,
    )
}
