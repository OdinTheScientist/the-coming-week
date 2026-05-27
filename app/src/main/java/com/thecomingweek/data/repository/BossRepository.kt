package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BossDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.domain.model.Boss
import javax.inject.Inject

class BossRepository @Inject constructor(
    private val bossDao: BossDao
) {

    suspend fun getForWeek(weekId: Long): Boss? =
        bossDao.getByWeekId(weekId)?.toDomain()
}
