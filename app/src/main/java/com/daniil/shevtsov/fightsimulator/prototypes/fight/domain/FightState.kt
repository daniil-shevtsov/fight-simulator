package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val lastSelectedControlledHolderId: SelectableHolderId,
    val lastSelectedControlledPartId: SelectableId,
    val lastSelectedTargetHolderId: SelectableHolderId,
    val lastSelectedTargetPartId: SelectableId,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
    val world: World,
) {

    val selectableHolders: List<SelectableHolder>
        get() = actors + listOf(world.ground)

    val selectables: List<Selectable>
        get() = selectableHolders.flatMap(SelectableHolder::selectables)

    private val currentTargetSelectableId: SelectableId?
        get() {
            val lastHolder = selectableHolders.find { it.id == lastSelectedTargetHolderId }
            val lastHolderSelectables = lastHolder?.selectables.orEmpty()
            val newSelectable = when {
                lastHolderSelectables.any { selectable -> selectable.id == lastSelectedTargetPartId } -> lastSelectedTargetPartId
                lastHolderSelectables.isNotEmpty() -> lastHolderSelectables.first().id
                else -> selectableHolders.firstOrNull { it.id != controlledCreature.id }?.selectables?.firstOrNull()?.id
            }
            return newSelectable
        }

    val targetSelectableHolder: SelectableHolder
        get() = selectableHolders.find { holder -> holder.selectables.any { selectable -> selectable.id == currentTargetSelectableId } }!!

    val targetSelectable: Selectable?
        get() = selectables.find { it.id == currentTargetSelectableId }

    val controlledCreature: Creature
        get() = actors.find { it.id == lastSelectedControlledHolderId } ?: actors.first()
    val targetCreature: Creature
        get() = actors.find { it.id == lastSelectedTargetHolderId } ?: actors.last()

    val controlledBodyPart: BodyPart
        get() = controlledCreature.controlledSelectedBodyPart
    val targetBodyPart: BodyPart?
        get() = targetCreature.targetSelectedBodyPart.takeIf { targetCreature.id == targetSelectableHolder.id }
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

    private val Creature.targetSelectedBodyPart
        get() = functionalParts.find { it.id == bodyParts.find { kek -> kek.id == currentTargetSelectableId }?.id }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Creature.controlledSelectedBodyPart
        get() = functionalParts.find { it.id == bodyParts.find { kek -> kek.id == lastSelectedControlledPartId }?.id }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Item?.attackActionsWithThrow: List<AttackAction>
        get() = this?.attackActions?.let { it + listOf(AttackAction.Throw) }.orEmpty()
}


fun fightState(
    controlledActorId: CreatureId = creatureId(0L),
    targetId: SelectableHolderId = creatureId(0L),
    realControlledSelectableId: SelectableId = bodyPartId(0L),
    realTargetSelectableId: SelectableId = bodyPartId(0L),
    lastSelectedHolderId: SelectableHolderId = creatureId(0L),
    actors: List<Creature> = listOf(
        creature(id = "playerId".hashCode().toLong()),
        creature(id = "enemyId".hashCode().toLong())
    ),
    actionLog: List<ActionEntry> = emptyList(),
    world: World = world(),
) = FightState(
    lastSelectedControlledHolderId = controlledActorId,
    lastSelectedControlledPartId = realControlledSelectableId,
    lastSelectedTargetHolderId = lastSelectedHolderId,
    lastSelectedTargetPartId = realTargetSelectableId,
    actors = actors,
    actionLog = actionLog,
    world = world,
)
