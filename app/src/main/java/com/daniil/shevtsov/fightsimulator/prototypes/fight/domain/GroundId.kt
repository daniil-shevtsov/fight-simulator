package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

@JvmInline
value class GroundId(override val raw: Long) : TargetId, SelectableHolderId

fun groundId(raw: Long) = GroundId(raw)
