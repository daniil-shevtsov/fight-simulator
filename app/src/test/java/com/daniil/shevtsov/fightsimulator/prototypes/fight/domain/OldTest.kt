package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

internal class OldTest {

    @Test
    fun `should display initial state`() {
        val newState = fightFunctionalCore(
            state = fightState(),
            action = FightAction.Init
        )
        assertThat(newState).all {
            prop(FightState::lastSelectedControlledPartId)
                .isEqualTo(newState.controlledCreatureBodyParts.find { it.name == "Left Hand" }!!.id)
            prop(FightState::controlledCreatureBodyParts)
                .transform { bodyParts -> bodyParts.map { bodyPart -> bodyPart.name to bodyParts.find { it.id == bodyPart.parentPartId }?.name } }
                .containsAll(
                    "Head" to "Body",
                    "Skull" to null,
                    "Body" to null,
                    "Right Arm" to "Body",
                    "Right Hand" to "Right Arm",
                    "Left Arm" to "Body",
                    "Left Hand" to "Left Arm",
                    "Right Leg" to "Body",
                    "Right Foot" to "Right Leg",
                    "Left Leg" to "Body",
                    "Left Foot" to "Left Leg",
                )
            val head = newState.targetCreatureBodyParts.find { it.name == "Head" }!!
            val skull = newState.targetCreatureBodyParts.find { it.name == "Skull" }!!
            assertThat(head).prop(BodyPart::containedBodyParts).containsOnly(skull.id)
            assertThat(skull).prop(BodyPart::containerPartId).isNotNull().isEqualTo(head.id)

            prop(FightState::controlledBodyPart).prop(BodyPart::name).isEqualTo("Left Hand")
            prop(FightState::targetBodyPart).prop(BodyPart::name).isEqualTo("Head")
            prop(FightState::availableCommands)
                .extracting(Command::attackAction)
                .containsExactly(AttackAction.Punch)
        }
    }
}
