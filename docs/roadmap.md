# The Coming Week — Build Roadmap

**Current stage:** Stage 4 — NOT STARTED

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

- [ ] **Stage 4 — DI modules & repository layer**
  `DatabaseModule`, repositories with entity↔domain mappers. Hilt
  graph resolves at runtime.

- [ ] **Stage 5 — Theme**
  Ritual palette, typography, Material 3 theme. Dark by default.

- [ ] **Stage 6 — Navigation & placeholder screens**
  `NavGraph`, `Routes`, six placeholder Composables. Bottom bar wired.
  All screens reachable.

- [ ] **Stage 7 — First use case + seed data**
  `DrawDailyQuestsUseCase`. Small hardcoded starter quest pool seeded
  on first launch.

- [ ] **Stage 8 — Home vertical slice**
  Home screen draws 3 daily quests, displays them, tapping one marks
  it complete, stat increments persist, buff appears.

- [ ] **Stage 9 — Weekly loop**
  Quotas, stat theme, week advancement. Sunday recognized.

- [ ] **Stage 10 — Boss & biome scaffolding**
  Boss difficulty calculation, weekly boss resolution, biome
  progression and reset on biome end.

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