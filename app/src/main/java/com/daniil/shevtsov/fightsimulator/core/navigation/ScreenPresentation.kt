package com.daniil.shevtsov.fightsimulator.core.navigation

import com.daniil.shevtsov.fightsimulator.feature.coreshell.domain.GameState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.fightPresentationMapping

fun screenPresentationFunctionalCore(
    state: GameState
): ScreenViewState {
    return when (state.currentScreen) {
        Screen.Main -> ScreenViewState.Main(fightPresentationMapping(state.fightState))
        Screen.FinishedGame -> ScreenViewState.Main(fightPresentationMapping(state.fightState))
    }
}
