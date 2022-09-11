package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.*
import org.junit.jupiter.api.Test

internal class FightPresentationMappingTest {
    @Test
    fun `should map state`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .all {
                prop(FightViewState.Content::actors)
                    .all {
                        index(0)
                            .prop(CreatureMenu::bodyParts)
                            .extracting(BodyPartItem::name)
                            .containsExactly(initialState.controlledBodyPart.name)
                        index(1)
                            .prop(CreatureMenu::bodyParts)
                            .extracting(BodyPartItem::name)
                            .containsExactly(
                                initialState.targetBodyPart.name,
                                "skull",
                                initialState.targetCreature.missingParts.first().name,
                            )
                    }
                prop(FightViewState.Content::commandsMenu)
                    .prop(CommandsMenu::commands)
                    .extracting(CommandItem::name)
                    .containsExactly(initialState.controlledBodyPart.attackActions.first().name)
            }
    }

    @Test
    fun `should display selected player part`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(0)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::isSelected)
                    .containsExactly(initialState.controlledBodyPart.name to true)
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::isSelected)
                    .containsExactly(
                        initialState.targetBodyPart.name to true,
                        "skull" to false,
                        initialState.targetCreature.missingParts.first().name to false,
                    )
            }
    }

    @Test
    fun `should display body part statuses`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::statuses)
                    .containsAll(
                        initialState.targetCreature.missingParts.first().name to listOf(
                            BodyPartStatus.Missing
                        ),
                        initialState.targetCreature.brokenParts.first().name to listOf(
                            BodyPartStatus.Broken
                        ),
                    )
            }
    }

    @Test
    fun `should display as selected only functional body parts`() {
        val initialState = fullNormalState().let { state ->
            state.copy(
                selections = mapOf(
                    state.targetCreature.id to state.targetCreature.missingParts.first().id
                )
            )
        }
        val viewState = fightPresentationMapping(
            state = initialState
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::isSelected)
                    .containsAll(
                        initialState.targetCreature.functionalParts.first().name to true,
                        initialState.targetCreature.missingParts.first().name to false,
                    )
            }
    }

    private fun fullNormalState(): FightState {
        val playerBodyPart = bodyPart(id = 0L, name = "hand", attackActions = listOf(AttackAction.Strike))
        val skull = bodyPart(id = 1L, name = "skull")
        val enemyBodyPart = bodyPart(id = 2L, name = "head", containedBodyParts = setOf(skull.name))
        val missingBodyPart = bodyPart(id = 3L, name = "hand")
        val player =
            creature(
                id = "playerId",
                name = "Player",
                actor = Actor.Player,
                bodyParts = listOf(playerBodyPart)
            )
        val enemy = creature(
            id = "enemyId",
            name = "Enemy",
            actor = Actor.Enemy,
            bodyParts = listOf(enemyBodyPart, skull, missingBodyPart),
            missingPartSet = setOf(missingBodyPart.name),
            brokenPartSet = setOf(skull.name)
        )

        return fightState(
            controlledActorId = player.id,
            selections = mapOf(
                player.id to playerBodyPart.id,
                enemy.id to enemyBodyPart.id,
            ),
            actors = listOf(player, enemy),
        )
    }
}
