package com.thecomingweek.domain.model

data class DayRecord(
    val epochDay: Long,
    val biomeId: Long,
    val weekId: Long,
    val quests: List<QuestSnapshot>,
    val battleOutcome: BattleOutcome?,
    val hpBefore: Int,
    val hpAfter: Int,
    val note: String?,
)
