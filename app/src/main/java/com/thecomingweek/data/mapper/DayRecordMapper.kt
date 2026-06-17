package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.DayRecordEntity
import com.thecomingweek.domain.model.DayRecord
import com.thecomingweek.domain.model.QuestStatus
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

fun DayRecordEntity.toDomain(): DayRecord = DayRecord(
    epochDay = epochDay,
    biomeId = biomeId,
    weekId = weekId,
    questIds = Json.decodeFromString(ListSerializer(String.serializer()), questIdsJson),
    questStatuses = Json.decodeFromString(
        MapSerializer(String.serializer(), String.serializer()), questStatusesJson
    ).mapValues { QuestStatus.valueOf(it.value) },
    battleOutcome = battleOutcome,
    hpBefore = hpBefore,
    hpAfter = hpAfter,
    note = note,
)

fun DayRecord.toEntity(): DayRecordEntity = DayRecordEntity(
    epochDay = epochDay,
    biomeId = biomeId,
    weekId = weekId,
    questIdsJson = Json.encodeToString(ListSerializer(String.serializer()), questIds),
    questStatusesJson = Json.encodeToString(
        MapSerializer(String.serializer(), String.serializer()),
        questStatuses.mapValues { it.value.name }
    ),
    battleOutcome = battleOutcome,
    hpBefore = hpBefore,
    hpAfter = hpAfter,
    note = note,
)
