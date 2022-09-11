package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

class FightWeaponEnemyTest : FightWeaponTest {
    override val controlledActorName: String
        get() = "Enemy"
    override val targetActorName: String
        get() = "Player"
}
