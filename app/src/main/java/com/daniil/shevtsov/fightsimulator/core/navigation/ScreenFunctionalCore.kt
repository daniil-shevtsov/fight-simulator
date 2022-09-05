package com.daniil.shevtsov.fightsimulator.core.navigation

import com.daniil.shevtsov.fightsimulator.feature.coreshell.domain.GameState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.fightFunctionalCore

fun screenFunctionalCore(
    state: GameState,
    viewAction: ScreenViewAction,
): GameState {
    return when (viewAction) {
        is ScreenViewAction.General -> generalFunctionalCore(
            state = state,
            viewAction = viewAction.action,
        )
        is ScreenViewAction.Fight -> state.copy(
            fightState = fightFunctionalCore(
                state = state.fightState,
                action = viewAction.action,
            )
        )
    }
}
