package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val selections: Map<SelectableHolderId, SelectableId>,
    val controlledActorId: CreatureId,
    val targetId: SelectableHolderId,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
    val world: World,
) {

    var realTargetSelectableId: SelectableId? = selections[targetId]

    val target: Targetable
        get() = targets.find { it.id == targetId }!!

    private val targets: List<Targetable>
        get() = actors + world.ground

    val controlledCreature: Creature
        get() = actors.find { it.id == controlledActorId } ?: actors.first()
    val targetCreature: Creature
        get() = actors.find { it.id == targetId } ?: actors.last()

    val controlledBodyPart: BodyPart
        get() = controlledCreature.selectedBodyPart
    val targetBodyPart: BodyPart?
        get() = targetCreature.selectedBodyPart.takeIf { targetCreature.id == targetId }
    val targetBodyPartBone: BodyPart?
        get() = targetCreature.bodyParts.firstOrNull { it.id in targetBodyPart?.containedBodyParts.orEmpty() }

    val availableCommands: List<Command>
        get() = when {
            world.ground.items.contains(targetSelectable) && controlledBodyPart.canGrab -> listOf(
                AttackAction.Grab
            )
            controlledCreature.bodyParts.isNotEmpty() -> (controlledBodyPart.attackActions + controlledBodyPart.holding.attackActionsWithThrow)
            else -> emptyList()
        }.map(::Command)

    val selectableHolders: List<SelectableHolder>
        get() = listOf(controlledCreature, targetCreature, world.ground)

    val selectables: List<Selectable>
        get() = selectableHolders.flatMap(SelectableHolder::selectables)

    val targetSelectable: Selectable?
        get() = selectables.find { it.id == realTargetSelectableId }

    private val Creature.selectedBodyPart
        get() = functionalParts.find { it.id == bodyParts.find { kek -> kek.id == selections[id] }?.id }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Item?.attackActionsWithThrow: List<AttackAction>
        get() = this?.attackActions?.let { it + listOf(AttackAction.Throw) }.orEmpty()
}


fun fightState(
    controlledActorId: CreatureId = creatureId(0L),
    targetId: SelectableHolderId = creatureId(0L),
    selections: Map<SelectableHolderId, SelectableId> = mapOf(),
    actors: List<Creature> = listOf(
        creature(id = "playerId".hashCode().toLong()),
        creature(id = "enemyId".hashCode().toLong())
    ),
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
