package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BuffDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.Buff
import com.thecomingweek.domain.model.BuffPolarity
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.StatType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val BUFF_DURATION: Map<BuffSource, Long> = mapOf(
    BuffSource.QUEST_COMPLETED to 1L,
    BuffSource.QUEST_MISSED    to 1L,
    BuffSource.QUOTA_MET       to 7L,
    BuffSource.QUOTA_MISSED    to 7L,
    BuffSource.BOSS_WON        to 7L,
    BuffSource.BOSS_LOST       to 7L,
)

private val BUFF_MODIFIER: Map<BuffSource, Int> = mapOf(
    BuffSource.QUEST_COMPLETED to  1,
    BuffSource.QUEST_MISSED    to -1,
    BuffSource.QUOTA_MET       to  2,
    BuffSource.QUOTA_MISSED    to -2,
    BuffSource.BOSS_WON        to  3,
    BuffSource.BOSS_LOST       to -3,
)

private val BUFF_NAME: Map<BuffSource, String> = mapOf(
    BuffSource.QUEST_COMPLETED to "Rite Fulfilled",
    BuffSource.QUEST_MISSED    to "Rite Neglected",
    BuffSource.QUOTA_MET       to "The Week Answered",
    BuffSource.QUOTA_MISSED    to "The Week Unanswered",
    BuffSource.BOSS_WON        to "Trial Victorious",
    BuffSource.BOSS_LOST       to "Trial Broken",
)

class BuffRepository @Inject constructor(
    private val buffDao: BuffDao,
) {

    fun observeActive(epochDay: Long): Flow<List<Buff>> =
        buffDao.observeAll().map { entities ->
            entities.filter { it.expiresEpochDay > epochDay }.map { it.toDomain() }
        }

    suspend fun pruneAndGetActive(epochDay: Long): List<Buff> =
        buffDao.pruneAndGetActive(epochDay).map { it.toDomain() }

    suspend fun grant(source: BuffSource, stat: StatType, epochDay: Long): Buff {
        val polarity = when (source) {
            BuffSource.QUEST_COMPLETED, BuffSource.QUOTA_MET, BuffSource.BOSS_WON -> BuffPolarity.BUFF
            BuffSource.QUEST_MISSED, BuffSource.QUOTA_MISSED, BuffSource.BOSS_LOST -> BuffPolarity.DEBUFF
        }
        val buff = Buff(
            id = kotlin.random.Random.nextLong(),
            name = BUFF_NAME.getValue(source),
            polarity = polarity,
            statAffected = stat,
            modifier = BUFF_MODIFIER.getValue(source),
            expiresEpochDay = epochDay + BUFF_DURATION.getValue(source),
            source = source,
        )
        buffDao.upsert(buff.toEntity())
        return buff
    }
}
