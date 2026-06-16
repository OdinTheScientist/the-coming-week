package com.thecomingweek.domain.model

// A settled day: the quests it carried, how they landed, and what the day's
// battle did to the body. Written once, when the day's battle resolves.
data class DayRecord(
    val epochDay: Long,
    val biomeId: Long,
    val weekId: Long,
    val questIds: List<String>,
    val questStatuses: Map<String, QuestStatus>,
    val battleOutcome: BattleOutcome?,
    val hpBefore: Int,
    val hpAfter: Int,
    val note: String?,
)
