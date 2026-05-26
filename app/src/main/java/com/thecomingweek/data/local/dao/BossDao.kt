package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.BossEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BossDao {

    @Query("SELECT * FROM bosses")
    fun observeAll(): Flow<List<BossEntity>>

    @Query("SELECT * FROM bosses WHERE weekId = :weekId")
    suspend fun getByWeekId(weekId: Long): BossEntity?

    @Upsert
    suspend fun upsert(boss: BossEntity)

    @Query("UPDATE bosses SET defeated = :defeated, finalDifficulty = :finalDifficulty WHERE id = :id")
    suspend fun updateResult(id: Long, defeated: Boolean, finalDifficulty: Int)
}
