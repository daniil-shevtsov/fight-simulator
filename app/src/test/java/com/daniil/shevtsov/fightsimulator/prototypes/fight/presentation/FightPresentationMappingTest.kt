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
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .all {
                prop(FightViewState.Content::actors)
                    .all {
                        index(0)
                            .prop(CreatureMenu::bodyParts)
                            .extracting(BodyPartItem::name)
                            .containsAll(initialState.state.controlledBodyPart.name)
                        index(1)
                            .prop(CreatureMenu::bodyParts)
                            .extracting(BodyPartItem::name)
                            .containsAll(
                                initialState.state.targetBodyPart.name,
                                "Skull",
                                initialState.state.targetCreature.missingParts.first().name,
                            )
                    }
                prop(FightViewState.Content::commandsMenu)
                    .prop(CommandsMenu::commands)
                    .extracting(CommandItem::name)
                    .containsExactly(initialState.state.controlledBodyPart.attackActions.first().name)
            }
    }

    @Test
    fun `should display selected player part`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(0)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::isSelected)
                    .containsAll(initialState.state.controlledBodyPart.name to true)
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::isSelected)
                    .containsAll(
                        initialState.state.targetBodyPart.name to true,
                        "Skull" to false,
                        initialState.state.targetCreature.missingParts.first().name to false,
                    )
            }
    }

    @Test
    fun `should display body part statuses`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::statuses)
                    .containsAll(
                        initialState.state.targetCreature.missingParts.first().name to listOf(
                            BodyPartStatus.Missing
                        ),
                        initialState.state.targetCreature.brokenParts.first().name to listOf(
                            BodyPartStatus.Broken
                        ),
                    )
            }
    }

    @Test
    fun `should display as selected only functional body parts`() {
        val initialState = fullNormalState().let { state ->
            state.copy(state = state.state.copy(
                selections = mapOf(
                    state.state.targetCreature.id to state.state.targetCreature.missingParts.first().id
                ))

            )
        }
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(BodyPartItem::name, BodyPartItem::isSelected)
                    .containsAll(
                        initialState.state.targetCreature.functionalParts.first().name to true,
                        initialState.state.targetCreature.missingParts.first().name to false,
                    )
            }
    }

    private fun fullNormalState(): TestState.AttackWithItem {
        val originalState = TestState.AttackWithItem(
            state = fightFunctionalCore(
                state = fightState(),
                action = FightAction.Init
            )
        )

        val modifiedAttacker = originalState.attacker
        val modifiedTarget = originalState.target.copy(
            missingPartsSet = setOf(originalState.targetRightHand.id),
            brokenPartsSet = setOf(originalState.targetSkull.id),
        )

        return originalState.copy(
            state = originalState.state.copy(
                actors = listOf(modifiedAttacker, modifiedTarget)
            )
        )
    }
}
