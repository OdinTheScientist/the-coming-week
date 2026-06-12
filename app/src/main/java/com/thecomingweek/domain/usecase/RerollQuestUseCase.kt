package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.usecase.internal.weightedDraw
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// The Fates' bargain: discard one of today's untouched quests for a fresh
// draw, weighted the same way the original draw was. Bounded by the week's
// reroll allowance — see docs/roadmap.md Stage 12.
class RerollQuestUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val weekRepository: WeekRepository,
) {

    suspend operator fun invoke(questId: String, epochDay: Long): Quest? {
        val week = weekRepository.current() ?: return null
        if (week.rerollsRemaining <= 0) return null

        val today = questRepository.observeToday(epochDay).first()
        val target = today.firstOrNull { it.id == questId } ?: return null
        if (target.status != QuestStatus.AVAILABLE) return null

        // The other drawn quests are off-limits for the replacement, so the
        // same quest cannot appear twice in one day's draw.
        val drawnPoolIds = today.filter { it.id != questId }
            .map { it.id.removeSuffix("_$epochDay") }
            .toSet()

        val pool = questRepository.pool()
            .filter { it.type == QuestType.DAILY && it.dayAssigned == null && it.id !in drawnPoolIds }
        if (pool.isEmpty()) return null

        val weighted = pool.map { quest ->
            val w = if (quest.stat == week.statTheme) {
                quest.weight * 2.0
            } else {
                quest.weight.toDouble()
            }
            quest to w
        }

        val replacement = weightedDraw(weighted, 1).firstOrNull() ?: return null
        val drawn = replacement.copy(
            id = "${replacement.id}_$epochDay",
            dayAssigned = epochDay,
            status = QuestStatus.AVAILABLE,
        )

        questRepository.delete(target.id)
        questRepository.seedDailyDraw(listOf(drawn))
        weekRepository.setRerollsRemaining(week.id, week.rerollsRemaining - 1)

        return drawn
    }
}
