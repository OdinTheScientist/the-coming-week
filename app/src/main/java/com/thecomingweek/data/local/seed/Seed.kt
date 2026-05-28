package com.thecomingweek.data.local.seed

import com.thecomingweek.data.local.AppDatabase
import com.thecomingweek.data.local.entity.BiomeEntity
import com.thecomingweek.data.local.entity.PlayerStateEntity
import com.thecomingweek.data.local.entity.QuestEntity
import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.data.local.entity.WeekEntity
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatType
import java.time.LocalDate

object Seed {

    suspend fun populate(db: AppDatabase) {
        val today = LocalDate.now().toEpochDay()

        db.biomeDao().upsert(biome(today))
        db.weekDao().upsert(week(today))
        db.playerStateDao().upsert(playerState())
        StatType.entries.forEach { db.statDao().upsert(stat(it)) }
        quests().forEach { db.questDao().upsert(it) }
    }

    private fun biome(startEpochDay: Long) = BiomeEntity(
        id = 1L,
        name = "The Stone Hours",
        flavor = "A monastery of unspoken vows.",
        weekCount = 6,
        startEpochDay = startEpochDay,
        finalBossId = null
    )

    private fun week(startEpochDay: Long) = WeekEntity(
        id = 1L,
        weekNumber = 1,
        statTheme = StatType.STRENGTH,
        biomeId = 1L,
        startEpochDay = startEpochDay,
        endEpochDay = startEpochDay + 6,
        quotasJson = """{"STRENGTH":3,"AGILITY":1,"VITALITY":2,"INTELLECT":1,"CREATIVITY":1,"WILLPOWER":1}""",
        isResolved = false
    )

    private fun playerState() = PlayerStateEntity(
        runNumber = 1,
        level = 1,
        xp = 0,
        currentBiomeId = 1L,
        currentWeekId = 1L
    )

    private fun stat(type: StatType) = StatEntity(
        type = type,
        value = 0,
        weeklyGain = 0
    )

    private fun quests() = listOf(
        quest("str_01", "Lift twice your doubt.", "The iron speaks. Answer it.", StatType.STRENGTH),
        quest("str_02", "Push past the second wind.", "Your body is the offering. Bring it.", StatType.STRENGTH),
        quest("agi_01", "Move before the day moves you.", "Stillness is a slow death. Step.", StatType.AGILITY),
        quest("agi_02", "Walk one mile in silence.", "The ground remembers those who walk it.", StatType.AGILITY),
        quest("vit_01", "Eat as if the body is sacred.", "Not fuel. Sacrament.", StatType.VITALITY),
        quest("vit_02", "Sleep before the hour turns.", "Surrender to the dark on time.", StatType.VITALITY),
        quest("int_01", "Read what unsettles you.", "Comfort is the mind's grave.", StatType.INTELLECT),
        quest("int_02", "Solve one problem fully.", "Do not leave it half-known.", StatType.INTELLECT),
        quest("cre_01", "Make one thing no one asked for.", "The uncalled work is the truest.", StatType.CREATIVITY),
        quest("cre_02", "Sketch the unspeakable.", "Give form to what hides.", StatType.CREATIVITY),
        quest("wil_01", "Sit with the urge unsated.", "The craving is the test, not the enemy.", StatType.WILLPOWER),
        quest("wil_02", "Refuse the easy answer.", "Discipline is not punishment. It is devotion.", StatType.WILLPOWER)
    )

    private fun quest(id: String, title: String, flavor: String, stat: StatType) = QuestEntity(
        id = id,
        title = title,
        flavor = flavor,
        stat = stat,
        type = QuestType.DAILY,
        xpReward = 10,
        statGain = 1,
        weight = 1,
        status = QuestStatus.AVAILABLE,
        dayAssigned = null
    )
}
