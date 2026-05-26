package com.thecomingweek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thecomingweek.data.local.entity.PlayerStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStateDao {

    @Query("SELECT * FROM player_state")
    fun observe(): Flow<List<PlayerStateEntity>>

    @Query("SELECT * FROM player_state WHERE id = 1")
    suspend fun get(): PlayerStateEntity?

    @Upsert
    suspend fun upsert(state: PlayerStateEntity)
}
