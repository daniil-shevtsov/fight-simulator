package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.BodyPartStatus
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Selectable
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.SelectableId

sealed interface SelectableItem {

    abstract val id: SelectableId
    abstract val name: String

    data class BodyPartItem(
        override val id: BodyPartId,
        override val name: String,
        val holding: Selectable?,
        val contained: Set<BodyPartId>,
        val lodgedIn: Set<Selectable>,
        val statuses: List<BodyPartStatus>,
        val canGrab: Boolean,
        val isSelected: Boolean,
    ) : SelectableItem

    data class Item(
        override val id: BodyPartId,
        override val name: String,
    ) : SelectableItem

}
