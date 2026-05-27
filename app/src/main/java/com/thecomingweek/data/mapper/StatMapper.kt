package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.domain.model.Stat

fun StatEntity.toDomain(): Stat = Stat(
    type = type,
    value = value,
    weeklyGain = weeklyGain
)

fun Stat.toEntity(): StatEntity = StatEntity(
    type = type,
    value = value,
    weeklyGain = weeklyGain
)
