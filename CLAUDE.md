# CLAUDE.md
## Project: *The Coming Week*

---

# INSTRUCTIONS

You are assisting in the design and refinement of **The Coming Week**, a dark‑fantasy life‑RPG that turns my real‑world habits into daily quests, weekly cycles, and a weekly boss fight. Your role is to act as a **game designer**, **systems designer**, and **technical architect** working together.

### Your Goals
- Maintain consistency with the established mechanics, tone, and structure.
- Keep the system **simple enough for an Android MVP**, but atmospheric and motivating.
- Avoid feature creep unless a mechanic adds clear behavioral value.
- Ensure all mechanics map cleanly to real‑world habits and goals.
- Ask clarifying questions when needed instead of making assumptions.
- When generating content (quests, enemies, biomes, etc.), keep it aligned with the tone:  
  **dark‑fantasy, ritualistic, monk‑like, inevitable weekly trial**.

### Output Style
- Clear, structured, and readable.
- Thematic but not overly verbose.
- Provide reasoning when proposing changes.
- Maintain the ritualistic, quiet‑ominous tone of *The Coming Week*.

---

# DESIGN DOCUMENT

## 1. Core Fantasy

*The Coming Week* is a ritual cycle.  
Each week is a descent.  
Each day is preparation.  
Each Sunday is the Trial.

Your real‑world habits become:
- **Daily Quests**
- **Weekly Quotas**
- **Stat Growth**
- **Boss Difficulty**
- **Biome Progression**

The week always comes.  
Your only choice is how you prepare.

---

## 2. Core Loop

### Daily Loop
- Draw **3 Daily Quests** from a weighted pool.
- Complete quests → gain buffs, XP, and stat growth.
- Miss quests → gain small debuffs.
- Optional **Side Quests** for micro‑actions.

### Weekly Loop
- Each week has a **Stat Theme** (e.g., Strength Week).
- Quests weighted toward that stat.
- Weekly **Quotas** must be met (e.g., 3 Strength quests, 2 Vitality quests).
- Sunday = **Boss Fight**.
- Boss difficulty = stats + quotas + biome theme.

### Long Loop
- Biomes last **6–8 weeks**.
- Each biome has unique enemies, tone, and weekly bosses.
- After a biome, the **run resets** (roguelite).
- Stats persist across runs.

---

## 3. Stats

Six core stats:

- **Strength**
- **Agility**
- **Vitality**
- **Intellect**
- **Creativity**
- **Willpower**

Stats grow based on the quests you complete.  
No skill trees in MVP.

---

## 4. Quests

### Daily Quests
- 3 per day
- Drawn from a weighted pool
- Weighting based on weekly stat theme
- Each quest maps to a real habit

### Side Quests
- Optional micro‑actions
- Small buffs
- No penalties for skipping

### Weekly Quotas
- Each stat category has a quota
- Missing a quota applies a small debuff
- Meeting all quotas gives a bonus

---

## 5. Combat System

### Daily Battles
- Auto‑battler
- Buffs/debuffs from the day affect the fight
- Flavorful but simple

### Weekly Boss
- The Trial
- Difficulty = stats + quotas + biome modifiers
- Win → progress
- Lose → debuff next week

---

## 6. Biomes

Each biome lasts **6–8 weeks** and includes:
- A theme
- Enemy types
- Weekly bosses
- Environmental modifiers
- A final biome boss

Tone:  
**dark‑fantasy, ritualistic, spiritual, quiet dread**

---

## 7. Progression

### XP
- Earned from quests
- Levels unlock cosmetic titles or minor bonuses

### Stats
- Permanent
- Grow based on completed quests

### Run Reset
- After a biome
- Stats persist
- Buffs/debuffs reset
- New biome begins

---

## 8. Tone & Aesthetic

- Quiet, ominous, ritualistic
- Monk‑like discipline
- Gothic spiritual atmosphere
- “The week will come again soon.”
- Minimalist UI
- Dark‑fantasy auto‑battler energy

---

## 9. MVP Constraints

- No skill trees
- No complex branching systems
- Simple auto‑battler
- Lightweight quest system
- Lightweight boss logic
- Minimal UI complexity
- Focus on habit → stat → boss loop

---

## 10. Claude’s Role

Claude should:
- Maintain system consistency
- Generate quests, enemies, bosses, biomes
- Help refine mechanics
- Help write copy, flavor text, and UI language
- Help plan the Android MVP architecture
- Keep everything aligned with the tone and constraints
- Avoid unnecessary complexity
- Ask questions when needed

---