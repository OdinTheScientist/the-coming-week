package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.BuffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuffDao {

    @Query("SELECT * FROM buffs")
    fun observeAll(): Flow<List<BuffEntity>>

    @Query("SELECT * FROM buffs WHERE expiresEpochDay > :currentEpochDay")
    suspend fun getActive(currentEpochDay: Long): List<BuffEntity>

    @Upsert
    suspend fun upsert(buff: BuffEntity)

    @Query("DELETE FROM buffs WHERE expiresEpochDay <= :currentEpochDay")
    suspend fun deleteExpired(currentEpochDay: Long)

    @Transaction
    suspend fun pruneAndGetActive(epochDay: Long): List<BuffEntity> {
        deleteExpired(epochDay)
        return getActive(epochDay)
    }
}
