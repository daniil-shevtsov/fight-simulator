package com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation

import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.Actor

data class CreatureMenu(
    val id: String,
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
    id = id,
    actor = actor,
    bodyParts = bodyParts,
    isControlled = isControlled,
)
