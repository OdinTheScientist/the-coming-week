package com.thecomingweek.domain.model

data class Boss(
    val id: Long,
    val weekId: Long,
    val biomeId: Long,
    val name: String,
    val flavor: String,
    val baseDifficulty: Int,
    val finalDifficulty: Int,
    val defeated: Boolean?
)
