package com.thecomingweek.domain.model

data class Biome(
    val id: Long,
    val name: String,
    val flavor: String,
    val weekCount: Int,
    val startEpochDay: Long,
    val finalBossId: Long?,
    // True once the run has descended through all of the biome's weeks and reset
    // past it. Set by ResetRunUseCase at the biome boundary. The current biome is
    // still resolved by highest id (a new biome supersedes the old), so this is
    // record-keeping for now — it makes a completed descent legible in storage
    // and gives post-MVP biome rotation/history something to read.
    val isCompleted: Boolean = false,
)
