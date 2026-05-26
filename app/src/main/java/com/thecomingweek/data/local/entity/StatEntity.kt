package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thecomingweek.domain.model.StatType

@Entity(tableName = "stats")
data class StatEntity(
    @PrimaryKey val type: StatType,
    val value: Int,
    val weeklyGain: Int
)
