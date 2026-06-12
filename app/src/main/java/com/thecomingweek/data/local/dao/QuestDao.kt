package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.QuestEntity
import com.thecomingweek.domain.model.QuestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests")
    fun observeAll(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :id")
    suspend fun getById(id: String): QuestEntity?

    @Query("SELECT * FROM quests WHERE dayAssigned = :epochDay")
    suspend fun getByDay(epochDay: Long): List<QuestEntity>

    @Upsert
    suspend fun upsert(quest: QuestEntity)

    @Query("UPDATE quests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: QuestStatus)

    @Query("DELETE FROM quests WHERE id = :id")
    suspend fun deleteById(id: String)
}
