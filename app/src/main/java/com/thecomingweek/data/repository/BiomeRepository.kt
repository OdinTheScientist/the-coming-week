package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BiomeDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.domain.model.Biome
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BiomeRepository @Inject constructor(
    private val biomeDao: BiomeDao
) {

    suspend fun current(): Biome? =
        biomeDao.observeAll().first()
            .maxByOrNull { it.id }
            ?.toDomain()

    // TODO: Stage 10 — real biome modifier calculation
    suspend fun modifierFor(biomeId: Long): Int = 0
}
