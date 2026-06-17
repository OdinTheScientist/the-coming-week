package com.thecomingweek.domain.model

data class CalendarDay(
    val epochDay: Long,
    val dayOfMonth: Int,
    val monthLabel: String?,
    val state: DayState,
    val isToday: Boolean,
)
