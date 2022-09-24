package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val lastSelectedControlledPartId: SelectableId,
    val lastSelectedTargetHolderId: SelectableHolderId,
    val lastSelectedTargetPartId: SelectableId,
    val allSelectables: List<Selectable>,
    val actors: List<Creature>,
    val actionLog: List<ActionEntry>,
    val world: World,
) {
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
            val lastHolderAllSelectableIds = lastHolder?.allSelectableIds(allSelectables).orEmpty()
            val newSelectable = when {
                lastHolderAllSelectableIds.any { selectableId -> selectableId == lastSelectedTargetPartId } -> lastSelectedTargetPartId
                lastHolderAllSelectableIds.isNotEmpty() -> lastHolderAllSelectableIds.first()
                else -> selectableHolders.firstOrNull { it.id != controlledCreature.id }?.selectableIds?.firstOrNull()
            }
            return newSelectable
        }

    val targetSelectableHolder: SelectableHolder
        get() {
            val targetHolder = selectableHolders.find { holder ->
                val holderAllSelectableIds = holder.allSelectableIds(allSelectables)
                holderAllSelectableIds.any { selectableId ->
                    selectableId == currentTargetSelectableId
                }
            }
            return targetHolder!!
        }

    val targetSelectable: Selectable?
        get() = selectables.find { it.id == currentTargetSelectableId }

    val controlledCreature: Creature
        get() = actors.find { it.bodyPartIds.contains(lastSelectedControlledPartId) }
            ?: actors.first()
    val controlledCreatureBodyParts: List<BodyPart>
        get() = allBodyParts.filter { bodyPart -> bodyPart.id in controlledCreature.bodyPartIds }
    val controlledBodyPart: BodyPart
        get() = allBodyParts.find { it.id == controlledCreature.controlledSelectedBodyPart.id }!!
    val controlledCreatureAllSelectableIds: List<SelectableId>
        get() = controlledCreature.allSelectableIds(allSelectables)

    val targetCreature: Creature
        get() = actors.find { it.id == targetSelectableHolder.id } ?: actors.last()
    val targetCreatureBodyParts: List<BodyPart>
        get() = allBodyParts.filter { bodyPart -> bodyPart.id in targetCreature.bodyPartIds || bodyPart.id in targetCreature.missingPartsSet }
    val targetBodyPart: BodyPart?
        get() = allBodyParts.find { it.id == targetCreature.targetSelectedBodyPart?.id }
            .takeIf { targetCreature.id == targetSelectableHolder.id }
    val targetBodyPartBone: BodyPart?
        get() = allBodyParts.firstOrNull { bodyPart -> bodyPart.id in targetBodyPart?.containedBodyParts.orEmpty() }
    val targetCreatureAllSelectableIds: List<SelectableId>
        get() = targetCreature.allSelectableIds(allSelectables)

    val availableCommands: List<Command>
        get() = when {
            allItems.any { it.id == targetSelectable?.id } && controlledBodyPart.canGrab && controlledBodyPart.holding == null -> listOf(
                AttackAction.Grab
            )
            allBodyParts.any { it.id in controlledCreature.bodyPartIds } -> (controlledBodyPart.attackActions
                    + allSelectables.find { it.id == controlledBodyPart.holding }.attackActionsWithThrow)
            else -> emptyList()
        }.map(::Command)

    private val Creature.targetSelectedBodyPart: BodyPart?
        get() = findSelected(lastSelectedId = currentTargetSelectableId)

    private val Creature.controlledSelectedBodyPart: BodyPart
        get() = findSelected(lastSelectedId = lastSelectedControlledPartId)!!

    private fun SelectableHolder.allSelectableIds(allSelectables: List<Selectable>): List<SelectableId> {
        return selectableIds + allSelectables.filter { it.id in selectableIds }
            .filterIsInstance<BodyPart>().flatMap { it.lodgedInSelectables.toList() }
    }

    private fun Creature.findSelected(lastSelectedId: SelectableId?): BodyPart? {
        val creatureBodyParts =
            allBodyParts.filter { bodyPart -> bodyPart.id in (bodyPartIds + missingPartsSet) }

        val part1 = functionalParts.find { it == lastSelectedId }
        val part2 = functionalParts.firstOrNull()
        val part3 = creatureBodyParts.first().id

        val finalPart =
            (part1 ?: part2 ?: part3).let { id -> creatureBodyParts.find { kek -> kek.id == id } }

        return finalPart!!
    }

    private val Selectable?.attackActionsWithThrow: List<AttackAction>
        get() = (this as? Item)?.attackActions.orEmpty() + listOf(AttackAction.Throw).takeIf { this != null }
            .orEmpty()
}


fun fightState(
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
    lastSelectedControlledPartId = realControlledSelectableId,
    lastSelectedTargetHolderId = lastSelectedHolderId,
    lastSelectedTargetPartId = realTargetSelectableId,
    actors = actors,
    allSelectables = allSelectables,
    actionLog = actionLog,
    world = world,
)
