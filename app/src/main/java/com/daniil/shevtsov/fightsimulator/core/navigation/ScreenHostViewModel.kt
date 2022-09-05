package com.daniil.shevtsov.fightsimulator.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniil.shevtsov.fightsimulator.feature.main.data.MainImperativeShell
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenHostViewModel @Inject constructor(
    private val imperativeShell: MainImperativeShell,
) : ViewModel() {

    private val _state =
        MutableStateFlow<ScreenViewState>(ScreenViewState.Loading)
    val state = _state.asStateFlow()

    private val viewActionFlow = MutableSharedFlow<ScreenViewAction>()

    init {
        viewActionFlow
            .onStart { emit(ScreenViewAction.Fight(FightAction.Init)) }
            .onEach { viewAction ->
                val newState = screenFunctionalCore(
                    state = imperativeShell.getState(),
                    viewAction = viewAction,
                )

                imperativeShell.updateState(newState)
            }
            .launchIn(viewModelScope)

        imperativeShell.observeState()
            .onEach { state ->
                _state.value = screenPresentationFunctionalCore(state = state)
            }
            .launchIn(viewModelScope)
    }

    fun handleAction(action: ScreenViewAction) {
        viewModelScope.launch {
            viewActionFlow.emit(action)
        }
    }

}
