package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.BiomeRepository
import com.thecomingweek.data.repository.BossRepository
import com.thecomingweek.data.repository.BuffRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Boss
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.TrialResult
import com.thecomingweek.domain.usecase.internal.playerScore
import java.time.LocalDate
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
    private val playerStateRepository: PlayerStateRepository,
    private val checkWeeklyQuotas: CheckWeeklyQuotasUseCase,
    private val calculateBossDifficulty: CalculateBossDifficultyUseCase,
    private val advanceWeek: AdvanceWeekUseCase,
    private val resetRun: ResetRunUseCase,
) {

    suspend operator fun invoke(boss: Boss): TrialResult {
        val epochDay = LocalDate.now().toEpochDay()
        val finalDifficulty = calculateBossDifficulty(boss)

        val week = weekRepository.current()
        val statSum = statRepository.all().sumOf { it.value }
        val quotaReport = week?.let { checkWeeklyQuotas(it) }
        val quotasMet = quotaReport?.progress?.count { it.met } ?: 0

        // A body broken by the week's battles still stands for the Trial — but
        // diminished. Set to 1 HP (not 0: the Trial is not a battle the player
        // can simply be absent from) and the weakness is felt as a penalty to
        // the score they bring.
        val wounded = (playerStateRepository.get()?.currentHp ?: 1) <= 0
        if (wounded) {
            playerStateRepository.setCurrentHp(1)
        }

        // Active buffs accumulated over the week modify the player's Trial score.
        val activeBuffs = buffRepository.pruneAndGetActive(epochDay)
        val buffSum = activeBuffs.sumOf { it.modifier }

        // The Trial has no attack stat to dock; the roadmap's "-2 attack while
        // wounded" is deliberately applied here as a flat -2 to score instead.
        val score = playerScore(statSum, quotasMet) - (if (wounded) 2 else 0) + buffSum

        val defeated = score >= finalDifficulty

        // Persist the outcome. The placeholder Warden has no prior row, so this
        // is its first and only write for the week (id == weekId, stable).
        bossRepository.upsert(
            boss.copy(finalDifficulty = finalDifficulty, defeated = defeated),
        )

        // Grant quota result buffs — they carry into next week's battles.
        quotaReport?.progress?.forEach { q ->
            val source = if (q.met) BuffSource.QUOTA_MET else BuffSource.QUOTA_MISSED
            buffRepository.grant(source, q.stat, epochDay)
        }

        // Grant boss result buff and capture the debuff for TrialResult display.
        val debuff = if (!defeated && week != null) {
            buffRepository.grant(BuffSource.BOSS_LOST, week.statTheme, epochDay)
        } else {
            if (week != null) buffRepository.grant(BuffSource.BOSS_WON, week.statTheme, epochDay)
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
