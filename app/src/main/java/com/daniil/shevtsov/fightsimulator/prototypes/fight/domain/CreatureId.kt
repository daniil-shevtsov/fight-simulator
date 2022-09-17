package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

@JvmInline
value class CreatureId(override val raw: Long) : SelectableHolderId

fun creatureId(raw: Long) = CreatureId(raw)
