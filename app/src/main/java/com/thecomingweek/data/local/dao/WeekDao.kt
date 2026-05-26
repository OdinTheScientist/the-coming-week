package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.WeekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {

    @Query("SELECT * FROM weeks")
    fun observeAll(): Flow<List<WeekEntity>>

    @Query("SELECT * FROM weeks WHERE id = :id")
    suspend fun getById(id: Long): WeekEntity?

    @Upsert
    suspend fun upsert(week: WeekEntity)

    @Query("UPDATE weeks SET isResolved = :resolved WHERE id = :id")
    suspend fun updateResolved(id: Long, resolved: Boolean)
}
