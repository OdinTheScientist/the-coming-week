package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.DayRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayRecordDao {

    @Upsert
    suspend fun upsert(record: DayRecordEntity)

    @Query("SELECT * FROM day_records WHERE epochDay = :epochDay")
    suspend fun getByEpochDay(epochDay: Long): DayRecordEntity?

    @Query("SELECT * FROM day_records WHERE epochDay = :epochDay")
    fun observeByEpochDay(epochDay: Long): Flow<DayRecordEntity?>

    @Query("SELECT * FROM day_records WHERE biomeId = :biomeId")
    suspend fun getByBiomeId(biomeId: Long): List<DayRecordEntity>

    @Query("SELECT * FROM day_records WHERE biomeId = :biomeId")
    fun observeByBiomeId(biomeId: Long): Flow<List<DayRecordEntity>>

    @Query("SELECT * FROM day_records")
    fun observeAll(): Flow<List<DayRecordEntity>>
}
