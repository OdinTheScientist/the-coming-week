# The Coming Week — Build Roadmap

**Current stage:** Stage 10 — NOT STARTED

The MVP is being built in numbered stages. Each stage is a single
focused commit (or small handful), independently reviewable, and ends
with the project still compiling and the app still launching.

Between every stage, run the `/review-stage` skill. Do not advance
until the review returns no blockers.

---

## Stages

- [x] **Stage 0 — Project initialized**
  Empty Activity template, git repo, first commit pushed.

- [x] **Stage 1 — Dependencies & Hilt application**
  Version catalog updated. Room, Hilt, Navigation Compose, Lifecycle
  added. `TheComingWeekApp` created and registered in manifest.
  Project builds, app launches.

- [x] **Stage 2 — Package skeleton & domain models**
  Full package structure created. Domain enums and data classes added
  (Stat, Quest, Week, Boss, Biome, PlayerState, Buff). No logic yet.

- [x] **Stage 3 — Room layer**
  Entities, DAOs, `AppDatabase`, `Converters`. Room compiles cleanly.

- [x] **Stage 4 — DI modules & repository layer**
  `DatabaseModule`, repositories with entity↔domain mappers. Hilt
  graph resolves at runtime.

- [x] **Stage 5 — Theme**
  Ritual palette, typography, Material 3 theme. Dark by default.

- [x] **Stage 6 — Navigation & placeholder screens**
  `NavGraph`, `Routes`, six placeholder Composables. Bottom bar wired.
  All screens reachable.

- [x] **Stage 7 — First use case + seed data**
  `DrawDailyQuestsUseCase`. Small hardcoded starter quest pool seeded
  on first launch.

- [x] **Stage 8a — Home wiring & state**
  HomeViewModel, UiState, reactive quest flow, remove DB-open trigger.
  Gate: 3 quests flow into state on first launch, no seed/read race.

- [x] **Stage 8b — Home UI & completion**
  Quest-card UI, CompleteQuestUseCase, tap-to-complete, stat increment,
  buff grant. Gate: draw → see → complete → stat ticks up, on device.

- [x] **Stage 9 — Weekly loop**
  Quotas, stat theme, week advancement. Sunday recognized.

- [x] **Stage 10 — Boss & biome scaffolding**
  Boss difficulty calculation, weekly boss resolution, biome
  progression and reset on biome end.

- [x] **Stage 11 — Daily Battle**
  Daily enemy encounter, turn-based auto-resolution, HP persistence,
  battle screen, manual trigger + 10pm auto-trigger, debug long-press bypass.

  **HP & Damage Formula**
  - Player max HP = `10 + (level * 5)`. Seed: level 1 = 15 HP.
  - Enemy HP = `5 + (weekNumber * 2)`. Enemy attack = `2 + weekNumber`.
  - Player base attack = `3 + (statThemeValue / 2)`.
  - Buff (all quests done): player +2 attack, enemy -1 attack.
  - Neutral (some done): no modifier.
  - Debuff (none done): player -1 attack, enemy +1 attack.
  - Boss HP = `20 + (weekNumber * 5)`. Boss attack = `4 + (weekNumber * 2)`.

  **New domain models:** `BattleResult`, `BattleRound`, `BattleType`, `BattleOutcome`.
  **New entity/DAO:** `BattleResultEntity`, `BattleResultDao`.
  **New use case:** `ResolveDailyBattleUseCase`.
  **PlayerState additions:** `currentHp`, `maxHp`.

  **Trigger logic**
  - Manual button on Home: active when quests drawn + today's battle unresolved.
  - Long-press same button: debug bypass of resolved gate.
  - Auto-trigger: on app open, if time ≥ 22:00 and today's battle unresolved,
    navigate directly to BattleScreen before Home renders.

  **Battle screen** (`ui/screen/battle/`)
  - Hero sprite left, enemy sprite right, HP bars for both.
  - Scrollable round log: `"You strike for 4."` / `"The Hollow Grunt claws for 3."`
  - Resolution banner: `"The enemy falls."` (victory) or
    `"You are broken. The week is not over."` (wounded).
  - Continue returns to Home.

  **Enemy roster (MVP, stat-keyed, reused across biomes)**
  - Strength: The Hollow Grunt
  - Agility: The Flinching Shade
  - Vitality: The Rot-Bloated
  - Intellect: The Pale Scribe
  - Creativity: The Withered Muse
  - Willpower: The Kneeling Penitent

  **Wounded state:** HP ≤ 0 mid-week → set to 1 for boss fight, -2 player
  attack for boss duration.
  **Week reset:** `AdvanceWeekUseCase` resets `currentHp = maxHp`.

  Gate: all 8 acceptance criteria must pass before advancing.
  1. Battle resolves correctly from all three buff states.
  2. HP persists after battle, visible on Home.
  3. Today's battle cannot be resolved twice (normal path).
  4. Long-press bypasses resolved gate.
  5. Auto-trigger fires on app open after 10pm if unresolved.
  6. BattleScreen shows round log and resolution banner.
  7. Week advance resets HP.
  8. Wounded state applies -2 attack debuff to boss fight.

  - [ ] **Stage 12 — Quest Reroll**
    Weekly reroll resource (3 per week) allowing the player to replace one
    available daily quest with a new weighted draw.

    **Data changes:** `rerollsRemaining: Int` added to Week domain model,
    entity, mapper, DAO. New weeks and run resets seed with 3.

    **New use case:** `RerollQuestUseCase` — guards on count > 0 and quest
    AVAILABLE, deletes drawn instance, draws replacement, decrements week.

    **Home screen:** "The Fates (N)" button below quest list. Tapping enters
    Reroll Mode — available cards highlight (Blood border), completed/missed
    dim. Tapping available card triggers reroll, exits mode. Cancel text exits
    with no change. Button grays out at 0 or no available quests.

    **UiState additions:** `rerollsRemaining: Int`, `isRerollMode: Boolean`.

    Gate: all 9 acceptance criteria must pass before advancing.
    1. Rerolls start at 3 on a fresh week.
    2. Entering Reroll Mode highlights available, dims completed/missed.
    3. Tapping available card replaces quest, exits mode, decrements count.
    4. Count persists across app restarts.
    5. Button grays out at 0 rerolls or no available quests.
    6. Cancel exits Reroll Mode with no state change.
    7. Rerolled quest respects weekly stat theme weighting.
    8. Cannot reroll completed or missed quest.
    9. New week resets rerolls to 3.
---

## Deferred / post-MVP

Known, bounded limitations with known fixes. Tracked here so they are
not re-diagnosed as bugs later.

- **Sunday-aligned week advancement.** Advancement is currently triggered
  manually (a hidden dev long-press on the Week screen title; see
  `AdvanceWeekUseCase`). Automatic, Trial-driven advancement on Sunday is
  deferred.

- **Early-advance quota-credit lag.** Quota credit (NOT stats, XP, or buffs)
  lags for quests completed between an early manual advance and the new week's
  start. The completion correctly credits Week N's range, but the Week screen
  only displays Week N+1's quotas after advance. Stats, XP, and buffs are
  unaffected. Resolves automatically when the calendar reaches the new week's
  `startEpochDay`, or with Sunday-aligned advancement (deferred to post-MVP).
  No data loss; bounded display lag only.

- **Trial balance.** Player score uses the sum of persistent stats; difficulty
  uses sum/6 (statAverage). After a few runs, stat accumulation tilts the player
  score much faster than difficulty rises, so the Trial becomes trivially
  winnable for long-term players. A balance pass is needed before public
  release; for personal-MVP use, this only manifests after several biomes.

- **Room migrations.** The database currently uses
  `fallbackToDestructiveMigration` — schema changes wipe the DB. Acceptable for
  the MVP (no real user data), but proper migrations are needed before any
  shipped release with real user data.

---

## Stage completion protocol

When a stage is complete:

1. Run `/review-stage`.
2. Address any BLOCKERS.
3. FLAGS may be deferred but should be tracked.
4. Commit with message: `Stage N complete: <short summary>`.
5. Update this file: check the box, change "Current stage" to N+1.
6. Push.

The `review-stage` skill uses the most recent `Stage N complete` commit
as the diff baseline.