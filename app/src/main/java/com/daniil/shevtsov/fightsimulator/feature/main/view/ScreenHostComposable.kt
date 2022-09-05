package com.daniil.shevtsov.fightsimulator.feature.main.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.daniil.shevtsov.fightsimulator.core.navigation.GeneralViewAction
import com.daniil.shevtsov.fightsimulator.core.navigation.ScreenHostViewModel
import com.daniil.shevtsov.fightsimulator.core.navigation.ScreenViewAction
import com.daniil.shevtsov.fightsimulator.core.navigation.ScreenViewState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.ui.FightScreen


@Composable
fun ScreenHostComposable(
    viewModel: ScreenHostViewModel
) {
    val delegatedViewState by viewModel.state.collectAsState()

    BackHandler {
        viewModel.handleAction(ScreenViewAction.General(GeneralViewAction.Back))
    }

    when (val viewState = delegatedViewState) {
        is ScreenViewState.Main -> {
            FightScreen(
                state = viewState.state,
                onAction = { action -> viewModel.handleAction(ScreenViewAction.Fight(action)) }
            )
        }
        ScreenViewState.Loading -> Unit
    }
}
