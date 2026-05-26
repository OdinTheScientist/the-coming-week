package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bosses")
data class BossEntity(
    @PrimaryKey val id: Long,
    val weekId: Long,
    val biomeId: Long,
    val name: String,
    val flavor: String,
    val baseDifficulty: Int,
    val finalDifficulty: Int,
    val defeated: Boolean?
)
