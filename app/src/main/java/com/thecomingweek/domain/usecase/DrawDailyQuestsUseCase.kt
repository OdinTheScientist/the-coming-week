package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.usecase.internal.weightedDraw
import javax.inject.Inject

class DrawDailyQuestsUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val weekRepository: WeekRepository
) {

    suspend operator fun invoke(epochDay: Long): List<Quest> {
        val week = weekRepository.current() ?: return emptyList()
        val pool = questRepository.pool()
            .filter { it.type == QuestType.DAILY && it.dayAssigned == null }
        if (pool.isEmpty()) return emptyList()

        val weighted = pool.map { quest ->
            val w = if (quest.stat == week.statTheme) {
                quest.weight * 2.0
            } else {
                quest.weight.toDouble()
            }
            quest to w
        }

        val drawn = weightedDraw(weighted, 3).map { quest ->
            // Drawn instances are suffixed "{poolId}_{epochDay}" so the pool
            // template is preserved for reuse and each day's draw is uniquely
            // keyed. Code tracing an instance to its template relies on this.
            quest.copy(
                id = "${quest.id}_$epochDay",
                dayAssigned = epochDay,
                status = QuestStatus.AVAILABLE
            )
        }

        questRepository.seedDailyDraw(drawn)
        return drawn
    }
}
