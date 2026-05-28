package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.domain.model.StatType
import kotlinx.coroutines.flow.Flow

@Dao
interface StatDao {

    @Query("SELECT * FROM stats")
    fun observeAll(): Flow<List<StatEntity>>

    @Query("SELECT * FROM stats WHERE type = :type")
    suspend fun getByType(type: StatType): StatEntity?

    @Upsert
    suspend fun upsert(stat: StatEntity)

    @Query("UPDATE stats SET weeklyGain = 0")
    suspend fun resetWeeklyGains()
}
