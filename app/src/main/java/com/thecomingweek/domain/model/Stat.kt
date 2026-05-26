package com.thecomingweek.domain.model

data class Stat(
    val type: StatType,
    val value: Int,
    val weeklyGain: Int
)
