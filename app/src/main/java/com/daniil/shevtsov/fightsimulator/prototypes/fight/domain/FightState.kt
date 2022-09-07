package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val selections: Map<String, String>,
    val controlledActorId: String,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
) {

    val controlledCreature: Creature
        get() = actors.find { it.id == controlledActorId } ?: actors.first()
    val targetCreature: Creature
        get() = actors.find { it.id != controlledActorId } ?: actors.last()

    val controlledBodyPart: BodyPart
        get() = controlledCreature.selectedBodyPart
    val targetBodyPart: BodyPart
        get() = targetCreature.selectedBodyPart
    val targetBodyPartBone: BodyPart?
        get() = targetCreature.bodyParts.firstOrNull { it.name in targetBodyPart.containedBodyParts }

    val availableCommands: List<Command>
        get() = if (controlledCreature.bodyParts.isNotEmpty()) {
            (controlledBodyPart.attackActions + controlledBodyPart.holding.attackActionsWithThrow)
        } else {
            emptyList()
        }.map(::Command)

//    private val Creature.selectedBodyPart
//        get() = bodyParts.find { it.name == selections[id] }
//            ?: bodyParts.first()
    private val Creature.selectedBodyPart
        get() = functionalParts.find { it.name == selections[id] }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Item?.attackActionsWithThrow: List<AttackAction>
        get() = this?.attackActions?.let { it + listOf(AttackAction.Throw) }.orEmpty()
}


fun fightState(
    controlledActorId: String = "",
    selections: Map<String, String> = mapOf(),
    actors: List<Creature> = listOf(creature(id = "playerId"), creature(id = "enemyId")),
    actionLog: List<ActionEntry> = emptyList(),
) = FightState(
    controlledActorId = controlledActorId,
    actors = actors,
    selections = selections,
    actionLog = actionLog,
)
