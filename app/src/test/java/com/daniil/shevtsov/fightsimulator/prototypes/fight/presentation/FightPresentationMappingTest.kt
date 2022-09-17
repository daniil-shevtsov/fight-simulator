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
                            .extracting(BodyPartItem::id)
                            .containsAll(initialState.attackerRightHand.id)
                        index(1)
                            .prop(CreatureMenu::bodyParts)
                            .extracting(BodyPartItem::id)
                            .containsAll(
                                initialState.targetHead.id,
                                initialState.targetSkull.id,
                                initialState.targetRightHand.id,
                            )
                    }
                prop(FightViewState.Content::commandsMenu)
                    .prop(CommandsMenu::commands)
                    .extracting(CommandItem::name)
                    .containsExactly(initialState.attackerRightHand.attackActions.first().name)
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
                        initialState.targetHead.name to true,
                        initialState.targetSkull.name to false,
                        initialState.targetRightHand.name to false,
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
            state.copy(
                state = state.state.copy(
                    lastSelectedTargetPartId = state.state.targetCreature.missingParts.first().id,
                )

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

    @Test
    fun `should display ground as target`() {
        val initialState = fullNormalState().let { state ->
            state.copy(
                state = state.state.copy(
                    lastSelectedTargetHolderId = state.ground.id,
                    lastSelectedTargetPartId = state.ground.selectables.first().id,
                )
            )
        }
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::ground)
            .prop(GroundMenu::isSelected)
            .isTrue()
    }

    @Test
    fun `should display enemy as target`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::isTarget)
                    .isTrue()
            }
    }

    @Test
    fun `should display part as selected after changing`() {
        val initialState = fullNormalState()
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::isTarget)
                    .isTrue()
            }
    }

    private fun fullNormalState(): AttackWithItemTestState {
        val originalState = AttackWithItemTestState(
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
        val spear = item(id = 1L, name = "Spear")
        val modifiedGround = originalState.ground.copy(
            selectables = listOf(spear)
        )

        return originalState.copy(
            state = originalState.state.copy(
                actors = listOf(modifiedAttacker, modifiedTarget),
                world = originalState.state.world.copy(ground = modifiedGround)
            )
        )
    }
}
