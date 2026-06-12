package com.thecomingweek.ui.screen.battle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.BattleRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.BattleOutcome
import com.thecomingweek.domain.model.BattleRound
import com.thecomingweek.domain.usecase.ResolveDailyBattleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val resolveDailyBattle: ResolveDailyBattleUseCase,
    private val battleRepository: BattleRepository,
    private val playerStateRepository: PlayerStateRepository,
    private val weekRepository: WeekRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Debug bypass: a long press on Home's battle button forces a re-fight even
    // if today's battle was already resolved.
    private val force: Boolean = savedStateHandle.get<Boolean>("force") ?: false

    data class UiState(
        val enemyName: String = "",
        val playerHp: Int = 0,
        val playerMaxHp: Int = 0,
        val enemyHp: Int = 0,
        val enemyMaxHp: Int = 0,
        val rounds: List<BattleRound> = emptyList(),
        val outcome: BattleOutcome? = null,
        val isLoading: Boolean = true,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    // The day's battle is resolved once, then re-displayed: if today's result
    // already exists (normal re-entry, or the debug long-press bypass), show
    // it as-is rather than fighting again.
    init {
        viewModelScope.launch {
            val epochDay = LocalDate.now().toEpochDay()
            val week = weekRepository.current()
            val playerState = playerStateRepository.get()
            if (week == null || playerState == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val result = (if (force) null else battleRepository.getByEpochDay(epochDay))
                ?: resolveDailyBattle(epochDay)
                ?: run {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

            val enemyMaxHp = 5 + week.weekNumber * 2
            val enemyHp = if (result.outcome == BattleOutcome.VICTORY) {
                0
            } else {
                result.rounds.lastOrNull()?.enemyHpAfter ?: enemyMaxHp
            }

            _state.update {
                it.copy(
                    enemyName = result.enemyName,
                    playerHp = result.playerHpAfter,
                    playerMaxHp = playerState.maxHp,
                    enemyHp = enemyHp,
                    enemyMaxHp = enemyMaxHp,
                    rounds = result.rounds,
                    outcome = result.outcome,
                    isLoading = false,
                )
            }
        }
    }
}
