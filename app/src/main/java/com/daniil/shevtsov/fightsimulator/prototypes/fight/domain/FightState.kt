package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val realControlledSelectableId: SelectableId,
    val lastSelectedTargetPartId: SelectableId,
    val controlledActorId: CreatureId,
    val targetId: SelectableHolderId,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
    val world: World,
) {

    val selectableHolders: List<SelectableHolder>
        get() = actors + listOf(world.ground)

    val selectables: List<Selectable>
        get() = selectableHolders.flatMap(SelectableHolder::selectables)

    val currentSelectedTargetId: SelectableId?
        get() {
            val currentHolder =
                selectableHolders.find { holder -> holder.selectables.any { selectable -> selectable.id == lastSelectedTargetPartId } }
            val currentHolderSelectables = currentHolder?.selectables.orEmpty()
            return when {
                currentHolderSelectables.any { selectable -> selectable.id == lastSelectedTargetPartId } -> lastSelectedTargetPartId
                currentHolderSelectables.isNotEmpty() -> targetSelectableHolder.selectables.first().id
                else -> selectableHolders.firstOrNull { it.id != controlledCreature.id }?.selectables?.firstOrNull()?.id
            }
        }

    val targetSelectableHolder: SelectableHolder
        get() = selectableHolders.find { holder ->
            holder.selectables.any { selectable -> selectable.id == currentSelectedTargetId }
        }!!

    val targetSelectable: Selectable?
        get() = selectables.find { it.id == currentSelectedTargetId }

    val controlledCreature: Creature
        get() = actors.find { it.id == controlledActorId } ?: actors.first()
    val targetCreature: Creature
        get() = actors.find { it.bodyParts.any { bodyPart -> bodyPart.id == currentSelectedTargetId } }
            ?: actors.last()

    val controlledBodyPart: BodyPart
        get() = controlledCreature.controlledSelectedBodyPart
    val targetBodyPart: BodyPart?
        get() = targetCreature.targetselectedBodyPart.takeIf { targetCreature.id == targetId }
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

    private val Creature.targetselectedBodyPart
        get() = functionalParts.find { it.id == bodyParts.find { kek -> kek.id == currentSelectedTargetId }?.id }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Creature.controlledSelectedBodyPart
        get() = functionalParts.find { it.id == bodyParts.find { kek -> kek.id == realControlledSelectableId }?.id }
            ?: functionalParts.firstOrNull() ?: bodyParts.first()

    private val Item?.attackActionsWithThrow: List<AttackAction>
        get() = this?.attackActions?.let { it + listOf(AttackAction.Throw) }.orEmpty()
}


fun fightState(
    controlledActorId: CreatureId = creatureId(0L),
    targetId: SelectableHolderId = creatureId(0L),
    realControlledSelectableId: SelectableId = bodyPartId(0L),
    realTargetSelectableId: SelectableId = bodyPartId(0L),
    actors: List<Creature> = listOf(
        creature(id = "playerId".hashCode().toLong()),
        creature(id = "enemyId".hashCode().toLong())
    ),
    actionLog: List<ActionEntry> = emptyList(),
    world: World = world(),
) = FightState(
    controlledActorId = controlledActorId,
    targetId = targetId,
    realControlledSelectableId = realControlledSelectableId,
    lastSelectedTargetPartId = realTargetSelectableId,
    actors = actors,
    actionLog = actionLog,
    world = world,
)
