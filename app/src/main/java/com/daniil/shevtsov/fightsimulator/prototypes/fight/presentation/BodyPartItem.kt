package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Item
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.bodyPartId

data class BodyPartItem(
    val id: BodyPartId,
    val name: String,
    val holding: Item?,
    val contained:  Set<BodyPartId>,
    val statuses: List<BodyPartStatus>,
    val isSelected: Boolean,
)

fun bodyPartItem(
    id: Long,
    name: String = "",
    holding: Item? = null,
    contained:  Set<BodyPartId> = setOf(),
    statuses: List<BodyPartStatus> = emptyList(),
    isSelected: Boolean = false,
) = BodyPartItem(
    id = bodyPartId(id),
    name = name,
    holding = holding,
    contained = contained,
    statuses = statuses,
    isSelected = isSelected,
)
