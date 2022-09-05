package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Item

data class BodyPartItem(
    val name: String,
    val holding: Item?,
    val contained: Set<String>,
    val statuses: List<BodyPartStatus>,
    val isSelected: Boolean,
)

fun bodyPartItem(
    name: String = "",
    holding: Item? = null,
    contained: Set<String> = setOf(),
    statuses: List<BodyPartStatus> = emptyList(),
    isSelected: Boolean = false,
) = BodyPartItem(
    name = name,
    holding = holding,
    contained = contained,
    statuses = statuses,
    isSelected = isSelected,
)
