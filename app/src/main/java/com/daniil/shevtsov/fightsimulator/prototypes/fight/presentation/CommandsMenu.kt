package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

data class CommandsMenu(
    val commands: List<CommandItem>,
)

fun commandsMenu(commands: List<CommandItem> = emptyList()) = CommandsMenu(commands = commands)
