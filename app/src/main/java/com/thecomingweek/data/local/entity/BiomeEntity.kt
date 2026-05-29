package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "biomes")
data class BiomeEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val flavor: String,
    val weekCount: Int,
    val startEpochDay: Long,
    val finalBossId: Long?,
    val isCompleted: Boolean = false
)
