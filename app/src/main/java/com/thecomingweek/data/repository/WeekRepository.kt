package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.QuestDao
import com.thecomingweek.data.local.dao.WeekDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.Week
import com.thecomingweek.domain.usecase.internal.computeQuotaProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeekRepository @Inject constructor(
    private val weekDao: WeekDao,
    private val questDao: QuestDao,
) {

    fun observeCurrent(): Flow<Week?> =
        weekDao.observeAll().map { weeks ->
            weeks.firstOrNull { !it.isResolved }?.toDomain()
        }

    suspend fun current(): Week? =
        weekDao.observeAll().first()
            .firstOrNull { !it.isResolved }
            ?.toDomain()

    suspend fun upsert(week: Week) = weekDao.upsert(week.toEntity())

    suspend fun markResolved(id: Long) = weekDao.updateResolved(id, true)

    suspend fun setRerollsRemaining(weekId: Long, count: Int) =
        weekDao.updateRerollsRemaining(weekId, count)

    // Count of stats whose weekly quota is unmet. Shares the counting rule with
    // CheckWeeklyQuotasUseCase through computeQuotaProgress rather than calling
    // the use case — a repository must not depend on a use case. Stage 10's boss
    // difficulty reads this.
    suspend fun unmetQuotas(): Int {
        val week = current() ?: return 0
        val quests = questDao.observeAll().first().map { it.toDomain() }
        return computeQuotaProgress(week, quests).count { !it.met }
    }
}
