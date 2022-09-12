package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Actor
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.CreatureId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.creatureId

data class CreatureMenu(
    val id: CreatureId,
    val actor: Actor,
    val bodyParts: List<BodyPartItem>,
    val isControlled: Boolean,
)

fun creatureMenu(
    id:String = "",
    actor: Actor = Actor.Enemy,
    bodyParts: List<BodyPartItem> = emptyList(),
    isControlled: Boolean = false,
) = CreatureMenu(
    id = creatureId(id),
    actor = actor,
    bodyParts = bodyParts,
    isControlled = isControlled,
)
