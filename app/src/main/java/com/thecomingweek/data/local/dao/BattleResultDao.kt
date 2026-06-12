package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.BattleResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BattleResultDao {

    @Query("SELECT * FROM battle_results WHERE epochDay = :epochDay")
    fun observeByEpochDay(epochDay: Long): Flow<BattleResultEntity?>

    @Query("SELECT * FROM battle_results WHERE epochDay = :epochDay")
    suspend fun getByEpochDay(epochDay: Long): BattleResultEntity?

    @Upsert
    suspend fun upsert(result: BattleResultEntity)
}
