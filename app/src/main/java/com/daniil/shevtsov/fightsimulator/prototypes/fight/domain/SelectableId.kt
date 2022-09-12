package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

interface SelectableId {
    val raw: Long
}

fun bodyPartId(raw: Long) = BodyPartId(raw = raw)
fun itemId(raw: Long) = ItemId(raw = raw)
