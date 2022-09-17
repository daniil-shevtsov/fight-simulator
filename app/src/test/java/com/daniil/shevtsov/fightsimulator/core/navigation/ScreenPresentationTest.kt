package com.daniil.shevtsov.fightsimulator.core.navigation

import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.daniil.shevtsov.fightsimulator.feature.coreshell.domain.gameState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.fightFunctionalCore
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.fightState
import org.junit.jupiter.api.Test

internal class ScreenPresentationTest {
    @Test
    fun `should form main view state when main screen selected`() {
        val state = gameState(
            currentScreen = Screen.Main,
            fightState = fightFunctionalCore(fightState(), FightAction.Init)
        )

        val viewState = screenPresentationFunctionalCore(state = state)

        assertThat(viewState).isInstanceOf(ScreenViewState.Main::class)
    }
}
