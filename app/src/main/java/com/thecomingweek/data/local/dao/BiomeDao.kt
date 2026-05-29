package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.BiomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BiomeDao {

    @Query("SELECT * FROM biomes")
    fun observeAll(): Flow<List<BiomeEntity>>

    @Query("SELECT * FROM biomes WHERE id = :id")
    suspend fun getById(id: Long): BiomeEntity?

    @Upsert
    suspend fun upsert(biome: BiomeEntity)

    @Query("UPDATE biomes SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean)
}
