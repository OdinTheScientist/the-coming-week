package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thecomingweek.domain.model.BattleOutcome
import com.thecomingweek.domain.model.BattleType

@Entity(tableName = "battle_results")
data class BattleResultEntity(
    @PrimaryKey val epochDay: Long,
    val weekId: Long,
    val type: BattleType,
    val enemyName: String,
    val outcome: BattleOutcome,
    val roundsJson: String,
    val playerHpAfter: Int
)
