package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

interface FightWeaponTest {

    val controlledActorName: String
    val targetActorName: String

    @Test
    fun `should select body part when clicked`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                creatureId = initialState.attacker.id,
                selectableId = initialState.attackerLeftHand.id,
            )
        )

        assertThat(state)
            .controlledBodyPartName()
            .isEqualTo(initialState.attackerLeftHand.name)
    }

    private fun Assert<FightState>.controlledBodyPartName() = prop(FightState::controlledBodyPart)
        .prop(BodyPart::name)

//    @Test
//    fun `should not select slashed part when clicked`() {
//        val leftHand = bodyPart(id = 0L, name = "Left Hand")
//        val rightHand = bodyPart(id = 1L, name = "Right Hand")
//        val initialState = normalFullState(
//            bodyParts = listOf(leftHand, rightHand),
//            controlledPartName = rightHand.name,
//            targetPartName = leftHand.name,
//            missingParts = listOf(leftHand.name)
//        )
//        val state = fightFunctionalCore(
//            state = initialState,
//            action = FightAction.SelectSomething(
//                creatureId = initialState.controlledActorId,
//                selectableId = leftHand.id,
//            )
//        )
//
//        assertThat(state)
//            .prop(FightState::selections)
//            .containsAll(
//                initialState.controlledActorId to rightHand.id,
//                initialState.targetCreature.id to initialState.targetCreature.bodyParts.first().id,
//            )
//    }

    @Test
    fun `should display body part actions when selected attacker and target body parts`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                creatureId = initialState.attacker.id,
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
                    creatureId = initialState.attacker.id,
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
//
//    @Test
//    fun `should select who I am playing`() {
//        val initialState = normalFullState()
//
//        val state = fightFunctionalCore(
//            state = initialState,
//            action = FightAction.SelectControlledActor(actorId = initialState.targetCreature.id)
//        )
//
//        assertThat(state).all {
//            prop(FightState::controlledActorId)
//                .isEqualTo(initialState.targetCreature.id)
//        }
//    }
//
//    @Test
//    fun `should do everything by controlled actor`() {
//        val initialState = normalFullState(controlledName = "Enemy", targetName = "Player")
//
//        val state = fightFunctionalCore(
//            state = initialState,
//            action = FightAction.SelectCommand(attackAction = AttackAction.Punch)
//        )
//
//        assertThat(state).all {
//            prop(FightState::actionLog)
//                .extracting(ActionEntry::text)
//                .containsExactly("Enemy punches Player's head with their right hand")
//        }
//    }

    @Test
    fun `should display weapon commands when selected attacker and target parts`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectSomething(
                creatureId = initialState.target.id,
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
        }
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
                creatureId = initialState.target.id,
                selectableId = initialState.targetRightHand.id
            )
        )
        val stateWithBothSelections = fightFunctionalCore(
            state = stateWithTargetHand,
            action = FightAction.SelectSomething(
                creatureId = initialState.attacker.id,
                selectableId = initialState.attackerLeftHand.id
            )
        )
        val state = fightFunctionalCore(
            state = stateWithBothSelections,
            action = FightAction.SelectCommand(attackAction = AttackAction.Punch)
        )

        assertThat(TestState.AttackWithItem(state))
            .all {
                prop(TestState.AttackWithItem::targetRightHand)
                    .prop(BodyPart::holding)
                    .isNull()
                prop(TestState.AttackWithItem::state)
                    .all {
                        prop(FightState::world)
                            .prop(World::ground)
                            .prop(Ground::items)
                            .containsExactly(initialState.targetWeapon)
                        prop(FightState::actionLog)
                            .extracting(ActionEntry::text)
                            .index(0)
                            .isEqualTo("$controlledActorName punches $targetActorName's right hand with their left hand. $targetActorName's knife is knocked out to the ground!")
                    }
            }
    }

    @Test
    fun `should select ground`() {
        val initialState = stateForItemAttack()

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectTarget(
                id = initialState.ground.id
            )
        )

        assertThat(TestState.AttackWithItem(state))
            .all {
                prop(TestState.AttackWithItem::state)
                    .prop(FightState::target)
                    .prop(Targetable::id)
                    .isEqualTo(initialState.ground.id)
            }
    }

    private fun stateForItemAttack(): TestState.AttackWithItem {
        val initialState = fightFunctionalCore(state = fightState(), action = FightAction.Init)

        val leftActor = initialState.actors.first().copy(name = "Player")
        val rightActor = initialState.actors.last().copy(name = "Enemy")
        val controlled = when (controlledActorName) {
            leftActor.name -> leftActor.id
            rightActor.name -> rightActor.id
            else -> leftActor.id
        }
        val target = when (controlled) {
            leftActor.id -> rightActor.id
            else -> leftActor.id
        }

        val ground = ground(id = 1L)

        val state = initialState.copy(
            world = initialState.world.copy(ground = ground),
            controlledActorId = controlled,
            targetId = target,
            selections = mapOf(
                leftActor.id to when (leftActor.id) {
                    controlled -> leftActor.bodyParts.find { it.name == "Right Hand" }!!.id
                    else -> leftActor.bodyParts.find { it.name == "Head" }!!.id
                },
                rightActor.id to when (rightActor.id) {
                    controlled -> rightActor.bodyParts.find { it.name == "Right Hand" }!!.id
                    else -> rightActor.bodyParts.find { it.name == "Head" }!!.id
                }
            )
        )

        return TestState.AttackWithItem(
            state = state,
        )
    }
}
