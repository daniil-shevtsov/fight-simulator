package com.daniil.shevtsov.fightsimulator.core.navigation

import com.daniil.shevtsov.fightsimulator.feature.coreshell.domain.GameState

fun generalFunctionalCore(
    state: GameState,
    viewAction: GeneralViewAction,
): GameState = when (viewAction) {
    is GeneralViewAction.Open -> {
        val newStack = when {
            viewAction.shouldReplace -> state.screenStack.dropLast(1) + listOf(viewAction.screen)
            else -> state.screenStack + listOf(viewAction.screen)
        }
        state.copy(
            currentScreen = viewAction.screen,
            screenStack = newStack,
        )
    }
    is GeneralViewAction.Back -> {
        if (state.screenStack.size > 1) {
            val newScreenStack = state.screenStack.dropLast(1)
            val newCurrentScreen = newScreenStack.last()
            state.copy(
                currentScreen = newCurrentScreen,
                screenStack = newScreenStack,
            )
        } else {
            state
        }
    }
}
