package com.daniil.shevtsov.fightsimulator.feature.coreshell.domain

import com.daniil.shevtsov.fightsimulator.core.navigation.Screen
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.fightState

data class GameState(
    val currentScreen: Screen,
    val screenStack: List<Screen>,
    val fightState: FightState,
)

fun gameState(
    currentScreen: Screen = Screen.Main,
    screenStack: List<Screen> = emptyList(),
    fightState: FightState = fightState(),
) = GameState(
    currentScreen = currentScreen,
    screenStack = screenStack,
    fightState = fightState,
)
