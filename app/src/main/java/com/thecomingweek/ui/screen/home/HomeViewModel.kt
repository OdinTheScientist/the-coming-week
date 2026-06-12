package com.thecomingweek.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.BattleRepository
import com.thecomingweek.data.repository.BuffRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Buff
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.usecase.CompleteQuestUseCase
import com.thecomingweek.domain.usecase.DrawDailyQuestsUseCase
import com.thecomingweek.domain.usecase.RerollQuestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

private val AUTO_BATTLE_HOUR = LocalTime.of(22, 0)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val drawDailyQuests: DrawDailyQuestsUseCase,
    private val completeQuest: CompleteQuestUseCase,
    private val rerollQuest: RerollQuestUseCase,
    private val questRepository: QuestRepository,
    private val weekRepository: WeekRepository,
    private val buffRepository: BuffRepository,
    private val battleRepository: BattleRepository,
    private val playerStateRepository: PlayerStateRepository,
) : ViewModel() {

    data class UiState(
        val today: List<Quest> = emptyList(),
        val activeBuffs: List<Buff> = emptyList(),
        val daysUntilTrial: Int = 0,
        val currentHp: Int = 0,
        val maxHp: Int = 0,
        val battleResolved: Boolean = false,
        val rerollsRemaining: Int = 3,
        val isRerollMode: Boolean = false,
        val isLoading: Boolean = true,
    )

    private val epochDay: Long = LocalDate.now().toEpochDay()

    // Guards the daily draw so it fires at most once per day. Mutated only from
    // the single trigger collector below, where Flow collection is sequential,
    // so a plain var is safe and cannot double-draw.
    private var drawAttempted = false

    // Reroll Mode is a transient UI toggle, not persisted state — tracked
    // separately and merged onto the reactive snapshot below.
    private val _isRerollMode = MutableStateFlow(false)

    val state: StateFlow<UiState> =
        combine(
            combine(
                questRepository.observeToday(epochDay),
                buffRepository.observeActive(epochDay),
                battleRepository.observeByEpochDay(epochDay),
                playerStateRepository.observe(),
                weekRepository.observeCurrent(),
            ) { today, buffs, battle, playerState, week ->
                UiState(
                    today = today,
                    activeBuffs = buffs,
                    daysUntilTrial = daysUntilTrial(epochDay),
                    currentHp = playerState?.currentHp ?: 0,
                    maxHp = playerState?.maxHp ?: 0,
                    battleResolved = battle != null,
                    rerollsRemaining = week?.rerollsRemaining ?: 3,
                    isLoading = today.isEmpty(),
                )
            },
            _isRerollMode,
        ) { base, rerollMode -> base.copy(isRerollMode = rerollMode) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState(isLoading = true),
            )

    // One-shot: fires at most once per app open, when the hour is late and
    // today's battle has not yet been fought. The screen navigates to the
    // Battle screen before Home renders.
    private val _navigateToBattle = Channel<Unit>(Channel.BUFFERED)
    val navigateToBattle = _navigateToBattle.receiveAsFlow()

    init {
        triggerDailyDraw()
        checkAutoBattleTrigger()
    }

    // Completes a quest, then lets the reactive pipeline carry the change to the
    // UI: the use case persists (status, stat, XP, buff), observeToday re-emits
    // with the quest now COMPLETED, and state recomposes. No manual mutation.
    fun onQuestCompleted(quest: Quest) {
        viewModelScope.launch {
            completeQuest(quest, epochDay)
        }
    }

    // Enters/exits Reroll Mode. Tapping the Fates button or the cancel line
    // both flip this; no other state changes.
    fun onRerollModeToggle() {
        _isRerollMode.update { !it }
    }

    // Replaces one of today's available quests, then exits Reroll Mode. The
    // reactive pipeline (observeToday + observeCurrent) carries the new quest
    // and the decremented count to the UI.
    fun onRerollQuest(questId: String) {
        viewModelScope.launch {
            rerollQuest(questId, epochDay)
            _isRerollMode.value = false
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

    // The week comes regardless of the hour: if it is late and today's battle
    // is still unfought, the day's reckoning is brought to the player rather
    // than waited on.
    private fun checkAutoBattleTrigger() {
        viewModelScope.launch {
            if (LocalTime.now() < AUTO_BATTLE_HOUR) return@launch
            if (battleRepository.getByEpochDay(epochDay) == null) {
                _navigateToBattle.send(Unit)
            }
        }
    }

    // Sunday is the Trial. 0 when today is Sunday.
    private fun daysUntilTrial(epochDay: Long): Int {
        val today = LocalDate.ofEpochDay(epochDay).dayOfWeek
        return (DayOfWeek.SUNDAY.value - today.value + 7) % 7
    }
}
