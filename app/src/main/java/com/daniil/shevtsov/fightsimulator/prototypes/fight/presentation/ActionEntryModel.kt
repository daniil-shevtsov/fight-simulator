package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

data class ActionEntryModel(
    val text: String,
)

fun actionEntryModel(text: String) = ActionEntryModel(text = text)
