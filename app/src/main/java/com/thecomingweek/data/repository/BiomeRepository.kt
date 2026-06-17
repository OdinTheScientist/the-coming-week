package com.thecomingweek.data.repository

import com.thecomingweek.data.local.dao.BiomeDao
import com.thecomingweek.data.mapper.toDomain
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.Biome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BiomeRepository @Inject constructor(
    private val biomeDao: BiomeDao
) {

    fun observeAll(): Flow<List<Biome>> =
        biomeDao.observeAll().map { biomes -> biomes.sortedBy { it.id }.map { it.toDomain() } }

    // Highest id is the current descent: ResetRunUseCase always writes the new
    // biome at old.id + 1, so a fresh descent supersedes the retired one.
    fun observeCurrent(): Flow<Biome?> =
        biomeDao.observeAll().map { biomes -> biomes.maxByOrNull { it.id }?.toDomain() }

    suspend fun current(): Biome? =
        biomeDao.observeAll().first()
            .maxByOrNull { it.id }
            ?.toDomain()

    suspend fun upsert(biome: Biome) = biomeDao.upsert(biome.toEntity())

    suspend fun markCompleted(id: Long) = biomeDao.updateCompleted(id, true)

    // The biome's contribution to boss difficulty.
    //   biomeModifier = weekNumber * 2
    //
    // Bosses get harder deeper into a biome: the same Warden that asks little in
    // Week 1 (+2) is a far heavier weight by the final week (+12), so a descent
    // tightens as it goes even before stat growth and quota penalties are added.
    // The factor of 2 keeps it a meaningful but not dominant term against the
    // base difficulty (10) and a growing player score.
    //
    // biomeId is taken but unused for MVP — there is one authored biome. It is
    // kept in the signature so post-MVP environmental modifiers (a harsher biome
    // adding a flat surcharge, etc.) have a seam to slot into without churn.
    @Suppress("UNUSED_PARAMETER")
    fun modifierFor(biomeId: Long, weekNumber: Int): Int = weekNumber * 2
}
