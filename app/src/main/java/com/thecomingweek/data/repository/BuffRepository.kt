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

class BuffRepository @Inject constructor(
    private val buffDao: BuffDao
) {

    fun observeActive(epochDay: Long): Flow<List<Buff>> =
        buffDao.observeAll().map { entities ->
            entities.filter { it.expiresEpochDay > epochDay }.map { it.toDomain() }
        }

    // TODO: Stage 8 — refine buff creation (name, modifier, polarity logic)
    suspend fun grant(source: BuffSource, stat: StatType, expiresEpochDay: Long) {
        val polarity = when (source) {
            BuffSource.QUEST_COMPLETED, BuffSource.QUOTA_MET, BuffSource.BOSS_WON -> BuffPolarity.BUFF
            BuffSource.QUEST_MISSED, BuffSource.QUOTA_MISSED, BuffSource.BOSS_LOST -> BuffPolarity.DEBUFF
        }
        val buff = Buff(
            id = System.currentTimeMillis(),
            name = source.name,
            polarity = polarity,
            statAffected = stat,
            modifier = 1,
            expiresEpochDay = expiresEpochDay,
            source = source
        )
        buffDao.upsert(buff.toEntity())
    }
}
