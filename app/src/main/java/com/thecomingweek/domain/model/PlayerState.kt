package com.thecomingweek.domain.model

data class PlayerState(
    val runNumber: Int,
    val level: Int,
    val xp: Int,
    val currentBiomeId: Long?,
    val currentWeekId: Long?,
    val currentHp: Int,
    val maxHp: Int,
    val stats: List<Stat>
)
