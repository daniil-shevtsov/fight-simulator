package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.fightFunctionalCore
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.fightState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.FightViewState
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.fightPresentationMapping
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class FightImperativeShell @Inject constructor() : ViewModel() {
    private val state: MutableStateFlow<FightState?> = MutableStateFlow(null)
    private val viewActionFlow = MutableSharedFlow<FightAction>()

    val viewState = MutableStateFlow<FightViewState>(FightViewState.Loading)

    init {
        state
            .filterNotNull()
            .map { state -> fightPresentationMapping(state) }
            .onEach { viewState.value = it }
            .launchIn(viewModelScope)

        viewActionFlow
            .onStart { emit(FightAction.Init) }
            .onEach { action ->
                val newState = fightFunctionalCore(
                    state = state.value ?: fightState(),
                    action = action,
                )

                state.value = newState
            }
            .launchIn(viewModelScope)
    }

    fun handleAction(action: FightAction) {
        viewModelScope.launch { viewActionFlow.emit(action) }
    }

}
