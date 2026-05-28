package com.thecomingweek.domain.usecase.internal

import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuotaProgress
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.model.Week

// Single source of the quota rule, shared by CheckWeeklyQuotasUseCase and
// WeekRepository.unmetQuotas so neither restates the counting. A quest counts
// when it is COMPLETED and its assigned day falls inside the week's window.
// Only stats that carry a quota produce a row, in canonical StatType order.
internal fun computeQuotaProgress(week: Week, quests: List<Quest>): List<QuotaProgress> {
    // The date-range check assumes week boundaries are calendar-aligned: a
    // completion credits its quota cleanly only when advancement is itself
    // calendar-aligned. After an early manual advance a completion can fall in
    // the resolved week's range rather than the current week's — a bounded
    // display lag, not data loss. See docs/roadmap.md "Deferred / post-MVP".
    val completedByStat = quests
        .filter { quest ->
            quest.status == QuestStatus.COMPLETED &&
                quest.dayAssigned != null &&
                quest.dayAssigned in week.startEpochDay..week.endEpochDay
        }
        .groupingBy { it.stat }
        .eachCount()

    return StatType.entries.mapNotNull { stat ->
        val required = week.quotas[stat] ?: return@mapNotNull null
        QuotaProgress(
            stat = stat,
            completed = completedByStat[stat] ?: 0,
            required = required,
        )
    }
}
