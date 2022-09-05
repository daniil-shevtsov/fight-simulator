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

    private fun fullNormalState(): FightState {
        val playerBodyPart = bodyPart(name = "lol", attackActions = listOf(AttackAction.Strike))
        val skull = bodyPart(name = "skull")
        val enemyBodyPart = bodyPart(name = "kek", containedBodyParts = setOf(skull.name))
        val missingBodyPart = bodyPart(name = "cheburek")
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
                player.id to playerBodyPart.name,
                enemy.id to enemyBodyPart.name,
            ),
            actors = listOf(player, enemy),
        )
    }
}
