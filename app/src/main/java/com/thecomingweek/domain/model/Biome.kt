package com.thecomingweek.domain.model

data class Biome(
    val id: Long,
    val name: String,
    val flavor: String,
    val weekCount: Int,
    val startEpochDay: Long,
    val finalBossId: Long?
)
