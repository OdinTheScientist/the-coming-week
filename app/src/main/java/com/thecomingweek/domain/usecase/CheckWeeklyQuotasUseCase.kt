package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.domain.model.Week
import com.thecomingweek.domain.model.WeeklyQuotaReport
import com.thecomingweek.domain.usecase.internal.computeQuotaProgress
import javax.inject.Inject

class CheckWeeklyQuotasUseCase @Inject constructor(
    private val questRepository: QuestRepository,
) {

    // Takes the week as input so the caller (which already observes the current
    // week reactively) need not re-read it. Counting is delegated to the shared
    // pure helper; this use case only fetches the quests it operates on.
    suspend operator fun invoke(week: Week): WeeklyQuotaReport {
        val quests = questRepository.pool()
        return WeeklyQuotaReport(computeQuotaProgress(week, quests))
    }
}
