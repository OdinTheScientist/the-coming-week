package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.BattleRepository
import com.thecomingweek.data.repository.DayRecordRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.BattleOutcome
import com.thecomingweek.domain.model.BattleResult
import com.thecomingweek.domain.model.BattleRound
import com.thecomingweek.domain.model.BattleType
import com.thecomingweek.domain.model.DayRecord
import com.thecomingweek.domain.model.QuestSnapshot
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.usecase.internal.enemyNameForTheme
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Resolves the day's auto-battle: the week's themed enemy against the player,
// shaped by how the day's quests went. See docs/roadmap.md Stage 11 for the
// HP & damage formulas this implements.
class ResolveDailyBattleUseCase @Inject constructor(
    private val playerStateRepository: PlayerStateRepository,
    private val weekRepository: WeekRepository,
    private val questRepository: QuestRepository,
    private val battleRepository: BattleRepository,
    private val dayRecordRepository: DayRecordRepository,
) {

    suspend operator fun invoke(epochDay: Long): BattleResult? {
        val playerState = playerStateRepository.get() ?: return null
        val week = weekRepository.current() ?: return null

        val today = questRepository.observeToday(epochDay).first()
        val completed = today.count { it.status == QuestStatus.COMPLETED }

        // Buff tier from the day's quests: all done rewards the body, none done
        // costs it. Anything in between is neutral.
        val (playerBonus, enemyBonus) = when {
            today.isNotEmpty() && completed == today.size -> 2 to -1
            completed == 0 -> -1 to 1
            else -> 0 to 0
        }

        val enemyMaxHp = 5 + week.weekNumber * 2
        val enemyAttack = (2 + week.weekNumber + enemyBonus).coerceAtLeast(0)

        val statThemeValue = playerState.stats.firstOrNull { it.type == week.statTheme }?.value ?: 0
        val playerAttack = (3 + statThemeValue / 2 + playerBonus).coerceAtLeast(0)

        val rounds = mutableListOf<BattleRound>()
        var playerHp = playerState.currentHp
        var enemyHp = enemyMaxHp
        var roundNumber = 0

        // Hero strikes first each round. If the enemy falls to that strike, it
        // does not get to answer in the same round.
        while (playerHp > 0 && enemyHp > 0) {
            roundNumber++
            enemyHp = (enemyHp - playerAttack).coerceAtLeast(0)

            val enemyDamage = if (enemyHp > 0) enemyAttack else null
            if (enemyDamage != null) {
                playerHp = (playerHp - enemyDamage).coerceAtLeast(0)
            }

            rounds.add(
                BattleRound(
                    roundNumber = roundNumber,
                    playerDamage = playerAttack,
                    enemyDamage = enemyDamage,
                    playerHpAfter = playerHp,
                    enemyHpAfter = enemyHp,
                )
            )
        }

        val outcome = if (playerHp <= 0) BattleOutcome.WOUNDED else BattleOutcome.VICTORY

        val result = BattleResult(
            epochDay = epochDay,
            weekId = week.id,
            type = BattleType.DAILY,
            enemyName = enemyNameForTheme(week.statTheme),
            outcome = outcome,
            rounds = rounds,
            playerHpAfter = playerHp,
        )

        val hpBefore = playerState.currentHp
        battleRepository.upsert(result)
        playerStateRepository.setCurrentHp(playerHp)

        val quests = today.map { q ->
            QuestSnapshot(id = q.id, title = q.title, action = q.action, stat = q.stat, status = q.status)
        }
        dayRecordRepository.upsert(
            DayRecord(
                epochDay = epochDay,
                biomeId = week.biomeId,
                weekId = week.id,
                quests = quests,
                battleOutcome = outcome,
                hpBefore = hpBefore,
                hpAfter = playerHp,
                note = null,
            )
        )

        return result
    }
}
