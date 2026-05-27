package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.WeekDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.model.Week
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WeekRepository @Inject constructor(
    private val weekDao: WeekDao
) {

    suspend fun current(): Week? =
        weekDao.observeAll().first()
            .firstOrNull { !it.isResolved }
            ?.toDomain()

    // TODO: Stage 9 — real quota-checking logic against completed quests
    suspend fun unmetQuotas(): Map<StatType, Int> = emptyMap()
}
