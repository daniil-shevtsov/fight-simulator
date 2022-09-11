package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

class FightSlashingTest {

    @Test
    fun `should display weapon commands when selected player and enemy parts`() {
        val initialState = stateForItemAttack(controlled = "Player")
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.state.targetCreature.id,
                partId = initialState.state.targetCreature.firstPart().id,
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
    fun `should remove limb and contained parts when player is slashing`() {
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
                .containsAll(
                    initialState.state.targetBodyPart.id,
                    initialState.state.targetCreature.bodyParts.find { it.id == initialState.state.targetBodyPart.containedBodyParts.first() }!!.id
                )
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
                .containsAll(
                    initialState.state.targetBodyPart.id,
                    initialState.state.targetCreature.bodyParts.find { it.id == initialState.state.targetBodyPart.containedBodyParts.first() }!!.id
                )
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
    fun `attack at head should break skull`() {
        val initialState = stateForItemAttack(controlled = "Player")

        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectCommand(attackAction = AttackAction.Pommel)
        )

        assertThat(state).all {
            prop(FightState::targetCreature)
                .prop(Creature::brokenPartsSet)
                .containsOnly(state.targetBodyPartBone?.id)
            prop(FightState::actionLog)
                .extracting(ActionEntry::text)
                .index(0)
                .isEqualTo("Player pommels Enemy's head with knife held by their right hand. The skull is fractured!")
        }
    }

    sealed class TestState {
        data class AttackWithItem(
            val state: FightState,
            val heldItem: Item
        ) : TestState() {
            val attacker: Creature
                get() = state.controlledCreature
            val target: Creature
                get() = state.targetCreature
            val weapon: Item
                get() = attacker.functionalParts.find { it.holding != null }?.holding ?: heldItem
            val targetHead: BodyPart
                get() = target.bodyParts.find { it.name == "Head" }!!
            val targetSkull: BodyPart
                get() = target.bodyParts.find { it.name == "Skull" }!!
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
            id = 0L,
            name = "Right Hand",
            holding = knife
        )
        val skull = bodyPart(id = 1L, name = "Skull")
        val head = bodyPart(id = 2L, name = "Head", containedBodyParts = setOf(skull.id))
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
                    controlled -> partWithKnife.id
                    else -> head.id
                },
                rightActor.id to when (rightActor.name) {
                    controlled -> partWithKnife.id
                    else -> head.id
                }
            )
        )

        return TestState.AttackWithItem(
            state = state,
            heldItem = knife,
        )
    }

}
