package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.WeekEntity
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.model.Week
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

fun WeekEntity.toDomain(): Week = Week(
    id = id,
    weekNumber = weekNumber,
    statTheme = statTheme,
    biomeId = biomeId,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
    quotas = quotasFromJson(quotasJson),
    isResolved = isResolved,
    rerollsRemaining = rerollsRemaining
)

fun Week.toEntity(): WeekEntity = WeekEntity(
    id = id,
    weekNumber = weekNumber,
    statTheme = statTheme,
    biomeId = biomeId,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
    quotasJson = quotasToJson(quotas),
    isResolved = isResolved,
    rerollsRemaining = rerollsRemaining
)

private val mapSerializer = MapSerializer(String.serializer(), Int.serializer())

private fun quotasFromJson(json: String): Map<StatType, Int> {
    val stringMap = Json.decodeFromString(mapSerializer, json)
    return stringMap.mapKeys { StatType.valueOf(it.key) }
}

private fun quotasToJson(quotas: Map<StatType, Int>): String {
    val stringMap = quotas.mapKeys { it.key.name }
    return Json.encodeToString(mapSerializer, stringMap)
}
