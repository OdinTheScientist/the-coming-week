package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.StatDao
import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.domain.model.Stat
import com.thecomingweek.domain.model.StatType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StatRepository @Inject constructor(
    private val statDao: StatDao
) {

    fun observeAll(): Flow<List<Stat>> =
        statDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    suspend fun all(): List<Stat> =
        statDao.observeAll().first().map { it.toDomain() }

    suspend fun increment(type: StatType, delta: Int) {
        val current = statDao.getByType(type) ?: StatEntity(type = type, value = 0, weeklyGain = 0)
        statDao.upsert(current.copy(value = current.value + delta, weeklyGain = current.weeklyGain + delta))
    }
}
