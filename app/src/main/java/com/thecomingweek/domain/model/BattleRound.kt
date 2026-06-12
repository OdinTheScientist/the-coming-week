package com.thecomingweek.domain.model

// One exchange of the daily auto-battle. The hero strikes first; enemyDamage
// is null when the hero's strike ends the fight before the enemy can answer.
data class BattleRound(
    val roundNumber: Int,
    val playerDamage: Int,
    val enemyDamage: Int?,
    val playerHpAfter: Int,
    val enemyHpAfter: Int,
)
