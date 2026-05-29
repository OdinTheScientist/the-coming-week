package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.BiomeRepository
import com.thecomingweek.data.repository.BossRepository
import com.thecomingweek.data.repository.BuffRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Boss
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.TrialResult
import com.thecomingweek.domain.usecase.internal.playerScore
import javax.inject.Inject

// The Trial, resolved. This is now the canonical thing that turns the week:
// facing the boss IS week advancement (replacing Stage 9's debug long-press as
// the real trigger). At a biome's final week it resets the run instead.
//
// Composition:
//   ResolveWeeklyBossUseCase
//     ├─ CalculateBossDifficultyUseCase   → the Trial's weight
//     ├─ (decides defeated)
//     ├─ AdvanceWeekUseCase               → normal weeks: turn to the next
//     └─ ResetRunUseCase                  → final week of biome: begin a new descent
class ResolveWeeklyBossUseCase @Inject constructor(
    private val statRepository: StatRepository,
    private val weekRepository: WeekRepository,
    private val biomeRepository: BiomeRepository,
    private val bossRepository: BossRepository,
    private val buffRepository: BuffRepository,
    private val checkWeeklyQuotas: CheckWeeklyQuotasUseCase,
    private val calculateBossDifficulty: CalculateBossDifficultyUseCase,
    private val advanceWeek: AdvanceWeekUseCase,
    private val resetRun: ResetRunUseCase,
) {

    suspend operator fun invoke(boss: Boss): TrialResult {
        val finalDifficulty = calculateBossDifficulty(boss)

        val week = weekRepository.current()
        val statSum = statRepository.all().sumOf { it.value }
        val quotasMet = week?.let { checkWeeklyQuotas(it).progress.count { q -> q.met } } ?: 0
        val score = playerScore(statSum, quotasMet)

        val defeated = score >= finalDifficulty

        // Persist the outcome. The placeholder Warden has no prior row, so this
        // is its first and only write for the week (id == weekId, stable).
        bossRepository.upsert(
            boss.copy(finalDifficulty = finalDifficulty, defeated = defeated),
        )

        // A loss marks next week. Buff lifecycle is known-broken (see
        // BuffRepository) — we grant anyway and let the documented breakage
        // stand; the debuff will not actually surface as active yet.
        val debuff = if (!defeated && week != null) {
            buffRepository.grant(BuffSource.BOSS_LOST, week.statTheme, week.endEpochDay + 7)
        } else {
            null
        }

        // The week turns because the Trial was faced. At the biome's last week
        // the descent ends and the run resets instead of advancing.
        val biome = biomeRepository.current()
        val atBiomeEnd = week != null && biome != null && week.weekNumber >= biome.weekCount
        if (atBiomeEnd) {
            resetRun()
        } else {
            advanceWeek()
        }

        return TrialResult(
            defeated = defeated,
            finalDifficulty = finalDifficulty,
            playerScore = score,
            debuffGranted = debuff,
        )
    }
}
