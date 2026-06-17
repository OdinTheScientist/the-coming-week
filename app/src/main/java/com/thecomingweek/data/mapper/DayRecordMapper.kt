package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.DayRecordEntity
import com.thecomingweek.domain.model.DayRecord
import com.thecomingweek.domain.model.QuestSnapshot
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.StatType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun DayRecordEntity.toDomain(): DayRecord = DayRecord(
    epochDay = epochDay,
    biomeId = biomeId,
    weekId = weekId,
    quests = questsFromJson(questsJson),
    battleOutcome = battleOutcome,
    hpBefore = hpBefore,
    hpAfter = hpAfter,
    note = note,
)

fun DayRecord.toEntity(): DayRecordEntity = DayRecordEntity(
    epochDay = epochDay,
    biomeId = biomeId,
    weekId = weekId,
    questsJson = questsToJson(quests),
    battleOutcome = battleOutcome,
    hpBefore = hpBefore,
    hpAfter = hpAfter,
    note = note,
)

// Private JSON mirror of QuestSnapshot: keeps domain/model free of
// kotlinx.serialization annotations.
@Serializable
private data class QuestSnapshotJson(
    val id: String,
    val title: String,
    val action: String,
    val stat: String,
    val status: String,
)

private fun QuestSnapshot.toJson() = QuestSnapshotJson(id, title, action, stat.name, status.name)
private fun QuestSnapshotJson.toDomain() =
    QuestSnapshot(id, title, action, StatType.valueOf(stat), QuestStatus.valueOf(status))

private fun questsToJson(quests: List<QuestSnapshot>): String =
    Json.encodeToString(quests.map { it.toJson() })

private fun questsFromJson(json: String): List<QuestSnapshot> =
    Json.decodeFromString<List<QuestSnapshotJson>>(json).map { it.toDomain() }
