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
            prop(FightState::controlledActorId)
                .isEqualTo(newState.controlledCreature.id)
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



}
