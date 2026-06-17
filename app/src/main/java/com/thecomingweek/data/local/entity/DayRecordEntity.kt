package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thecomingweek.domain.model.BattleOutcome

@Entity(tableName = "day_records")
data class DayRecordEntity(
    @PrimaryKey val epochDay: Long,
    val biomeId: Long,
    val weekId: Long,
    val questsJson: String,
    val battleOutcome: BattleOutcome?,
    val hpBefore: Int,
    val hpAfter: Int,
    val note: String?,
)
