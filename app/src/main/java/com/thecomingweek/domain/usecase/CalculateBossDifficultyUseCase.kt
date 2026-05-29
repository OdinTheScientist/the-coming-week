package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.BiomeRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Boss
import javax.inject.Inject

// The Trial's weight, assembled at Sunday from how the week was lived:
//
//   finalDifficulty = baseDifficulty + statAverage + quotaPenalty + biomeModifier
//
//   baseDifficulty — the boss's own floor (placeholder Warden: 10).
//   statAverage    — sum of the six stat values / 6. Scales the Trial with the
//                    player's permanent growth so it keeps pace across runs
//                    (stats persist; the boss must too, or late runs trivialise).
//   quotaPenalty   — one point per UNMET weekly quota. The punishment for
//                    slacking: every demand left unanswered makes the Trial
//                    heavier. This is WeekRepository.unmetQuotas, finally read.
//   biomeModifier  — weekNumber * 2. Deeper into the biome, harder the Trial.
class CalculateBossDifficultyUseCase @Inject constructor(
    private val statRepository: StatRepository,
    private val weekRepository: WeekRepository,
    private val biomeRepository: BiomeRepository,
) {

    suspend operator fun invoke(boss: Boss): Int {
        val stats = statRepository.all()
        val statAverage = if (stats.isEmpty()) 0 else stats.sumOf { it.value } / stats.size
        val quotaPenalty = weekRepository.unmetQuotas()
        // The week supplies the biome-depth term. Difficulty is computed before
        // the Trial advances the week, so current() is the week being judged.
        val weekNumber = weekRepository.current()?.weekNumber ?: 1
        val biomeModifier = biomeRepository.modifierFor(boss.biomeId, weekNumber)

        return boss.baseDifficulty + statAverage + quotaPenalty + biomeModifier
    }
}
