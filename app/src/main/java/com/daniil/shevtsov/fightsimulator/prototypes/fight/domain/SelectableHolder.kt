package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface SelectableHolder {
    val id: SelectableHolderId

    val selectables: List<Selectable>
    val selectableIds: List<SelectableId>
}
