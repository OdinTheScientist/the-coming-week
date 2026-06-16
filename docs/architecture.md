# The Coming Week — Architecture

The target architecture for the Android MVP. All new code must fit this
structure. Deviations require updating this document first.

## Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture pattern:** MVVM with a use-case layer
- **Persistence:** Room
- **DI:** Hilt
- **Navigation:** Navigation Compose
- **Min SDK:** 26
- **Build:** Gradle Kotlin DSL with version catalog (`libs.versions.toml`)

## Layering rules

Three layers. The general dependency flow is:

```
ui  →  domain  →  data
```

The `domain` layer has two parts with different rules:

- **`ui`** may depend on `domain`. Must not depend on `data` (no DAO or
  entity access from UI; go through a use case or repository).
- **`domain/model/`** depends on **nothing in the project**. Pure Kotlin.
  No `androidx.*`, no `android.*`, no Room, no Compose. These are the
  types every other layer speaks.
- **`domain/usecase/`** may depend on `domain/model/` and on data-layer
  repositories (`data/repository/`). Repository interfaces are deferred
  (see "DI" below), so use cases inject the **concrete** repository
  classes directly. Still pure Kotlin otherwise: no `androidx.*`,
  no `android.*`, no Room types, no Compose. A use case must speak domain
  models, never Room entities.
- **`data`** may depend on `domain` (repositories map entities → domain
  models; the seed builds entities from domain enums). Must not depend
  on `ui`.

So the precise dependency picture is: `ui → domain/usecase → data/repository`,
with `domain/model` sitting underneath as a shared, dependency-free core
that all three layers reference.

Violations to flag in review:
- UI code touching DAOs or entities directly (must go through a use case
  or repository).
- `domain/model/` importing anything from `data`, `ui`, Android, or Room.
- A use case importing Room entities or DAOs (it may import repositories,
  but must consume and return domain models only).
- Repositories containing business logic (belongs in use cases).
- Use cases returning Room entities instead of domain models.

## Package structure

```
com.thecomingweek/
├── TheComingWeekApp.kt              // @HiltAndroidApp
├── MainActivity.kt
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt
│   │   ├── entity/
│   │   │   ├── QuestEntity.kt
│   │   │   ├── StatEntity.kt
│   │   │   ├── WeekEntity.kt
│   │   │   ├── BossEntity.kt
│   │   │   ├── BiomeEntity.kt
│   │   │   ├── PlayerStateEntity.kt
│   │   │   └── BuffEntity.kt
│   │   └── dao/
│   │       ├── QuestDao.kt
│   │       ├── StatDao.kt
│   │       ├── WeekDao.kt
│   │       ├── BossDao.kt
│   │       ├── BiomeDao.kt
│   │       ├── PlayerStateDao.kt
│   │       └── BuffDao.kt
│   ├── mapper/                      // entity ↔ domain mappers
│   └── repository/
│       ├── QuestRepository.kt
│       ├── StatRepository.kt
│       ├── WeekRepository.kt
│       ├── BossRepository.kt
│       ├── BiomeRepository.kt
│       ├── PlayerStateRepository.kt
│       └── BuffRepository.kt
│
├── domain/
│   ├── model/                       // pure Kotlin data classes
│   └── usecase/
│       ├── DrawDailyQuestsUseCase.kt
│       ├── CompleteQuestUseCase.kt
│       ├── ResolveDailyBattleUseCase.kt
│       ├── CalculateBossDifficultyUseCase.kt
│       ├── ResolveWeeklyBossUseCase.kt
│       ├── CheckWeeklyQuotasUseCase.kt
│       ├── AdvanceWeekUseCase.kt
│       └── ResetRunUseCase.kt
│
├── ui/
│   ├── navigation/
│   │   ├── BottomBar.kt
│   │   ├── NavGraph.kt
│   │   └── Routes.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── screen/
│       ├── home/                    // HomeScreen + HomeViewModel
│       ├── quest/                   // QuestDetailScreen + VM
│       ├── hero/                    // HeroScreen + HeroViewModel
│       ├── statquests/              // StatQuestsScreen + StatQuestsViewModel
│       ├── week/                    // WeekScreen + VM
│       ├── boss/                    // BossScreen + VM
│       ├── battle/                  // BattleScreen + BattleViewModel
│       └── biome/                   // BiomeScreen + VM
│
└── di/
    ├── DatabaseModule.kt
    └── RepositoryModule.kt
```

## Domain models

Pure Kotlin. These are the types use cases and ViewModels speak.

```kotlin
enum class StatType { STRENGTH, AGILITY, VITALITY, INTELLECT, CREATIVITY, WILLPOWER }

data class Stat(
    val type: StatType,
    val value: Int,        // permanent
    val weeklyGain: Int    // resets each week
)

enum class QuestType { DAILY, SIDE, WEEKLY }
enum class QuestStatus { AVAILABLE, COMPLETED, MISSED }

data class Quest(
    val id: String,
    val title: String,
    val flavor: String,
    val stat: StatType,
    val type: QuestType,
    val xpReward: Int,
    val statGain: Int,
    val weight: Int,           // pool weighting
    val status: QuestStatus,
    val dayAssigned: Long?     // epoch day; null for side/weekly
)

data class Week(
    val id: Long,
    val weekNumber: Int,       // within current biome
    val statTheme: StatType,
    val biomeId: Long,
    val startEpochDay: Long,
    val endEpochDay: Long,
    val quotas: Map<StatType, Int>,
    val isResolved: Boolean
)

data class Boss(
    val id: Long,
    val weekId: Long,
    val biomeId: Long,
    val name: String,
    val flavor: String,
    val baseDifficulty: Int,
    val finalDifficulty: Int,  // computed at Sunday
    val defeated: Boolean?
)

data class Biome(
    val id: Long,
    val name: String,
    val flavor: String,
    val weekCount: Int,        // 6–8
    val startEpochDay: Long,
    val finalBossId: Long?
)

data class PlayerState(
    val runNumber: Int,
    val level: Int,
    val xp: Int,
    val currentBiomeId: Long?,
    val currentWeekId: Long?,
    val stats: List<Stat>
)

enum class BuffPolarity { BUFF, DEBUFF }

enum class BuffSource {
    QUEST_COMPLETED, QUEST_MISSED,
    QUOTA_MET, QUOTA_MISSED,
    BOSS_WON, BOSS_LOST
}

data class Buff(
    val id: Long,
    val name: String,
    val polarity: BuffPolarity,
    val statAffected: StatType?,
    val modifier: Int,
    val expiresEpochDay: Long,
    val source: BuffSource
)
```

## Room entities

Mirror the domain models with `@Entity` annotations. Use enums directly
via `@TypeConverters`. Serialize `Map<StatType, Int>` as a JSON string
in `WeekEntity.quotasJson`. `PlayerStateEntity` is a singleton row with
fixed `id = 1`.

Each DAO follows a consistent shape:
- One `observe*` returning `Flow<List<Entity>>`
- One or two suspend getters for ad-hoc reads
- One upsert
- Scoped update queries where appropriate
- Scoped delete queries for cleanup (e.g., expiring buffs)

### Quest pool vs. drawn instances

The `quests` table holds two kinds of rows that share one schema:

- **Pool templates** — the hardcoded starter quests. `dayAssigned = null`
  and a bare slug ID (`"str_01"`). These are never mutated; they are the
  source the daily draw selects from.
- **Drawn instances** — copies created by `DrawDailyQuestsUseCase` for a
  specific day. `dayAssigned` is set, and the ID is suffixed with the
  epoch day: **`"{poolId}_{epochDay}"`** (e.g., `"str_01_20601"`). The
  suffix keeps the template intact for reuse and makes each day's draw
  uniquely keyed.

`QuestRepository.pool()` returns *all* rows; callers must filter
(`type == DAILY && dayAssigned == null`) to get templates only. Any code
that traces a drawn quest back to its template must respect the
`{poolId}_{epochDay}` ID convention.

## Repository layer

Thin pass-throughs that:
- Map entity ↔ domain via `data/mapper/`
- Expose Flows of domain models, not entities
- Contain no business logic

## Use-case layer

Each use case is a single class with one `operator fun invoke(...)`.
This is where game logic lives:
- Draw quests with weighted random
- Apply stat gains / XP / buffs on quest completion
- Calculate boss difficulty from stats + quotas + biome modifier
- Resolve daily battles and weekly bosses
- Check weekly quotas
- Advance the week
- Reset the run on biome completion

Use cases are injected into ViewModels via Hilt.

> **Temporary trigger (MVP):** `AdvanceWeekUseCase` is complete and correct,
> but in the fiction the week turns only when Sunday's Trial passes — there is
> no in-fiction reason for the player to advance it manually. Until the Trial
> wires advancement (Stage 10), it is fired by a hidden long-press on the Week
> screen's title bar, with a plain-text debug confirmation. The mechanism is
> final; only the trigger evolves.

## ViewModels

One per screen. State exposed as a single immutable `UiState` data class
via `StateFlow`. Events handled via public methods (`onQuestCompleted`,
`onBossEnter`, etc.). No LiveData, no two-way binding.

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(...) : ViewModel() {
    data class UiState(
        val today: List<Quest> = emptyList(),
        val activeBuffs: List<Buff> = emptyList(),
        val daysUntilTrial: Int = 0,
        val isLoading: Boolean = true
    )
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()
}
```

## Navigation

Routes defined as a sealed class in `ui/navigation/Routes.kt`. Single
`NavHost` in `NavGraph.kt`. Bottom bar surfaces Home / Week / Hero /
Biome. Boss is reached from the Week screen on Sunday. QuestDetail is
reached from Home. StatQuests is reached from the Hero screen.

```kotlin
sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Home   : Route("home")
    data object Hero   : Route("hero")
    data object Week   : Route("week")
    data object Boss   : Route("boss")
    data object Battle : Route("battle") {
        const val PATTERN = "battle?force={force}"
        fun path(force: Boolean) = "battle?force=$force"
    }
    data object Biome  : Route("biome")
    data class QuestDetail(val id: String) : Route("quest/$id") {
        companion object { const val PATTERN = "quest/{id}" }
    }
    data class StatQuests(val stat: StatType) : Route("statquests/${stat.name}") {
        companion object { const val PATTERN = "statquests/{stat}" }
    }
}
```

## DI

- `DatabaseModule` provides `AppDatabase` and each DAO.
- Repositories are constructor-injected; no module needed until they
  are extracted to interfaces (deferred until testing demands it).

## MVP constraints

These are hard limits for the MVP. Anything below this line is
**out of scope** until explicitly promoted:

- No skill trees
- No complex branching systems
- Simple auto-battler (deterministic resolution, not turn-based)
- Lightweight quest system (no chains, no prerequisites)
- Lightweight boss logic (single difficulty number, win/loss)
- Minimal UI complexity (no animations beyond Compose defaults)
- No cloud sync, no accounts, no notifications (yet)
- No analytics
- Quest pool is hardcoded; no in-app quest authoring

## Tone constraints (applies to all user-facing strings)

Defined in full in `CLAUDE.md`. Summary for quick reference:
quiet, ominous, ritualistic, monk-like, gothic spiritual. The week
always comes. Avoid generic UX copy, exclamation marks, and cheerful
encouragement.


## Biome cadence

- Biome length: 6 weeks (~42 days), fixed for MVP
- Rationale: past the habit-formation midpoint for medium-complexity
  behaviors; long enough for a meaningful arc, short enough to stay
  fresh; yields ~8 biomes/year, balancing content burden against
  repetition
- Per biome: 1 common enemy per stat (6 sprites) + 1 final biome boss
  (1 sprite) = 7 sprite assets minimum
- Weekly bosses reuse the per-stat enemy at scaled difficulty, not
  unique sprites per week
- Content roadmap target: 6 biomes total → ~36 weeks of unique
  experience before any biome reappears