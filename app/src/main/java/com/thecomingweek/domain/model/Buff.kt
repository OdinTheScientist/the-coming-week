package com.thecomingweek.domain.model

data class Buff(
    val id: Long,
    val name: String,
    val polarity: BuffPolarity,
    val statAffected: StatType?,
    val modifier: Int,
    val expiresEpochDay: Long,
    val source: BuffSource
)
