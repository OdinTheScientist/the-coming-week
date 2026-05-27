package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.BossEntity
import com.thecomingweek.domain.model.Boss

fun BossEntity.toDomain(): Boss = Boss(
    id = id,
    weekId = weekId,
    biomeId = biomeId,
    name = name,
    flavor = flavor,
    baseDifficulty = baseDifficulty,
    finalDifficulty = finalDifficulty,
    defeated = defeated
)

fun Boss.toEntity(): BossEntity = BossEntity(
    id = id,
    weekId = weekId,
    biomeId = biomeId,
    name = name,
    flavor = flavor,
    baseDifficulty = baseDifficulty,
    finalDifficulty = finalDifficulty,
    defeated = defeated
)
