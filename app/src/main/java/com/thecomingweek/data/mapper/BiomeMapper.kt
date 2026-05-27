package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.BiomeEntity
import com.thecomingweek.domain.model.Biome

fun BiomeEntity.toDomain(): Biome = Biome(
    id = id,
    name = name,
    flavor = flavor,
    weekCount = weekCount,
    startEpochDay = startEpochDay,
    finalBossId = finalBossId
)

fun Biome.toEntity(): BiomeEntity = BiomeEntity(
    id = id,
    name = name,
    flavor = flavor,
    weekCount = weekCount,
    startEpochDay = startEpochDay,
    finalBossId = finalBossId
)
