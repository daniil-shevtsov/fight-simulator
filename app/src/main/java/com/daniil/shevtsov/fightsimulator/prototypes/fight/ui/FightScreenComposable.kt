package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FightScreenComposable(imperativeShell: FightImperativeShell) {
    val viewState by imperativeShell.viewState.collectAsState()
    FightScreen(state = viewState, onAction = { action -> imperativeShell.handleAction(action) })
}
