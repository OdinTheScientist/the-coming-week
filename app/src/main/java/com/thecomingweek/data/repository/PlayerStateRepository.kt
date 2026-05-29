package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.PlayerStateDao
import com.thecomingweek.data.local.dao.StatDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.domain.model.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlayerStateRepository @Inject constructor(
    private val playerStateDao: PlayerStateDao,
    private val statDao: StatDao
) {

    fun observe(): Flow<PlayerState?> =
        playerStateDao.observe().combine(statDao.observeAll()) { playerStates, stats ->
            playerStates.firstOrNull()?.toDomain(stats)
        }

    suspend fun get(): PlayerState? {
        val entity = playerStateDao.get() ?: return null
        val stats = statDao.observeAll().first()
        return entity.toDomain(stats)
    }

    suspend fun addXp(amount: Int) {
        val current = playerStateDao.get() ?: return
        playerStateDao.upsert(current.copy(xp = current.xp + amount))
    }

    suspend fun setCurrentWeek(weekId: Long) {
        val current = playerStateDao.get() ?: return
        playerStateDao.upsert(current.copy(currentWeekId = weekId))
    }

    // The roguelite reset. Increments the run counter and re-points the player
    // at the new biome's first week. Stats are NOT touched here — they live in
    // their own table and persist across runs by design (see ResetRunUseCase).
    suspend fun startNewRun(biomeId: Long, weekId: Long) {
        val current = playerStateDao.get() ?: return
        playerStateDao.upsert(
            current.copy(
                runNumber = current.runNumber + 1,
                currentBiomeId = biomeId,
                currentWeekId = weekId,
            )
        )
    }
}
