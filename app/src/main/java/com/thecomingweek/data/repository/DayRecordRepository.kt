package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.DayRecordDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.DayRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DayRecordRepository @Inject constructor(
    private val dayRecordDao: DayRecordDao,
) {

    suspend fun upsert(record: DayRecord) = dayRecordDao.upsert(record.toEntity())

    suspend fun getByEpochDay(epochDay: Long): DayRecord? =
        dayRecordDao.getByEpochDay(epochDay)?.toDomain()

    fun observeByEpochDay(epochDay: Long): Flow<DayRecord?> =
        dayRecordDao.observeByEpochDay(epochDay).map { it?.toDomain() }

    suspend fun getByBiomeId(biomeId: Long): List<DayRecord> =
        dayRecordDao.getByBiomeId(biomeId).map { it.toDomain() }

    fun observeByBiomeId(biomeId: Long): Flow<List<DayRecord>> =
        dayRecordDao.observeByBiomeId(biomeId).map { entities -> entities.map { it.toDomain() } }

    fun observeAll(): Flow<List<DayRecord>> =
        dayRecordDao.observeAll().map { entities -> entities.map { it.toDomain() } }
}
