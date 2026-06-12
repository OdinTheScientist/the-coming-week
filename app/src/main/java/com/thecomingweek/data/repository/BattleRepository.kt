package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BattleResultDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.BattleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BattleRepository @Inject constructor(
    private val battleResultDao: BattleResultDao
) {

    fun observeByEpochDay(epochDay: Long): Flow<BattleResult?> =
        battleResultDao.observeByEpochDay(epochDay).map { it?.toDomain() }

    suspend fun getByEpochDay(epochDay: Long): BattleResult? =
        battleResultDao.getByEpochDay(epochDay)?.toDomain()

    suspend fun upsert(result: BattleResult) = battleResultDao.upsert(result.toEntity())
}
