package com.thecomingweek.domain.model

data class Quest(
    val id: String,
    val title: String,
    val flavor: String,
    val stat: StatType,
    val type: QuestType,
    val xpReward: Int,
    val statGain: Int,
    val weight: Int,
    val status: QuestStatus,
    val dayAssigned: Long?
)
