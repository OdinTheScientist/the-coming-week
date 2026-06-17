package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.DayRecordRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.domain.model.DayRecord
import javax.inject.Inject

class SaveDayNoteUseCase @Inject constructor(
    private val dayRecordRepository: DayRecordRepository,
    private val playerStateRepository: PlayerStateRepository,
) {
    suspend operator fun invoke(epochDay: Long, note: String) {
        val existing = dayRecordRepository.getByEpochDay(epochDay)
        if (existing != null) {
            dayRecordRepository.upsert(existing.copy(note = note))
        } else {
            val playerState = playerStateRepository.get()
            dayRecordRepository.upsert(
                DayRecord(
                    epochDay = epochDay,
                    biomeId = playerState?.currentBiomeId ?: 0L,
                    weekId = playerState?.currentWeekId ?: 0L,
                    quests = emptyList(),
                    battleOutcome = null,
                    hpBefore = 0,
                    hpAfter = 0,
                    note = note,
                )
            )
        }
    }
}
