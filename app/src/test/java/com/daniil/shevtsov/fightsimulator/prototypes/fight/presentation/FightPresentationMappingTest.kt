package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.*
import org.junit.jupiter.api.Test

internal class FightPresentationMappingTest {
    @Test
    fun `should map state`() {
        val initialState = attackWithItemTestState()
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
                            .extracting(SelectableItem::id)
                            .containsAll(initialState.attackerRightHand.id)
                        index(1)
                            .prop(CreatureMenu::bodyParts)
                            .extracting(SelectableItem::id)
                            .containsAll(
                                initialState.targetHead.id,
                                initialState.targetSkull.id,
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
        val initialState = attackWithItemTestState()
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(0)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(SelectableItem::name, SelectableItem::isSelected)
                    .containsAll(initialState.state.controlledBodyPart.name to true)
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .extracting(SelectableItem::name, SelectableItem::isSelected)
                    .containsAll(
                        initialState.targetHead.name to true,
                        initialState.targetSkull.name to false,
                    )
            }
    }

    @Test
    fun `should display body part statuses`() {
        val initialState = attackWithItemTestState()
        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .transform { it.filterIsInstance<SelectableItem.BodyPartItem>() }
                    .extracting(SelectableItem::id, SelectableItem.BodyPartItem::statuses)
                    .containsAll(
                        initialState.targetSkull.id to listOf(
                            BodyPartStatus.Broken
                        )
                    )
            }
    }

    @Test
    fun `should display as selected only functional body parts`() {
        val initialState = attackWithItemTestState().let { state ->
            state.copy(
                state = state.state.copy(
                    lastSelectedTargetPartId = state.targetRightHand.id,
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
                    .extracting(SelectableItem::name, SelectableItem::isSelected)
                    .containsAll(
                        initialState.targetHead.name to true,
                        initialState.targetSkull.name to false,
                    )
            }
    }

    @Test
    fun `should display ground as target`() {
        val initialState = attackWithItemTestState().let { state ->
            state.copy(
                state = state.state.copy(
                    lastSelectedTargetHolderId = state.ground.id,
                    lastSelectedTargetPartId = state.ground.selectableIds.first(),
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
    fun `should display items on the ground`() {
        val initialState = attackWithItemTestState()

        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::ground)
            .prop(GroundMenu::selectables)
            .extracting(SelectableItem::name)
            .contains("Spear")
    }

    @Test
    fun `should display body parts on the ground`() {
        val initialState = slashedTestState()

        val viewState = fightPresentationMapping(
            state = initialState.state
        )

        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::ground)
            .prop(GroundMenu::selectables)
            .transform { it.filterIsInstance<SelectableItem.BodyPartItem>() }
            .extracting(SelectableItem::name)
            .containsExactly("Head")
    }

    @Test
    fun `should display enemy as target`() {
        val initialState = attackWithItemTestState()
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
        val initialState = attackWithItemTestState()
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
    fun `should display lodged in item`() {
        val initialState = attackWithItemTestState()
        val finalState = initialState.state.let { state ->
            fightFunctionalCore(
                state,
                FightAction.SelectSomething(
                    state.controlledCreature.id,
                    initialState.attackerRightHand.id
                )
            )
        }.let { state ->
            fightFunctionalCore(state, FightAction.SelectCommand(AttackAction.Throw))
        }

        val viewState = fightPresentationMapping(
            state = finalState
        )
        assertThat(viewState)
            .isInstanceOf(FightViewState.Content::class)
            .prop(FightViewState.Content::actors)
            .all {
                index(1)
                    .prop(CreatureMenu::bodyParts)
                    .transform { it.filterIsInstance<SelectableItem.BodyPartItem>() }
                    .extracting(
                        SelectableItem.BodyPartItem::id,
                        SelectableItem.BodyPartItem::lodgedIn
                    )
                    .contains(
                        initialState.targetHead.id to listOf(
                            selectableItem(
                                id = initialState.attackerWeapon.id.raw,
                                name = initialState.attackerWeapon.name,
                            )
                        )
                    )
            }
    }

    private fun attackWithItemTestState(): AttackWithItemTestState {
        val originalState = AttackWithItemTestState(
            state = fightFunctionalCore(
                state = fightState(),
                action = FightAction.Init
            )
        )

        val modifiedAttacker = originalState.attacker
        val modifiedTarget = originalState.target.copy(
            missingPartsSet = setOf(originalState.targetRightHand.id),
            bodyPartIds = originalState.target.bodyPartIds - originalState.targetRightHand.id,
        )
        val spear = item(id = 153L, name = "Spear")
        val modifiedGround = originalState.ground.copy(
            selectableIds = listOf(spear.id)
        )

        return originalState.copy(
            state = originalState.state.copy(
                allSelectables = (originalState.state.allSelectables.values.map { selectable ->
                    when {
                        selectable is BodyPart && selectable.id == originalState.targetSkull.id -> selectable.copy(
                            statuses = selectable.statuses + BodyPartStatus.Broken
                        )
                        else -> selectable
                    }
                } + listOf(spear)).associateBy { it.id },
                selectableHolders = listOf(modifiedAttacker, modifiedTarget, modifiedGround).associateBy { it.id },
            )
        )
    }

    private fun slashedTestState(): AttackWithItemTestState {
        val initial = createInitialStateWithControlled(actorName = "Player")
        val slashed = fightFunctionalCore(
            state = initial,
            action = FightAction.SelectCommand(AttackAction.Slash)
        )

        return AttackWithItemTestState(
            state = slashed
        )
    }
}
