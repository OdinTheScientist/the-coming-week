package com.thecomingweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thecomingweek.domain.model.BuffPolarity
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.StatType

@Entity(tableName = "buffs")
data class BuffEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val polarity: BuffPolarity,
    val statAffected: StatType?,
    val modifier: Int,
    val expiresEpochDay: Long,
    val source: BuffSource
)
