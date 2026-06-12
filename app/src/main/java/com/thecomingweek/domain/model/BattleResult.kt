package com.thecomingweek.domain.model

// The resolved outcome of a single day's auto-battle, keyed by the day it was
// fought. Persisted so the day's battle is resolved at most once.
data class BattleResult(
    val epochDay: Long,
    val weekId: Long,
    val type: BattleType,
    val enemyName: String,
    val outcome: BattleOutcome,
    val rounds: List<BattleRound>,
    val playerHpAfter: Int,
)
