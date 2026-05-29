package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BossDao
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.Boss
import javax.inject.Inject

class BossRepository @Inject constructor(
    private val bossDao: BossDao
) {

    // Upsert (not updateResult): MVP has no seeded bosses, so the placeholder
    // Warden is written for the first time at resolution. Its id mirrors the
    // week id, so re-resolving the same week overwrites rather than duplicates.
    suspend fun upsert(boss: Boss) = bossDao.upsert(boss.toEntity())
}
