package com.daniil.shevtsov.fightsimulator.core.navigation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.FightViewState

sealed class ScreenViewState {
    object Loading : ScreenViewState()
    data class Main(val state: FightViewState): ScreenViewState()
}
