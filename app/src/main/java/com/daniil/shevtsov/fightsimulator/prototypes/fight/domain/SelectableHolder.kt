package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface SelectableHolder {
    val id: SelectableHolderId

    val selectableIds: List<SelectableId>
}
