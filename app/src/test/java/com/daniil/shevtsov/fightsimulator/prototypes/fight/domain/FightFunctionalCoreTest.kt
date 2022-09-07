package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

internal class FightFunctionalCoreTest {

    @Test
    fun `should display initial state`() {
        val newState = fightFunctionalCore(
            state = fightState(),
            action = FightAction.Init
        )
        assertThat(newState).all {
            prop(FightState::controlledActorId).isEqualTo("Player")
            prop(FightState::actors)
                .each {
                    it.prop(Creature::bodyParts)
                        .extracting(BodyPart::name)
                        .containsExactly(
                            "Head",
                            "Skull",
                            "Body",
                            "Right Arm",
                            "Right Hand",
                            "Left Arm",
                            "Left Hand",
                            "Right Leg",
                            "Right Foot",
                            "Left Leg",
                            "Left Foot",
                        )
                }
            prop(FightState::selections).containsOnly("Player" to "Left Hand", "Enemy" to "Head")
            prop(FightState::availableCommands)
                .extracting(Command::attackAction)
                .containsExactly(AttackAction.Punch)
        }
    }

    @Test
    fun `should select player part when clicked`() {
        val initialState = normalFullState()
        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.controlledActorId,
                partName = initialState.controlledCreature.firstPart().name
            )
        )

        assertThat(state)
            .prop(FightState::selections)
            .contains(initialState.controlledActorId to initialState.controlledCreature.firstPart().name)
    }

    @Test
    fun `should not select slashed part when clicked`() {
        val leftHand = bodyPart(name = "Left Hand")
        val rightHand = bodyPart(name = "Right Hand")
        val initialState = normalFullState(
            bodyParts = listOf(leftHand, rightHand),
            controlledPartName = rightHand.name,
            targetPartName = leftHand.name,
            missingParts = listOf(leftHand.name)
        )
        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.controlledActorId,
                partName = leftHand.name
            )
        )

        assertThat(state)
            .prop(FightState::selections)
            .containsAll(
                initialState.controlledActorId to rightHand.name,
                initialState.targetCreature.id to leftHand.name,
            )
    }

    @Test
    fun `should display body part actions when selected player and enemy parts`() {
        val initialState = normalFullState()
        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.targetCreature.id,
                partName = initialState.targetCreature.firstPart().name
            )
        )

        assertThat(state)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .isEqualTo(initialState.controlledBodyPart.attackActions)
    }

    @Test
    fun `should display weapon commands when selected player and enemy parts`() {
        val initialState = stateForItemAttack(controlled = "Player")
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.state.targetCreature.id,
                partName = initialState.state.targetCreature.firstPart().name
            )
        )

        assertThat(state)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .containsExactly(
                AttackAction.Slash,
                AttackAction.Stab,
                AttackAction.Pommel,
                AttackAction.Throw,
            )
    }

    @Test
    fun `should add entry to log when command clicked`() {
        val initialState = normalFullState(controlled = "Player")
        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectCommand(attackAction = AttackAction.Punch)
        )

        assertThat(state)
            .prop(FightState::actionLog)
            .extracting(ActionEntry::text)
            .containsExactly("Player punches Enemy's head with their right hand")
    }

    @Test
    fun `should use item in log entry when command clicked`() {
        val initialState = stateForItemAttack(controlled = "Player")
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Stab)
        )

        assertThat(state)
            .prop(FightState::actionLog)
            .extracting(ActionEntry::text)
            .containsExactly("Player stabs Enemy's head with knife held by their right hand")
    }

    @Test
    fun `should move item from player to enemy when throwing`() {
        val testState = stateForItemAttack(
            controlled = "Player"
        )

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
                    it.prop(BodyPart::holding).isNotNull().prop(Item::name)
                        .isEqualTo(testState.heldItem.name)
                }
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("Player throws knife at Enemy's head with their right hand.\nThe knife has lodged firmly in the wound!")
        }
    }

    @Test
    fun `should throw by controlled actor`() {
        val testState = stateForItemAttack(
            controlled = "Enemy"
        )

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
                    it.prop(BodyPart::holding).isNotNull().prop(Item::name)
                        .isEqualTo(testState.heldItem.name)
                }
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("Enemy throws knife at Player's head with their right hand.\nThe knife has lodged firmly in the wound!")
        }
    }

    @Test
    fun `should remove limb when player is slashing`() {
        val initialState = stateForItemAttack(
            controlled = "Player"
        )
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Slash)
        )

        assertThat(state).all {
            prop(FightState::targetCreature)
                .prop(Creature::missingPartsSet)
                .containsOnly(initialState.state.targetBodyPart.name)
            prop(FightState::targetBodyPart)
                .prop(BodyPart::name)
                .isNotEqualTo(initialState.state.targetBodyPart.name)

            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("Player slashes at Enemy's head with knife held by their right hand.\nSevered head flies off in an arc!")
        }
    }

    @Test
    fun `should remove limb when enemy is slashing`() {
        val initialState = stateForItemAttack(controlled = "Enemy")
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Slash)
        )

        assertThat(state).all {
            prop(FightState::targetCreature)
                .prop(Creature::missingPartsSet)
                .containsOnly(initialState.state.targetBodyPart.name)
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo(
                    "Enemy slashes at Player's head with knife held by their right hand.\n" +
                            "Severed head flies off in an arc!"
                )
        }
    }

    @Test
    fun `should select who I am playing`() {
        val initialState = normalFullState()

        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectActor(actorId = initialState.targetCreature.id)
        )

        assertThat(state).all {
            prop(FightState::controlledActorId)
                .isEqualTo(initialState.targetCreature.id)
        }
    }

    @Test
    fun `should do everything by controlled actor`() {
        val initialState = normalFullState(controlled = "Enemy")

        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectCommand(attackAction = AttackAction.Punch)
        )

        assertThat(state).all {
            prop(FightState::actionLog)
                .extracting(ActionEntry::text)
                .containsExactly("Enemy punches Player's head with their right hand")
        }
    }

    @Test
    fun `attack at head should break skull`() {
        val initialState = stateForItemAttack(controlled = "Player")

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Pommel)
        )

        assertThat(state).all {
            prop(FightState::targetCreature)
                .prop(Creature::brokenPartsSet)
                .containsOnly(state.targetBodyPartBone?.name)
            prop(FightState::actionLog)
                .extracting(ActionEntry::text)
                .index(0)
                .isEqualTo("Player pommels Enemy's head with knife held by their right hand. The skull is fractured!")
        }
    }


    private fun stateForItemAttack(
        controlled: String,
    ): TestState.AttackWithItem {
        val left = "Player"
        val right = "Enemy"
        val knife = item(
            name = "Knife",
            attackActions = listOf(
                AttackAction.Slash,
                AttackAction.Stab,
                AttackAction.Pommel,
            )
        )
        val partWithKnife = bodyPart(
            name = "Right Hand",
            holding = knife
        )
        val skull = bodyPart(name = "Skull")
        val head = bodyPart(name = "Head", containedBodyParts = setOf(skull.name))
        val leftActor = creature(
            id = left, name = left, actor = Actor.Player, bodyParts = listOf(
                head,
                skull,
                partWithKnife,
            )
        )
        val rightActor = creature(
            id = right, name = right, actor = Actor.Enemy, bodyParts = listOf(
                head,
                skull,
                partWithKnife,
            )
        )

        val state = fightState(
            controlledActorId = controlled,
            actors = listOf(leftActor, rightActor),
            selections = mapOf(
                leftActor.id to when (leftActor.name) {
                    controlled -> partWithKnife.name
                    else -> head.name
                },
                rightActor.id to when (rightActor.name) {
                    controlled -> partWithKnife.name
                    else -> head.name
                }
            )
        )

        return TestState.AttackWithItem(
            state = state,
            heldItem = knife,
        )
    }

    sealed class TestState {
        data class AttackWithItem(val state: FightState, val heldItem: Item) : TestState()
    }

    fun normalFullState(
        controlled: String = "Player",
        bodyParts: List<BodyPart> = normalBody(),
        controlledPartName: String = "Right Hand",
        targetPartName: String = "Head",
        missingParts: List<String> = emptyList(),
    ): FightState {
        val player = creature(
            id = "Player",
            actor = Actor.Player,
            name = "Player",
            missingPartSet = missingParts.toSet(),
            bodyParts = bodyParts,
        )
        val enemy = creature(
            id = "Enemy",
            actor = Actor.Enemy,
            name = "Enemy",
            bodyParts = bodyParts
        )
        return fightState(
            controlledActorId = controlled,
            actors = listOf(player, enemy),
            selections = mapOf(
                player.id to when (controlled) {
                    player.id -> controlledPartName
                    else -> targetPartName
                },
                enemy.id to when (controlled) {
                    enemy.id -> controlledPartName
                    else -> targetPartName
                },
            )
        )
    }

    private fun normalBody(): List<BodyPart> {
        val head = bodyPart(name = "Head")
        val rightHand = bodyPart(name = "Right Hand", attackActions = listOf(AttackAction.Punch))
        val slashedLeftHand = bodyPart(name = "Left Hand")
        return listOf(head, rightHand, slashedLeftHand)
    }

}
