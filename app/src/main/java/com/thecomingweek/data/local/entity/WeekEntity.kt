package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thecomingweek.domain.model.StatType

@Entity(tableName = "weeks")
data class WeekEntity(
    @PrimaryKey val id: Long,
    val weekNumber: Int,
    val statTheme: StatType,
    val biomeId: Long,
    val startEpochDay: Long,
    val endEpochDay: Long,
    val quotasJson: String,
    val isResolved: Boolean,
    val rerollsRemaining: Int
)
