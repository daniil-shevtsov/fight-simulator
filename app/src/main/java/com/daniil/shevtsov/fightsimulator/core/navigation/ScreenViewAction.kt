package com.daniil.shevtsov.fightsimulator.core.navigation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction

sealed class ScreenViewAction {
    data class General(val action: GeneralViewAction) : ScreenViewAction()
    data class Fight(val action: FightAction) : ScreenViewAction()
}
