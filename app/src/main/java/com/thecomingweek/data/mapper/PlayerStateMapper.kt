package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.PlayerStateEntity
import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.domain.model.PlayerState

fun PlayerStateEntity.toDomain(statEntities: List<StatEntity>): PlayerState = PlayerState(
    runNumber = runNumber,
    level = level,
    xp = xp,
    currentBiomeId = currentBiomeId,
    currentWeekId = currentWeekId,
    stats = statEntities.map { it.toDomain() }
)

fun PlayerState.toEntity(): PlayerStateEntity = PlayerStateEntity(
    id = 1,
    runNumber = runNumber,
    level = level,
    xp = xp,
    currentBiomeId = currentBiomeId,
    currentWeekId = currentWeekId
)

fun PlayerState.toStatEntities(): List<StatEntity> =
    stats.map { it.toEntity() }
