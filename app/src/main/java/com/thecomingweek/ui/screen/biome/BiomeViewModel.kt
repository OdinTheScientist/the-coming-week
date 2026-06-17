package com.thecomingweek.ui.screen.biome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.BiomeRepository
import com.thecomingweek.data.repository.BossRepository
import com.thecomingweek.data.repository.DayRecordRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Biome
import com.thecomingweek.domain.model.Boss
import com.thecomingweek.domain.model.CalendarDay
import com.thecomingweek.domain.model.DayRecord
import com.thecomingweek.domain.model.DayState
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.Week
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

private const val MAX_FUTURE_WINDOWS = 3

@HiltViewModel
class BiomeViewModel @Inject constructor(
    biomeRepository: BiomeRepository,
    weekRepository: WeekRepository,
    bossRepository: BossRepository,
    dayRecordRepository: DayRecordRepository,
) : ViewModel() {

    data class UiState(
        val currentBiome: Biome? = null,
        val displayedBiome: Biome? = null,
        val allBiomes: List<Biome> = emptyList(),
        val dayRecords: List<DayRecord> = emptyList(),
        val calendarDays: List<CalendarDay> = emptyList(),
        val canPaginateBack: Boolean = false,
        val canPaginateForward: Boolean = false,
        val isLoading: Boolean = true,
    )

    private val _pageOffset = MutableStateFlow(0)

    val state: StateFlow<UiState> = combine(
        _pageOffset,
        biomeRepository.observeAll(),
        weekRepository.observeAll(),
        bossRepository.observeAll(),
        dayRecordRepository.observeAll(),
    ) { pageOffset, allBiomes, allWeeks, allBosses, allDayRecords ->
        if (allBiomes.isEmpty()) {
            return@combine UiState(isLoading = false)
        }

        val currentBiome = allBiomes.last()
        val today = LocalDate.now().toEpochDay()

        val isRealBiome = pageOffset <= 0
        val biomeIndex = allBiomes.lastIndex + pageOffset // pageOffset is 0 or negative for real biomes

        val displayedBiome: Biome?
        val startEpochDay: Long
        val calendarDays: List<CalendarDay>

        if (isRealBiome && biomeIndex >= 0) {
            displayedBiome = allBiomes[biomeIndex]
            startEpochDay = displayedBiome.startEpochDay
            val biomeDayRecords = allDayRecords.filter { it.biomeId == displayedBiome.id }
            val biomeWeeks = allWeeks.filter { it.biomeId == displayedBiome.id }
            calendarDays = buildCalendarDays(
                startEpochDay = startEpochDay,
                today = today,
                dayRecords = biomeDayRecords,
                weeks = biomeWeeks,
                bosses = allBosses,
            )
        } else {
            // Synthetic future window: pageOffset = +1 is the first window beyond current biome
            displayedBiome = null
            val futureOffset = pageOffset  // positive
            // currentBiome.startEpochDay + 42 * n gives the start of each future window
            startEpochDay = currentBiome.startEpochDay + 42L * futureOffset
            calendarDays = buildFutureCalendarDays(startEpochDay)
        }

        val canPaginateBack = if (isRealBiome) biomeIndex > 0 else true
        val canPaginateForward = pageOffset < MAX_FUTURE_WINDOWS

        UiState(
            currentBiome = currentBiome,
            displayedBiome = displayedBiome,
            allBiomes = allBiomes,
            dayRecords = allDayRecords.filter { it.biomeId == displayedBiome?.id },
            calendarDays = calendarDays,
            canPaginateBack = canPaginateBack,
            canPaginateForward = canPaginateForward,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState(isLoading = true))

    fun onPaginateBack() = _pageOffset.update { it - 1 }
    fun onPaginateForward() = _pageOffset.update { it + 1 }

    fun onDebugDayJump(epochDay: Long) {
        com.thecomingweek.debug.DebugClock.epochDayOverride = epochDay
    }
}

private fun buildCalendarDays(
    startEpochDay: Long,
    today: Long,
    dayRecords: List<DayRecord>,
    weeks: List<Week>,
    bosses: List<Boss>,
): List<CalendarDay> {
    val recordsByDay = dayRecords.associateBy { it.epochDay }
    val bossByWeekId = bosses.associateBy { it.weekId }

    var prevMonth: Month? = null
    return (0 until 42).map { offset ->
        val epochDay = startEpochDay + offset
        val date = LocalDate.ofEpochDay(epochDay)
        val month = date.month
        val monthLabel = if (month != prevMonth) month.name.take(3) else null
        prevMonth = month

        CalendarDay(
            epochDay = epochDay,
            dayOfMonth = date.dayOfMonth,
            monthLabel = monthLabel,
            state = deriveDayState(epochDay, today, date.dayOfWeek, recordsByDay, weeks, bossByWeekId),
            isToday = epochDay == today,
        )
    }
}

private fun buildFutureCalendarDays(startEpochDay: Long): List<CalendarDay> {
    var prevMonth: Month? = null
    return (0 until 42).map { offset ->
        val epochDay = startEpochDay + offset
        val date = LocalDate.ofEpochDay(epochDay)
        val month = date.month
        val monthLabel = if (month != prevMonth) month.name.take(3) else null
        prevMonth = month
        CalendarDay(
            epochDay = epochDay,
            dayOfMonth = date.dayOfMonth,
            monthLabel = monthLabel,
            state = DayState.FUTURE,
            isToday = false,
        )
    }
}

private fun deriveDayState(
    epochDay: Long,
    today: Long,
    dayOfWeek: DayOfWeek,
    recordsByDay: Map<Long, DayRecord>,
    weeks: List<Week>,
    bossByWeekId: Map<Long, Boss>,
): DayState {
    if (epochDay > today) return DayState.FUTURE

    // Sunday: show boss state based on whether the week trial was resolved
    if (dayOfWeek == DayOfWeek.SUNDAY) {
        val week = weeks.firstOrNull { epochDay in it.startEpochDay..it.endEpochDay }
        if (week != null) {
            val boss = bossByWeekId[week.id]
            return when (boss?.defeated) {
                true -> DayState.BOSS_WON
                false -> DayState.BOSS_LOST
                null -> DayState.BOSS
            }
        }
        return DayState.BOSS
    }

    if (epochDay == today) return DayState.TODAY

    val record = recordsByDay[epochDay]
    if (record != null) {
        val statuses = record.quests.map { it.status }
        return when {
            statuses.isNotEmpty() && statuses.all { it == QuestStatus.COMPLETED } -> DayState.COMPLETE
            statuses.any { it == QuestStatus.COMPLETED } -> DayState.PARTIAL
            else -> DayState.MISSED
        }
    }

    return DayState.MISSED
}
