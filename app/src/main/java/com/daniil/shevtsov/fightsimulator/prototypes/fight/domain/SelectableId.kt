package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

sealed interface SelectableId {
    @JvmInline
    value class BodyPart(val raw: Long) : SelectableId

    @JvmInline
    value class Item(val raw: Long) : SelectableId
}

fun SelectableId.BodyPart(raw: Long) = SelectableId.BodyPart(raw = raw)
fun itemId(raw: Long) = SelectableId.Item(raw = raw)
