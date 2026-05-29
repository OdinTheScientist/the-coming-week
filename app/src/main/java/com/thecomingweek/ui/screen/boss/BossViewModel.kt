package com.thecomingweek.ui.screen.boss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Boss
import com.thecomingweek.domain.model.TrialResult
import com.thecomingweek.domain.usecase.CalculateBossDifficultyUseCase
import com.thecomingweek.domain.usecase.CheckWeeklyQuotasUseCase
import com.thecomingweek.domain.usecase.ResolveWeeklyBossUseCase
import com.thecomingweek.domain.usecase.internal.WARDEN_ART
import com.thecomingweek.domain.usecase.internal.placeholderBoss
import com.thecomingweek.domain.usecase.internal.playerScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BossViewModel @Inject constructor(
    private val weekRepository: WeekRepository,
    private val statRepository: StatRepository,
    private val checkWeeklyQuotas: CheckWeeklyQuotasUseCase,
    private val calculateBossDifficulty: CalculateBossDifficultyUseCase,
    private val resolveWeeklyBoss: ResolveWeeklyBossUseCase,
) : ViewModel() {

    data class UiState(
        val boss: Boss? = null,
        // The boss's ASCII art. Routed through state (not read in the screen) so
        // that when authored bosses gain their own art field post-MVP, only this
        // assignment changes — boss.art instead of the placeholder constant.
        val bossArt: String = "",
        val finalDifficulty: Int = 0,
        val playerScore: Int = 0,
        // Null until the player faces the Trial; then carries the outcome.
        val result: TrialResult? = null,
        val isLoading: Boolean = true,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    // The Trial is a single moment, not a reactive stream: a snapshot of the week
    // as the player enters, the same numbers the resolution will judge. Loaded
    // once. (The boss is computed here rather than read from storage because MVP
    // seeds no bosses — see placeholderBoss.)
    init {
        viewModelScope.launch {
            val week = weekRepository.current()
            if (week == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            val boss = placeholderBoss(week)
            val difficulty = calculateBossDifficulty(boss)
            val statSum = statRepository.all().sumOf { it.value }
            val quotasMet = checkWeeklyQuotas(week).progress.count { it.met }
            _state.update {
                it.copy(
                    boss = boss,
                    bossArt = WARDEN_ART,
                    finalDifficulty = difficulty,
                    playerScore = playerScore(statSum, quotasMet),
                    isLoading = false,
                )
            }
        }
    }

    fun onFaceTrial() {
        val boss = _state.value.boss ?: return
        if (_state.value.result != null) return // resolved once; ignore repeats
        viewModelScope.launch {
            val result = resolveWeeklyBoss(boss)
            _state.update { it.copy(result = result) }
        }
    }
}
