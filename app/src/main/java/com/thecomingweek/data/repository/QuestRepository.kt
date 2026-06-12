package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.QuestDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questDao: QuestDao
) {

    fun observeToday(epochDay: Long): Flow<List<Quest>> =
        questDao.observeAll().map { entities ->
            entities.filter { it.dayAssigned == epochDay }.map { it.toDomain() }
        }

    fun observeAll(): Flow<List<Quest>> =
        questDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    suspend fun pool(): List<Quest> =
        questDao.observeAll().first().map { it.toDomain() }

    suspend fun seedDailyDraw(quests: List<Quest>) {
        quests.forEach { questDao.upsert(it.toEntity()) }
    }

    suspend fun complete(id: String) {
        questDao.updateStatus(id, QuestStatus.COMPLETED)
    }

    suspend fun miss(id: String) {
        questDao.updateStatus(id, QuestStatus.MISSED)
    }

    suspend fun delete(id: String) {
        questDao.deleteById(id)
    }
}
