package com.thecomingweek.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.BuffRepository
import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Buff
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.usecase.CompleteQuestUseCase
import com.thecomingweek.domain.usecase.DrawDailyQuestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val drawDailyQuests: DrawDailyQuestsUseCase,
    private val completeQuest: CompleteQuestUseCase,
    private val questRepository: QuestRepository,
    private val weekRepository: WeekRepository,
    private val buffRepository: BuffRepository,
) : ViewModel() {

    data class UiState(
        val today: List<Quest> = emptyList(),
        val activeBuffs: List<Buff> = emptyList(),
        val daysUntilTrial: Int = 0,
        val isLoading: Boolean = true,
    )

    private val epochDay: Long = LocalDate.now().toEpochDay()

    // Guards the daily draw so it fires at most once per day. Mutated only from
    // the single trigger collector below, where Flow collection is sequential,
    // so a plain var is safe and cannot double-draw.
    private var drawAttempted = false

    val state: StateFlow<UiState> =
        combine(
            questRepository.observeToday(epochDay),
            buffRepository.observeActive(epochDay),
        ) { today, buffs ->
            UiState(
                today = today,
                activeBuffs = buffs,
                daysUntilTrial = daysUntilTrial(epochDay),
                isLoading = today.isEmpty(),
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState(isLoading = true),
            )

    init {
        triggerDailyDraw()
    }

    // Completes a quest, then lets the reactive pipeline carry the change to the
    // UI: the use case persists (status, stat, XP, buff), observeToday re-emits
    // with the quest now COMPLETED, and state recomposes. No manual mutation.
    fun onQuestCompleted(quest: Quest) {
        viewModelScope.launch {
            completeQuest(quest, epochDay)
        }
    }

    // Reactive draw trigger. Observes today's quests independently of the UI
    // subscription so the draw (and the first-launch seed it depends on) runs
    // as soon as the VM exists. Dissolves the seed/read race: on a genuine
    // first launch the seed runs fire-and-forget on a background thread, so an
    // imperative read could beat it. Here we react — when today is empty we
    // only draw once the week exists (proof the seed landed); if it has not, we
    // do nothing and wait for the Flow to re-emit when the seed writes appear.
    private fun triggerDailyDraw() {
        questRepository.observeToday(epochDay)
            .onEach { today ->
                if (today.isEmpty() && !drawAttempted) {
                    val week = weekRepository.current()
                    if (week != null) {
                        drawAttempted = true
                        drawDailyQuests(epochDay)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    // Sunday is the Trial. 0 when today is Sunday.
    private fun daysUntilTrial(epochDay: Long): Int {
        val today = LocalDate.ofEpochDay(epochDay).dayOfWeek
        return (DayOfWeek.SUNDAY.value - today.value + 7) % 7
    }
}
