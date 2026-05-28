package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.BuffRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import javax.inject.Inject

class CompleteQuestUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val statRepository: StatRepository,
    private val playerStateRepository: PlayerStateRepository,
    private val buffRepository: BuffRepository,
) {

    suspend operator fun invoke(quest: Quest, epochDay: Long) {
        // Idempotent: a quest already observed is not re-observed. Guards
        // against a double tap landing two completions before the flow re-emits.
        if (quest.status == QuestStatus.COMPLETED) return

        questRepository.complete(quest.id)
        statRepository.increment(quest.stat, quest.statGain)
        playerStateRepository.addXp(quest.xpReward)
        buffRepository.grant(BuffSource.QUEST_COMPLETED, quest.stat, epochDay)
    }
}
