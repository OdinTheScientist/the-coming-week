package com.thecomingweek.domain.model

data class QuestSnapshot(
    val id: String,
    val title: String,
    val action: String,
    val stat: StatType,
    val status: QuestStatus,
)
