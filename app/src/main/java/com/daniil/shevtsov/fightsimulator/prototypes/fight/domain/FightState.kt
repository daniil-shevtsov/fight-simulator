package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val lastSelectedControlledHolderId: SelectableHolderId,
    val lastSelectedControlledPartId: SelectableId,
    val lastSelectedTargetHolderId: SelectableHolderId,
    val lastSelectedTargetPartId: SelectableId,
    val allSelectables: List<Selectable>,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
    val world: World,
) {

    //    val allSelectables: List<Selectable>
//        get() = (actors.flatMap { it.bodyParts } + world.ground.selectables + (actors.flatMap { it.bodyParts } + world.ground.selectables).filterIsInstance<BodyPart>()
//            .mapNotNull { it.holding }).let { holdingIds -> allItems.filter { it.id in holdingIds  } }.associateBy { it.id }.toList().map { it.second }
    val allBodyParts: List<BodyPart>
        get() = allSelectables.filterIsInstance<BodyPart>()
    val allItems: List<Item>
        get() = allSelectables.filterIsInstance<Item>()

    val selectableHolders: List<SelectableHolder>
        get() = actors + listOf(world.ground)

    val selectables: List<Selectable>
        get() = allSelectables

    private val currentTargetSelectableId: SelectableId?
        get() {
            val lastHolder = selectableHolders.find { it.id == lastSelectedTargetHolderId }
            val lastHolderSelectableIds = lastHolder?.selectableIds.orEmpty()
            val newSelectable = when {
                lastHolderSelectableIds.any { selectableId -> selectableId == lastSelectedTargetPartId } -> lastSelectedTargetPartId
                lastHolderSelectableIds.isNotEmpty() -> lastHolderSelectableIds.first()
                else -> selectableHolders.firstOrNull { it.id != controlledCreature.id }?.selectableIds?.firstOrNull()
            }
            return newSelectable
        }

    val targetSelectableHolder: SelectableHolder
        get() {
            val targetHolder = selectableHolders.find { holder ->
                holder.selectableIds.any { selectableId ->
                    selectableId == currentTargetSelectableId || selectableId == lastSelectedTargetPartId
                }
            }
            return targetHolder!!
        }

    val targetSelectable: Selectable?
        get() = selectables.find { it.id == currentTargetSelectableId }

    val controlledCreature: Creature
        get() = actors.find { it.id == lastSelectedControlledHolderId } ?: actors.first()
    val controlledCreatureBodyParts: List<BodyPart>
        get() = allBodyParts.filter { bodyPart -> bodyPart.id in controlledCreature.bodyPartIds }
    val targetCreature: Creature
        get() = actors.find { it.id == lastSelectedTargetHolderId } ?: actors.last()
    val targetCreatureBodyParts: List<BodyPart>
        get() = allBodyParts.filter { bodyPart -> bodyPart.id in targetCreature.bodyPartIds || bodyPart.id in targetCreature.missingPartsSet }

    val controlledBodyPart: BodyPart
        get() = allBodyParts.find { it.id == controlledCreature.controlledSelectedBodyPart.id }!!
    val targetBodyPart: BodyPart?
        get() = allBodyParts.find { it.id == targetCreature.targetSelectedBodyPart?.id }
            .takeIf { targetCreature.id == targetSelectableHolder.id }
    val targetBodyPartBone: BodyPart?
        get() = allBodyParts.firstOrNull { bodyPart -> bodyPart.id in targetBodyPart?.containedBodyParts.orEmpty() }

    val availableCommands: List<Command>
        get() = when {
            world.ground.selectableIds.contains(targetSelectable?.id) && controlledBodyPart.canGrab -> listOf(
                AttackAction.Grab
            )
            allBodyParts.filter { it.id in controlledCreature.bodyPartIds }
                .isNotEmpty() -> (controlledBodyPart.attackActions + allSelectables.find { it.id == controlledBodyPart.holding }.attackActionsWithThrow)
            else -> emptyList()
        }.map(::Command)

    private val Creature.targetSelectedBodyPart: BodyPart?
        get() = (functionalParts.find { id -> id == allBodyParts.find { bodyPart -> bodyPart.id == currentTargetSelectableId }?.id }
            ?: functionalParts.firstOrNull()
            ?: allBodyParts.first { it.id in bodyPartIds }).let { id -> allBodyParts.find { it.id == id } }

    private val Creature.controlledSelectedBodyPart: BodyPart
        get() = (functionalParts.find { id -> id == allBodyParts.find { bodyPart -> bodyPart.id == lastSelectedControlledPartId }?.id }
            ?: functionalParts.firstOrNull()
            ?: allBodyParts.first { it.id in bodyPartIds }).let { id -> allBodyParts.find { it.id == id } }!!

    private val Selectable?.attackActionsWithThrow: List<AttackAction>
        get() = (this as? Item)?.attackActions.orEmpty() + listOf(AttackAction.Throw).takeIf { this != null }
            .orEmpty()
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
    allSelectables: List<Selectable> = emptyList(),
    actionLog: List<ActionEntry> = emptyList(),
    world: World = world(),
) = FightState(
    lastSelectedControlledHolderId = controlledActorId,
    lastSelectedControlledPartId = realControlledSelectableId,
    lastSelectedTargetHolderId = lastSelectedHolderId,
    lastSelectedTargetPartId = realTargetSelectableId,
    actors = actors,
    allSelectables = allSelectables,
    actionLog = actionLog,
    world = world,
)
