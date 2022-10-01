package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

data class FightState(
    val lastSelectedControlledPartId: SelectableId,
    val lastSelectedTargetHolderId: SelectableHolderId,
    val lastSelectedTargetPartId: SelectableId,
    val allSelectables: Map<SelectableId, Selectable>,
    val selectableHolders: Map<SelectableHolderId, SelectableHolder>,
    val actionLog: List<ActionEntry>,
) {
    val allBodyParts: Map<SelectableId, BodyPart>
        get() = allSelectables.filterValues { it is BodyPart }.mapValues { it.value as BodyPart }
    val allItems: List<Item>
        get() = allSelectables.values.filterIsInstance<Item>()

    val actors: Map<SelectableHolderId, Creature>
        get() = selectableHolders.filterValues { it is Creature }.mapValues { it.value as Creature }
    val ground: Ground
        get() = selectableHolders.values.filterIsInstance<Ground>().first()

    private val currentTargetSelectableId: SelectableId?
        get() {
            val lastHolder = selectableHolders[lastSelectedTargetHolderId]
            val lastHolderAllSelectableIds =
                lastHolder?.allSelectableIds(allSelectables).orEmpty()
            val controlledCreatureId = controlledCreature.id
            val newSelectable = when {
                lastHolderAllSelectableIds.any { selectableId -> selectableId == lastSelectedTargetPartId } -> lastSelectedTargetPartId
                lastHolderAllSelectableIds.isNotEmpty() -> lastHolderAllSelectableIds.first()
                else -> selectableHolders.values.firstOrNull { it.id != controlledCreatureId }?.selectableIds?.firstOrNull()
            }
            return newSelectable
        }

    val targetSelectableHolder: SelectableHolder
        get() {
            val targetHolder = selectableHolders.values.find { holder ->
                val holderAllSelectableIds = holder.allSelectableIds(allSelectables)
                holderAllSelectableIds.any { selectableId ->
                    selectableId == currentTargetSelectableId
                }
            }
            return targetHolder!!
        }

    val targetSelectable: Selectable?
        get() = allSelectables[currentTargetSelectableId]

    val controlledCreature: Creature
        get() = actors.values.find { it.bodyPartIds.contains(lastSelectedControlledPartId) }
            ?: actors.values.first()
    val controlledCreatureBodyParts: List<BodyPart>
        get() {
            val controlledBodyPartIds = controlledCreature.bodyPartIds
            return allBodyParts.values.filter { bodyPart -> bodyPart.id in controlledBodyPartIds }
        }
    val controlledBodyPart: BodyPart
        get() = allBodyParts[controlledCreature.controlledSelectedBodyPart.id]!!

    val targetCreatureBodyParts: List<BodyPart>
        get() {
            val targetCreature = targetSelectableHolder as? Creature
            val targetBodyPartIds = targetCreature?.bodyPartIds.orEmpty()
            val targetMissingPartIds = targetCreature?.missingPartsSet.orEmpty()
            return allBodyParts.values
                .filter { bodyPart -> bodyPart.id in targetBodyPartIds || bodyPart.id in targetMissingPartIds }
        }
    val targetBodyPart: BodyPart?
        get() {
            val targetSelectableHolderId = targetSelectableHolder.id
            return allBodyParts[targetSelectable?.id!!]
                .takeIf { targetSelectableHolder.id == targetSelectableHolderId }
        }
    val targetBodyPartBone: BodyPart?
        get() {
            val targetBodyPartBones = targetBodyPart?.containedBodyParts.orEmpty()
            return allBodyParts.values.firstOrNull { bodyPart -> bodyPart.id in targetBodyPartBones }
        }

    val availableCommands: List<Command>
        get() {
            val controlledBodyPartIds = controlledCreature.bodyPartIds
            val isTargetSlashed =
                targetSelectable is BodyPart && selectableHolders.values.filterIsInstance<Creature>()
                    .none { creature ->
                        creature.bodyPartIds.contains(targetSelectable?.id)
                    }
            return when {
                (allItems.any { it.id == targetSelectable?.id } || isTargetSlashed) && controlledBodyPart.canGrab -> listOf(
                    AttackAction.Grab
                ).takeIf { controlledBodyPart.holding == null }.orEmpty()
                allBodyParts.values.any { it.id in controlledBodyPartIds } -> (controlledBodyPart.attackActions
                        + allSelectables[controlledBodyPart.holding].attackActionsWithThrow)
                else -> emptyList()
            }.map(::Command)
        }

    private val Creature.controlledSelectedBodyPart: BodyPart
        get() = findSelected(lastSelectedId = lastSelectedControlledPartId)!!

    private fun SelectableHolder.allSelectableIds(allSelectables: Map<SelectableId, Selectable>): List<SelectableId> {
        return selectableIds + allSelectables.values.filter { it.id in selectableIds }
            .filterIsInstance<BodyPart>().flatMap { it.lodgedInSelectables.toList() }
    }

    private fun Creature.findSelected(lastSelectedId: SelectableId?): BodyPart? {
        val creatureBodyParts =
            allBodyParts.filterValues { bodyPart -> bodyPart.id in (bodyPartIds + missingPartsSet) }

        val part1 = functionalParts.find { it == lastSelectedId }
        val part2 = functionalParts.firstOrNull()
        val part3 = creatureBodyParts.values.first().id

        val finalPart =
            (part1 ?: part2 ?: part3).let { id -> creatureBodyParts[id] }

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
    selectableHolders: Map<SelectableHolderId, SelectableHolder> = listOf(
        creature(id = "playerId".hashCode().toLong()),
        creature(id = "enemyId".hashCode().toLong()),
        ground(id = "groundId".hashCode().toLong()),
    ).associateBy { it.id },
    allSelectables: List<Selectable> = emptyList(),
    actionLog: List<ActionEntry> = emptyList(),
) = FightState(
    lastSelectedControlledPartId = realControlledSelectableId,
    lastSelectedTargetHolderId = lastSelectedHolderId,
    lastSelectedTargetPartId = realTargetSelectableId,
    selectableHolders = selectableHolders,
    allSelectables = allSelectables.associateBy { it.id },
    actionLog = actionLog,
)
