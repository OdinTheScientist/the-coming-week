package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.BattleResultEntity
import com.thecomingweek.domain.model.BattleResult
import com.thecomingweek.domain.model.BattleRound
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun BattleResultEntity.toDomain(): BattleResult = BattleResult(
    epochDay = epochDay,
    weekId = weekId,
    type = type,
    enemyName = enemyName,
    outcome = outcome,
    rounds = roundsFromJson(roundsJson),
    playerHpAfter = playerHpAfter
)

fun BattleResult.toEntity(): BattleResultEntity = BattleResultEntity(
    epochDay = epochDay,
    weekId = weekId,
    type = type,
    enemyName = enemyName,
    outcome = outcome,
    roundsJson = roundsToJson(rounds),
    playerHpAfter = playerHpAfter
)

// Private JSON mirror of BattleRound: keeps domain/model free of
// kotlinx.serialization annotations while still serializing rounds with the
// same Json instance used elsewhere (see WeekMapper's quota serialization).
@Serializable
private data class BattleRoundJson(
    val roundNumber: Int,
    val playerDamage: Int,
    val enemyDamage: Int?,
    val playerHpAfter: Int,
    val enemyHpAfter: Int,
)

private fun BattleRound.toJson() = BattleRoundJson(roundNumber, playerDamage, enemyDamage, playerHpAfter, enemyHpAfter)
private fun BattleRoundJson.toDomain() = BattleRound(roundNumber, playerDamage, enemyDamage, playerHpAfter, enemyHpAfter)

private fun roundsToJson(rounds: List<BattleRound>): String =
    Json.encodeToString(rounds.map { it.toJson() })

private fun roundsFromJson(json: String): List<BattleRound> =
    Json.decodeFromString<List<BattleRoundJson>>(json).map { it.toDomain() }
