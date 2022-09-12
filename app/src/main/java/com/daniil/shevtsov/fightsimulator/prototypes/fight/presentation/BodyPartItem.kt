package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Item
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.SelectableId

data class BodyPartItem(
    val id: SelectableId.BodyPart,
    val name: String,
    val holding: Item?,
    val contained:  Set<SelectableId.BodyPart>,
    val statuses: List<BodyPartStatus>,
    val isSelected: Boolean,
)

fun bodyPartItem(
    id: Long,
    name: String = "",
    holding: Item? = null,
    contained:  Set<SelectableId.BodyPart> = setOf(),
    statuses: List<BodyPartStatus> = emptyList(),
    isSelected: Boolean = false,
) = BodyPartItem(
    id = SelectableId.BodyPart(id),
    name = name,
    holding = holding,
    contained = contained,
    statuses = statuses,
    isSelected = isSelected,
)
