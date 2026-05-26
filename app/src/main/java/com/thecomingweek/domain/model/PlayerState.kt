package com.thecomingweek.domain.model

data class PlayerState(
    val runNumber: Int,
    val level: Int,
    val xp: Int,
    val currentBiomeId: Long?,
    val currentWeekId: Long?,
    val stats: List<Stat>
)
