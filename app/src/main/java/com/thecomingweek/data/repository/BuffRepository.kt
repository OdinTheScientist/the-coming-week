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

    // TODO: buff lifecycle is unimplemented and currently BROKEN. Callers pass
    // the current epochDay as `expiresEpochDay`, but `observeActive` filters for
    // `expiresEpochDay > epochDay` — so every granted buff is already expired the
    // moment it is written. Buffs are dead on arrival and never surface as active.
    // Duration is not modeled at all, and `modifier` is a hardcoded 1 that does
    // not yet mean anything. Before buffs are displayed or consumed anywhere
    // (Home's activeBuffs list, and combat resolution at Stage 10 — daily battle
    // and the weekly boss), the buff lifecycle needs a design pass: how long a
    // buff lasts, what each buff actually modifies, and stacking rules.
    // Returns the granted Buff so callers that want to surface it (e.g. the
    // Trial's TrialResult) can, without rebuilding it. The lifecycle is still
    // BROKEN per the note above — returning the value does not change that; the
    // buff is written already-expired and will not show as active.
    suspend fun grant(source: BuffSource, stat: StatType, expiresEpochDay: Long): Buff {
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
        return buff
    }
}
