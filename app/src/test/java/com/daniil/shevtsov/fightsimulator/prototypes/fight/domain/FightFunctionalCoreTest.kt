package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.Assert
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
                        .containsAll(
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
            val head = newState.targetCreature.bodyParts.find { it.name == "Head" }!!
            val skull = newState.targetCreature.bodyParts.find { it.name == "Skull" }!!
            assertThat(head).prop(BodyPart::containedBodyParts).containsOnly(skull.id)
            assertThat(skull).prop(BodyPart::parentId).isNotNull().isEqualTo(head.id)

            prop(FightState::controlledBodyPart).prop(BodyPart::name).isEqualTo("Left Hand")
            prop(FightState::targetBodyPart).prop(BodyPart::name).isEqualTo("Head")
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
                partId = initialState.controlledCreature.firstPart().id,
            )
        )

        assertThat(state)
            .controlledBodyPartName()
            .isEqualTo(initialState.controlledCreature.firstPart().name)
    }

    private fun Assert<FightState>.controlledBodyPartName() = prop(FightState::controlledBodyPart)
        .prop(BodyPart::name)

    @Test
    fun `should not select slashed part when clicked`() {
        val leftHand = bodyPart(id = 0L, name = "Left Hand")
        val rightHand = bodyPart(id = 1L, name = "Right Hand")
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
                partId = leftHand.id,
            )
        )

        assertThat(state)
            .prop(FightState::selections)
            .containsAll(
                initialState.controlledActorId to rightHand.id,
                initialState.targetCreature.id to leftHand.id,
            )
    }

    @Test
    fun `should display body part actions when selected player and enemy parts`() {
        val initialState = normalFullState()
        val state = fightFunctionalCore(
            state = initialState,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.targetCreature.id,
                partId = initialState.targetCreature.firstPart().id,
            )
        )

        assertThat(state)
            .prop(FightState::availableCommands)
            .extracting(Command::attackAction)
            .isEqualTo(initialState.controlledBodyPart.attackActions)
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
            missingPartSet = bodyParts.filter { it.name in missingParts }.map { it.id }.toSet(),
            bodyParts = bodyParts,
        )
        val enemy = creature(
            id = "Enemy",
            actor = Actor.Enemy,
            name = "Enemy",
            bodyParts = bodyParts
        )
        val controlledPartId = bodyParts.find { it.name == controlledPartName }?.id
        val targetPartId = bodyParts.find { it.name == targetPartName }?.id
        return fightState(
            controlledActorId = controlled,
            actors = listOf(player, enemy),
            selections = mapOf(
                player.id to when (controlled) {
                    player.id -> controlledPartId!!
                    else -> targetPartId!!
                },
                enemy.id to when (controlled) {
                    enemy.id -> controlledPartId!!
                    else -> targetPartId!!
                },
            )
        )
    }

    private fun normalBody(): List<BodyPart> {
        val head = bodyPart(id = 0L, name = "Head")
        val rightHand =
            bodyPart(id = 1L, name = "Right Hand", attackActions = listOf(AttackAction.Punch))
        val slashedLeftHand = bodyPart(id = 2L, name = "Left Hand")
        return listOf(head, rightHand, slashedLeftHand)
    }

}
