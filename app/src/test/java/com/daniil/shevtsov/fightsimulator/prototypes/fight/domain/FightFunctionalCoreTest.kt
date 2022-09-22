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
                prop(FightState::controlledBodyPart)
                    .prop(BodyPart::id)
                    .isEqualTo(initialState.attackerRightHand.id)
                prop(FightState::targetBodyPart)
                    .isNotNull()
                    .prop(BodyPart::id)
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
            prop(FightState::lastSelectedControlledPartId)
                .isEqualTo(initialState.targetHead.id)
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
                .isEqualTo(initialState.attackerRightHand)
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
            prop(FightState::controlledCreatureBodyParts)
                .each { it.prop(BodyPart::holding).isNull() }
            prop(FightState::targetBodyPart)
                .isNotNull()
                .all {
                    prop(BodyPart::holding)
                        .isEqualTo(testState.attackerWeapon.id)
                    prop(BodyPart::lodgedInSelectables)
                        .contains(testState.attackerWeapon.id)
                }
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("$controlledActorName throws knife at $targetActorName's head with their right hand.\nThe knife has lodged firmly in the wound!")
        }
    }

    @Test
    fun `should select lodged in item`() {
        val initialState = createLodgedInState()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.attacker.id,
                selectableId = initialState.arrow.id,
            )
        )

        assertThat(state).all {
            prop(FightState::targetSelectable)
                .isEqualTo(initialState.arrow)
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
                )
            prop(FightState::targetCreature)
                .prop(Creature::bodyPartIds)
                .containsNone(initialState.targetHead.id, initialState.targetSkull.id)
//            prop(FightState::allSelectables)
//                .any {
//                    it.isInstanceOf(BodyPart::class)
//                        .prop(BodyPart::statuses)
//                        .contains(BodyPartStatus.Missing)
//                }
            prop(FightState::targetBodyPart)
                .isNotNull()
                .prop(BodyPart::id)
                .isNotEqualTo(initialState.targetHead.id)

            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("$controlledActorName slashes at $targetActorName's head with knife held by their right hand.\nSevered head flies off in an arc!")
            prop(FightState::world)
                .prop(World::ground)
                .prop(Ground::selectableIds)
                .containsExactly(initialState.targetHead.id)
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
            .prop(Ground::selectableIds)
            .contains(initialState.targetHead.id)
    }

    @Test
    fun `attack at head should break skull`() {
        val initialState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Pommel)
        )

        assertThat(state).all {
            prop(FightState::allSelectables)
                .transform { it.filterIsInstance<BodyPart>() }
                .extracting(BodyPart::id, BodyPart::statuses)
                .contains(initialState.targetSkull.id to listOf(BodyPartStatus.Broken))
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
                            .prop(Ground::selectableIds)
                            .containsExactly(initialState.targetWeapon.id)
                        prop(FightState::actionLog)
                            .extracting(ActionEntry::text)
                            .index(0)
                            .isEqualTo("$controlledActorName punches $targetActorName's right hand with their left hand. $targetActorName's knife is knocked out to the ground!")
                    }
            }
    }

    @Test
    fun `should select the target when selecting its part`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand()

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
        val initialState = stateForItemPickupWithMissingTargetRightHand()

        val finalState = initialState.state.let { state ->
            fightFunctionalCore(
                state = state,
                action = FightAction.SelectSomething(
                    selectableHolderId = initialState.ground.id,
                    selectableId = initialState.spear.id,
                )
            )
        }.let { state ->
            fightFunctionalCore(
                state = state,
                action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
            )
        }.let { state ->
            fightFunctionalCore(
                state = state,
                action = FightAction.SelectSomething(
                    selectableHolderId = initialState.attacker.id,
                    selectableId = initialState.attackerLeftHand.id,
                )
            )
        }.let { state ->
            fightFunctionalCore(
                state = state,
                action = FightAction.SelectCommand(attackAction = AttackAction.Grab)
            )
        }

        assertThat(finalState)
            .all {
                prop(FightState::world)
                    .prop(World::ground)
                    .prop(Ground::selectableIds)
                    .isEmpty()
                prop(FightState::targetSelectable)
                    .isNotNull()
                    .prop(Selectable::id)
                    .isEqualTo(initialState.otherCreatureHead.id)
            }
    }

    @Test
    fun `should select item on the ground`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand()

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
    fun `should show command for picking up when selected item on the ground with grabber part`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand().let { state ->
            state.copy(
                state = fightFunctionalCore(
                    state = state.state,
                    action = FightAction.SelectSomething(
                        selectableHolderId = state.attacker.id,
                        selectableId = state.attackerLeftHand.id,
                    )
                )
            )
        }

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

    @Test
    fun `should not show command for picking up when selected item on the ground with holding grabber part`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand().let { state ->
            state.copy(
                state = fightFunctionalCore(
                    state = state.state,
                    action = FightAction.SelectSomething(
                        selectableHolderId = state.attacker.id,
                        selectableId = state.attackerRightHand.id,
                    )
                )
            )
        }

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
            .containsNone(AttackAction.Grab)
    }

    @Test
    fun `should start holding body part when grabbed from the ground`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand()

        val stateWithHeadSlashed = fightFunctionalCore(
            initialState.state,
            FightAction.SelectCommand(AttackAction.Slash)
        )
        val stateWithSpearSelected = fightFunctionalCore(
            state = stateWithHeadSlashed,
            action = FightAction.SelectSomething(
                selectableHolderId = initialState.ground.id,
                selectableId = initialState.targetHead.id,
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
                    .prop(Ground::selectableIds)
                    .containsNone(initialState.targetHead.id)
                prop(FightState::controlledBodyPart)
                    .prop(BodyPart::holding)
                    .isEqualTo(initialState.targetHead.id)
            }
    }

    @Test
    fun `should start holding the item when grabbed from the ground`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand()

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
                    .prop(Ground::selectableIds)
                    .containsOnly(initialState.sword.id)
                prop(FightState::controlledBodyPart)
                    .prop(BodyPart::holding)
                    .isEqualTo(initialState.spear.id)
            }
    }

    @Test
    fun `should add log entry when grabbed item from the ground`() {
        val initialState = stateForItemPickupWithMissingTargetRightHand()

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
        val initialState = stateForItemPickupWithMissingTargetRightHand()

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

    private fun stateForItemPickupWithMissingTargetRightHand(): ItemPickupTestState {
        val initialState = createInitialStateWithControlled(controlledActorName)

        val spear = item(id = 401L, name = "Spear")
        val sword = item(id = 501L, name = "Sword")
        val ground = ground(
            id = 1L,
            selectableIds = listOf(spear.id, sword.id)
        )

        //TODO: Control through action
        val state = initialState.copy(
            allSelectables = initialState.allSelectables + listOf(spear, sword),
            world = initialState.world.copy(ground = ground),
        )

        return ItemPickupTestState(
            state = state,
        )
    }

    private fun stateForItemAttack() =
        AttackWithItemTestState(state = createInitialStateWithControlled(controlledActorName))

    private fun createLodgedInState(): LodgedInItemTestState {
        val initialState = stateForItemAttack().let { state ->
            state.copy(
                state = fightFunctionalCore(
                    state = state.state,
                    action = FightAction.SelectSomething(
                        selectableHolderId = state.attacker.id,
                        selectableId = state.attackerLeftHand.id,
                    )
                )
            )
        }

        val arrow = item(id = 401L, name = "Arrow")

        val controlledActor = initialState.attacker
        val controlledBody = initialState.state.allBodyParts.find {
            it.name == "Body" && controlledActor.bodyPartIds.contains(it.id)
        }!!

        val state = initialState.state.copy(
            allSelectables = (initialState.state.allSelectables + listOf(arrow)).map { selectable ->
                when {
                    selectable.id == controlledBody.id && selectable is BodyPart -> selectable.copy(
                        lodgedInSelectables = setOf(arrow.id)
                    )
                    else -> selectable
                }
            },
        )

        return LodgedInItemTestState(
            state = state,
        )
    }
}

fun createInitialStateWithControlled(actorName: String): FightState {
    val originalState = fightFunctionalCore(state = fightState(), action = FightAction.Init)

    val leftActor = originalState.actors.first().copy(name = "Player")
    val rightActor = originalState.actors.last().copy(name = "Enemy")
    val controlled = when (actorName) {
        leftActor.name -> leftActor.id
        rightActor.name -> rightActor.id
        else -> leftActor.id
    }
    val stateWithSelectedBodyParts = fightFunctionalCore(
        state = originalState,
        action = FightAction.SelectControlledActor(controlled)
    ).let { state ->
        fightFunctionalCore(
            state = state,
            action = FightAction.SelectSomething(
                state.controlledCreature.id,
                state.controlledCreatureBodyParts.find { it.name == "Right Hand" }!!.id
            )
        )
    }.let { state ->
        fightFunctionalCore(
            state = state,
            action = FightAction.SelectSomething(
                state.targetCreature.id,
                state.targetCreatureBodyParts.find { it.name == "Head" }!!.id
            )
        )
    }
    return stateWithSelectedBodyParts
}
