package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Actor
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.CreatureId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.creatureId

data class CreatureMenu(
    val id: CreatureId,
    val actor: Actor,
    val bodyParts: List<SelectableItem.BodyPartItem>,
    val isControlled: Boolean,
    val isTarget: Boolean,
)

fun creatureMenu(
    id:Long = 0L,
    actor: Actor = Actor.Enemy,
    bodyParts: List<SelectableItem.BodyPartItem> = emptyList(),
    isControlled: Boolean = false,
    isTarget: Boolean = false,
) = CreatureMenu(
    id = creatureId(id),
    actor = actor,
    bodyParts = bodyParts,
    isControlled = isControlled,
    isTarget = isTarget,
)
