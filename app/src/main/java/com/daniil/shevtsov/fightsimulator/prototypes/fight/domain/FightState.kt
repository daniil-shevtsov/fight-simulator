package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val selections: Map<CreatureId, SelectableId>,
    val controlledActorId: CreatureId,
    val targetId: TargetId,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
    val world: World,
) {

    val targets: List<Targetable>
        get() = actors + world.ground

    val target: Targetable
        get() = targets.find { it.targetId == targetId }!!

    val controlledCreature: Creature
        get() = actors.find { it.id == controlledActorId } ?: actors.first()
    val targetCreature: Creature
        get() = actors.find { it.id != controlledActorId } ?: actors.last()

    val controlledBodyPart: BodyPart
        get() = controlledCreature.selectedBodyPart
    val targetBodyPart: BodyPart
        get() = targetCreature.selectedBodyPart
    val targetBodyPartBone: BodyPart?
        get() = targetCreature.bodyParts.firstOrNull { it.id in targetBodyPart.containedBodyParts }

    val availableCommands: List<Command>
        get() = if (controlledCreature.bodyParts.isNotEmpty()) {
            (controlledBodyPart.attackActions + controlledBodyPart.holding.attackActionsWithThrow)
        } else {
            emptyList()
        }.map(::Command)

    private val Creature.selectedBodyPart
        get() = functionalParts.find { it.name == bodyParts.find { kek -> kek.id == selections[id] }?.name }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Item?.attackActionsWithThrow: List<AttackAction>
        get() = this?.attackActions?.let { it + listOf(AttackAction.Throw) }.orEmpty()
}


fun fightState(
    controlledActorId: CreatureId = creatureId(0L),
    targetId: TargetId = creatureTargetId(0L),
    selections: Map<CreatureId, SelectableId> = mapOf(),
    actors: List<Creature> = listOf(creature(id = "playerId".hashCode().toLong()), creature(id = "enemyId".hashCode().toLong())),
    actionLog: List<ActionEntry> = emptyList(),
    world: World = world(),
) = FightState(
    controlledActorId = controlledActorId,
    targetId = targetId,
    actors = actors,
    selections = selections,
    actionLog = actionLog,
    world = world,
)
