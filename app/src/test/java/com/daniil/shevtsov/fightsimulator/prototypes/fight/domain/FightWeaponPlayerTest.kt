package com.daniil.shevtsov.fightsimulator.prototypes.fight.domain

class FightWeaponPlayerTest : FightWeaponTest {
    override val controlledActorName: String
        get() = "Player"
    override val targetActorName: String
        get() = "Enemy"
}
