package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

interface FightWeaponTest {
    
    val controlledActorName: String
    val targetActorName: String
    
    @Test
    fun `should display weapon commands when selected attacker and target parts`() {
        val initialState = stateForItemAttack()
        val state = fightFunctionalCore(
            state = initialState.state,
            action = FightAction.SelectBodyPart(
                creatureId = initialState.target.id,
                partId = initialState.targetHead.id,
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
                        .isEqualTo(testState.weapon.name)
                }
            prop(FightState::actionLog)
                .index(0)
                .prop(ActionEntry::text)
                .isEqualTo("$controlledActorName throws knife at $targetActorName's head with their right hand.\nThe knife has lodged firmly in the wound!")
        }
    }

    @Test
    fun `should throw by controlled actor`() {
        val testState = stateForItemAttack( )

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
                        .isEqualTo(testState.weapon.name)
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

    private fun stateForItemAttack(): TestState.AttackWithItem {
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
            controlledActorId = controlledActorName,
            actors = listOf(leftActor, rightActor),
            selections = mapOf(
                leftActor.id to when (leftActor.name) {
                    controlledActorName -> partWithKnife.id
                    else -> head.id
                },
                rightActor.id to when (rightActor.name) {
                    controlledActorName -> partWithKnife.id
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
