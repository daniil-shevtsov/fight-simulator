package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

sealed class FightViewState {
    object Loading : FightViewState()

    data class Content(
        val actors: List<CreatureMenu>,
        val ground: GroundMenu,
        val commandsMenu: CommandsMenu,
        val actionLog: List<ActionEntryModel>,
    ) : FightViewState()
}
