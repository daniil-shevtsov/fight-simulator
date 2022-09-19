package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.SelectableId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.itemId

sealed interface SelectableItem {

    val id: SelectableId
    val name: String
    val isSelected: Boolean

    data class BodyPartItem(
        override val id: BodyPartId,
        override val name: String,
        override val isSelected: Boolean,
        val holding: SelectableItem?,
        val contained: Set<BodyPartId>,
        val lodgedIn: Set<SelectableItem>,
        val statuses: List<BodyPartStatus>,
        val canGrab: Boolean,
    ) : SelectableItem

    data class Item(
        override val id: SelectableId,
        override val name: String,
        override val isSelected: Boolean,
    ) : SelectableItem

}

fun selectableItem(
    id: Long = 0L,
    name: String = "",
    isSelected: Boolean = false,
) = SelectableItem.Item(
    id = itemId(id),
    name = name,
    isSelected = isSelected,
)
