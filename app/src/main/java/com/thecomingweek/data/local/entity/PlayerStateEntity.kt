package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerStateEntity(
    @PrimaryKey val id: Int = 1,
    val runNumber: Int,
    val level: Int,
    val xp: Int,
    val currentBiomeId: Long?,
    val currentWeekId: Long?,
    val currentHp: Int,
    val maxHp: Int
)
