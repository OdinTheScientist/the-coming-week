package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.BuffEntity
import com.thecomingweek.domain.model.Buff

fun BuffEntity.toDomain(): Buff = Buff(
    id = id,
    name = name,
    polarity = polarity,
    statAffected = statAffected,
    modifier = modifier,
    expiresEpochDay = expiresEpochDay,
    source = source
)

fun Buff.toEntity(): BuffEntity = BuffEntity(
    id = id,
    name = name,
    polarity = polarity,
    statAffected = statAffected,
    modifier = modifier,
    expiresEpochDay = expiresEpochDay,
    source = source
)
