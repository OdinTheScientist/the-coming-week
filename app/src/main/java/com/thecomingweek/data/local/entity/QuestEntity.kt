package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatType

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
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
