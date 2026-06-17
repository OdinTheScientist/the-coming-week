package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.DayRecordRepository
import com.thecomingweek.domain.model.DayRecord
import javax.inject.Inject

class GetDayRecordUseCase @Inject constructor(
    private val dayRecordRepository: DayRecordRepository,
) {
    suspend operator fun invoke(epochDay: Long): DayRecord? =
        dayRecordRepository.getByEpochDay(epochDay)
}
