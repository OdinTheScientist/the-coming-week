package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BossDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.Boss
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BossRepository @Inject constructor(
    private val bossDao: BossDao
) {

    fun observeAll(): Flow<List<Boss>> =
        bossDao.observeAll().map { bosses -> bosses.map { it.toDomain() } }

    // Upsert (not updateResult): MVP has no seeded bosses, so the placeholder
    // Warden is written for the first time at resolution. Its id mirrors the
    // week id, so re-resolving the same week overwrites rather than duplicates.
    suspend fun upsert(boss: Boss) = bossDao.upsert(boss.toEntity())
}
