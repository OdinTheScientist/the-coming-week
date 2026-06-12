package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatQuestEntry
import com.thecomingweek.domain.model.StatType
import javax.inject.Inject

class GetQuestsByStatUseCase @Inject constructor(
    private val questRepository: QuestRepository
) {

    suspend operator fun invoke(stat: StatType, epochDay: Long): List<StatQuestEntry> {
        val pool = questRepository.pool()
        val templates = pool.filter {
            it.type == QuestType.DAILY && it.dayAssigned == null && it.stat == stat
        }
        val drawnToday = pool.filter { it.dayAssigned == epochDay }

        return templates
            .map { template ->
                val isDrawn = drawnToday.any { it.id.startsWith("${template.id}_") }
                StatQuestEntry(quest = template, isDrawnThisWeek = isDrawn)
            }
            .sortedWith(
                compareByDescending<StatQuestEntry> { it.isDrawnThisWeek }
                    .thenByDescending { it.quest.weight }
            )
    }
}
