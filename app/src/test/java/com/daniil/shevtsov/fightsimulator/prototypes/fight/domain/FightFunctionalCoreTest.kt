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



    fun normalFullState(
        controlledName: String = "Player",
        targetName: String = "Enemy",
        bodyParts: List<BodyPart> = normalBody(),
        controlledPartName: String = "Right Hand",
        targetPartName: String = "Head",
        missingParts: List<String> = emptyList(),
    ): FightState {
        val player = creature(
            id = creatureId(0L).raw,
            actor = Actor.Player,
            name = "Player",
            missingPartSet = bodyParts.filter { it.name in missingParts }.map { it.id }.toSet(),
            bodyParts = bodyParts,
        )
        val enemy = creature(
            id = creatureId(1L).raw,
            actor = Actor.Enemy,
            name = "Enemy",
            bodyParts = bodyParts
        )
        val controlled = when (controlledName) {
            player.name -> player.id
            else -> enemy.id
        }
        val target = when (targetName) {
            player.name -> player.id
            else -> enemy.id
        }
        val controlledPartId = bodyParts.find { it.name == controlledPartName }?.id
        val targetPartId = bodyParts.find { it.name == targetPartName }?.id
        return fightState(
            controlledActorId = controlled,
            targetId = target,
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
