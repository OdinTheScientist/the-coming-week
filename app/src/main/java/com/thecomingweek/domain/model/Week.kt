package com.thecomingweek.domain.model

data class Week(
    val id: Long,
    val weekNumber: Int,
    val statTheme: StatType,
    val biomeId: Long,
    val startEpochDay: Long,
    val endEpochDay: Long,
    val quotas: Map<StatType, Int>,
    val isResolved: Boolean
)
