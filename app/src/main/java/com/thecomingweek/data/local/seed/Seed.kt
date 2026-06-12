package com.thecomingweek.data.local.seed

import com.thecomingweek.data.local.AppDatabase
import com.thecomingweek.data.local.entity.BiomeEntity
import com.thecomingweek.data.local.entity.PlayerStateEntity
import com.thecomingweek.data.local.entity.QuestEntity
import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.data.mapper.toEntity
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.model.Week
import com.thecomingweek.domain.usecase.internal.quotasForTheme
import java.time.LocalDate

object Seed {

    suspend fun populate(db: AppDatabase) {
        val today = LocalDate.now().toEpochDay()

        db.biomeDao().upsert(biome(today))
        db.weekDao().upsert(week(today))
        db.playerStateDao().upsert(playerState())
        StatType.entries.forEach { db.statDao().upsert(stat(it)) }
        // Load-bearing order: quests MUST be inserted last. HomeViewModel's draw
        // trigger fires on the quest-table Flow re-emission, and only draws once
        // the week exists — so the week must be committed before these inserts.
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

    // Built as a domain Week and mapped to its entity so quotas are derived from
    // the theme by the one shared rule (quotasForTheme) and serialised by the one
    // shared mapper — no hand-written quota JSON to drift from advancement.
    private fun week(startEpochDay: Long) = Week(
        id = 1L,
        weekNumber = 1,
        statTheme = StatType.STRENGTH,
        biomeId = 1L,
        startEpochDay = startEpochDay,
        endEpochDay = startEpochDay + 6,
        quotas = quotasForTheme(StatType.STRENGTH),
        isResolved = false,
        rerollsRemaining = 3,
    ).toEntity()

    private fun playerState() = PlayerStateEntity(
        runNumber = 1,
        level = 1,
        xp = 0,
        currentBiomeId = 1L,
        currentWeekId = 1L,
        currentHp = 15,
        maxHp = 15
    )

    private fun stat(type: StatType) = StatEntity(
        type = type,
        value = 0,
        weeklyGain = 0
    )

    private fun quests() = listOf(

        // STRENGTH
        quest("str_01", "The Iron Rite",
            "Complete a full calisthenics workout session.",
            "The body is the first fortress. Strengthen its walls.",
            StatType.STRENGTH, 3),
        quest("str_02", "Burden Of Bone",
            "Perform fifty push-ups before the sun reaches its zenith.",
            "Pressure forces carbon into diamond, and flesh into steel.",
            StatType.STRENGTH, 3),
        quest("str_03", "Grappler's Oath",
            "Spend one hour in dedicated combat arts practice.",
            "True mastery is found in the pressure of another's grip.",
            StatType.STRENGTH, 2),
        quest("str_04", "Vow Of Heaviness",
            "Execute a high-intensity interval training session.",
            "Willingness is the difference between the soldier and the slave.",
            StatType.STRENGTH, 2),
        quest("str_05", "Skeletal Alignment",
            "Perform a comprehensive weighted lift session.",
            "Gravity is the constant enemy that must be appeased.",
            StatType.STRENGTH, 2),
        quest("str_06", "The Crushing Grip",
            "Hold a plank for the duration of five slow breaths.",
            "Stability is the precursor to all effective violence.",
            StatType.STRENGTH, 3),
        quest("str_07", "Kinetic Consecration",
            "Perform one hundred bodyweight squats.",
            "The earth yields only to those who possess the power to move it.",
            StatType.STRENGTH, 1),
        quest("str_08", "Marrow Forge",
            "Complete five sets of any compound lift to failure.",
            "Only through controlled destruction can the frame be rebuilt anew.",
            StatType.STRENGTH, 1),

        // AGILITY
        quest("agi_01", "Shadow Weave",
            "Complete a full session of restorative yoga.",
            "A rigid limb is a broken limb in the dance of survival.",
            StatType.AGILITY, 3),
        quest("agi_02", "Serpentine Flow",
            "Engage in twenty minutes of deliberate, fluid mobility work.",
            "To move like water is to evade the blade of fate.",
            StatType.AGILITY, 3),
        quest("agi_03", "Path Of Silence",
            "Walk for three miles through an area of natural solitude.",
            "A quiet tread leaves no trail for the predator to follow.",
            StatType.AGILITY, 3),
        quest("agi_04", "Swift Vow",
            "Practice coordination drills for thirty minutes.",
            "The mind commands, but the nerves must translate the intent into motion.",
            StatType.AGILITY, 2),
        quest("agi_05", "Tethered Balance",
            "Hold a single-leg balance pose for five minutes per side.",
            "Balance is the thin line between standing and falling into the abyss.",
            StatType.AGILITY, 2),
        quest("agi_06", "Lithe Offering",
            "Perform a deep-tissue stretch routine.",
            "Release the knots of the day before they anchor your spirit.",
            StatType.AGILITY, 2),
        quest("agi_07", "Phantom Reflex",
            "Engage in reactive agility training exercises.",
            "The body that hesitates is the body that breaks.",
            StatType.AGILITY, 1),
        quest("agi_08", "Fluid Sacrament",
            "Complete a full mobility cycle before the sun rises.",
            "Adaptation is the only law that governs the wild.",
            StatType.AGILITY, 1),
        quest("agi_09", "Grappler's Study",
            "Study jiu jitsu for thirty minutes — drill concepts, watch film, or review technique.",
            "The ground is patient. It waits for those who have not prepared.",
            StatType.AGILITY, 2),

        // VITALITY
        quest("vit_01", "Fasting Vigil",
            "Abstain from all food for sixteen hours.",
            "Hunger clarifies the vision when the veil is thin.",
            StatType.VITALITY, 3),
        quest("vit_02", "Icy Baptism",
            "End your morning washing with three minutes of cold water.",
            "The frost shocks the soul back into the vessel.",
            StatType.VITALITY, 3),
        quest("vit_03", "Chalice Of Life",
            "Consume two liters of pure, untainted water.",
            "The body is a dried well that must be filled to sustain the flame.",
            StatType.VITALITY, 3),
        quest("vit_04", "Restful Shroud",
            "Sleep for eight hours in a completely dark environment.",
            "The dream world is a necessary descent into the dark to reclaim the light.",
            StatType.VITALITY, 2),
        quest("vit_05", "Sustenance Ritual",
            "Prepare a meal consisting only of whole, raw elements.",
            "Fuel the machine with what the earth provides, not what the industry processes.",
            StatType.VITALITY, 2),
        quest("vit_06", "Breath Of Ancients",
            "Practice deep, diaphragmatic breathing for ten minutes.",
            "Oxygen is the unseen nectar that sustains the internal spirit.",
            StatType.VITALITY, 2),
        quest("vit_07", "Sunlight Catechism",
            "Spend twenty minutes under the morning sun without distraction.",
            "Absorb the radiance to ward off the chill of the creeping rot.",
            StatType.VITALITY, 1),
        quest("vit_08", "Purge The Toxin",
            "Completely remove processed sugar from your intake for the day.",
            "Sweetness is often a mask for decay.",
            StatType.VITALITY, 1),

        // INTELLECT
        quest("int_01", "Scripture Study",
            "Read thirty pages of a dense, instructive text.",
            "The wisdom of the dead is the only map for the living.",
            StatType.INTELLECT, 3),
        quest("int_02", "The Coder's Litany",
            "Solve one algorithmic problem to sharpen the mind.",
            "Logic is the language of the cosmos, written in cold syntax.",
            StatType.INTELLECT, 3),
        quest("int_03", "Arcane Research",
            "Watch one hour of educational content on a complex subject.",
            "To understand the world is to begin to control it.",
            StatType.INTELLECT, 2),
        quest("int_04", "Language Of Dust",
            "Practice a foreign vocabulary for twenty minutes.",
            "New tongues grant access to forgotten realms of thought.",
            StatType.INTELLECT, 2),
        quest("int_05", "Scribe's Duty",
            "Synthesize a complex topic into a single written summary.",
            "If it cannot be explained simply, it is not yet understood.",
            StatType.INTELLECT, 2),
        quest("int_06", "Silent Analysis",
            "Review your progress on a current project for thirty minutes.",
            "Examine the gears of your work to ensure they do not grind to a halt.",
            StatType.INTELLECT, 1),
        quest("int_07", "The Engineer's Codex",
            "Read one chapter or thirty minutes of a software engineering textbook, blog, or technical resource.",
            "The craft is never mastered. Only studied, applied, and studied again.",
            StatType.INTELLECT, 2),
        quest("int_08", "The Mental Forge",
            "Spend thirty minutes solving logic puzzles or strategic problems.",
            "Iron sharpens iron; mind sharpens mind.",
            StatType.INTELLECT, 1),

        // CREATIVITY
        quest("cre_01", "Ink Seance",
            "Spend thirty minutes drawing or sketching.",
            "Give form to the phantoms that dwell within the mind.",
            StatType.CREATIVITY, 3),
        quest("cre_02", "The World Maker",
            "Work on your video game project for one hour.",
            "You are the architect of a reality that exists only by your will.",
            StatType.CREATIVITY, 3),
        quest("cre_03", "Chords Of Woe",
            "Practice your musical instrument for forty minutes.",
            "Resonance can bridge the gap between this realm and the next.",
            StatType.CREATIVITY, 2),
        quest("cre_04", "Visionary Vow",
            "Brainstorm ten new concepts or ideas without editing.",
            "Creation is a chaotic birth; do not stifle the first breath.",
            StatType.CREATIVITY, 2),
        quest("cre_05", "Pixel Mortification",
            "Create one piece of digital art or pixel work.",
            "Build the world pixel by pixel, stone by stone.",
            StatType.CREATIVITY, 2),
        quest("cre_06", "The Blank Scroll",
            "Write two pages of stream-of-consciousness prose.",
            "Empty the well of your thoughts so that it may be filled again.",
            StatType.CREATIVITY, 1),
        quest("cre_07", "Aesthetic Penance",
            "Refine a previous project or piece of work.",
            "Perfection is the elusive ghost you chase in the moonlight.",
            StatType.CREATIVITY, 1),
        quest("cre_08", "Sculpting Intent",
            "Modify one aspect of your daily workspace to better serve your craft.",
            "The environment shapes the creator as much as the creator shapes the art.",
            StatType.CREATIVITY, 1),
        quest("cre_09", "The Ritual Refined",
            "Work on The Coming Week — improve, extend, or refine the app.",
            "To build the system that builds you is the deepest form of the work.",
            StatType.CREATIVITY, 2),
        quest("cre_10", "The Rollodex Rite",
            "Work on the Rollodex BJJ app for one hour.",
            "Every tool built for the craft is an extension of the practitioner.",
            StatType.CREATIVITY, 2),

        // WILLPOWER
        quest("wil_01", "Monastic Silence",
            "Meditate for twenty minutes in complete stillness.",
            "The loudest demons are silenced by the absence of sound.",
            StatType.WILLPOWER, 3),
        quest("wil_02", "Journal Of Sins",
            "Write down your failures and successes of the day.",
            "A secret kept is a burden; a truth recorded is a lesson learned.",
            StatType.WILLPOWER, 3),
        quest("wil_03", "Social Renunciation",
            "Spend the evening without any digital connection.",
            "To be alone is to face the only entity that truly matters.",
            StatType.WILLPOWER, 2),
        quest("wil_04", "The Austere Choice",
            "Refuse one comfort you usually enjoy for twenty-four hours.",
            "Desire is a chain; break one link to feel the taste of freedom.",
            StatType.WILLPOWER, 2),
        quest("wil_05", "Compassionate Rite",
            "Reach out to one person with genuine, non-trivial intent.",
            "Connection is the thread that keeps us from unraveling entirely.",
            StatType.WILLPOWER, 2),
        quest("wil_06", "The Stoic Vigil",
            "Focus on a single, difficult task for two hours straight.",
            "The mind will attempt to flee; pull it back with iron chains.",
            StatType.WILLPOWER, 1),
        quest("wil_07", "Confessional Writ",
            "Document your intentions for the coming week in detail.",
            "Write your fate, or allow it to be written for you.",
            StatType.WILLPOWER, 1),
        quest("wil_08", "The Final Hour",
            "Prepare your environment for tomorrow before you sleep.",
            "Order is the shield against the encroaching chaos of the next dawn.",
            StatType.WILLPOWER, 1)
    )

    private fun quest(
        id: String,
        title: String,
        action: String,
        flavor: String,
        stat: StatType,
        weight: Int
    ) = QuestEntity(
        id = id,
        title = title,
        action = action,
        flavor = flavor,
        stat = stat,
        type = QuestType.DAILY,
        xpReward = 10,
        statGain = 1,
        weight = weight,
        status = QuestStatus.AVAILABLE,
        dayAssigned = null
    )
}
