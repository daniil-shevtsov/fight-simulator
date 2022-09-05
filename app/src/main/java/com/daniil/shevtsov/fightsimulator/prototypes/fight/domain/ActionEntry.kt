package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class ActionEntry(
    val text: String,
)

fun actionEntry(
    text: String = "",
) = ActionEntry(
    text = text,
)
