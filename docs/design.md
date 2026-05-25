# The Coming Week — Design

## Visual register
Late-80s / early-90s PC occult. Doom-metal sigil aesthetic.
Minimalist pixel auto-battler — reference: He Is Coming (Chronocle, 2025).

## Palette

### UI palette (locked, 5 colors)
Used for all UI chrome: backgrounds, text, borders, buttons, status.
- Pitch  #0A0A0A  background
- Ash    #1C1A1A  surfaces
- Bone   #E8E2D5  primary text, sprite highlights
- Blood  #8B0A1A  primary accent, danger, the Trial
- Ember  #C97A2B  secondary accent, XP, candlelight

### Sprite palette (~16 colors)
Used for enemies, bosses, environment art. Includes the 5 UI colors
plus the following. Pure saturation is forbidden — everything should
look painted in candlelight.

- Bile    #4A5D23  rot, goblin flesh
- Moss    #2A3818  green shadow
- Bruise  #3A1F4A  poison, undead
- Sigil   #5A2E6B  purple highlight
- Iron    #4A4A4A  armor, weapons
- Rust    #6B3A1F  corroded metal
- Pallor  #9A8A7A  sickly flesh, bone
- Wound   #5A0810  deep blood shadow
- Hearth  #D9A24A  fire, gold (sprite-only)
- Void    #000000  sprite outlines
- [+1 biome-specific accent, rotates per biome]

## Typography
- Display: VT323 (Google Fonts)
- Body: JetBrains Mono (Google Fonts)

## Grid & sprites
- Snap UI to 8dp
- Enemy/quest sprites: 32×32, rendered with FilterQuality.None
- Boss sprites: 32×32 or 64×64
- Sigil glyphs: 16×16 or 24×24
- No rounded corners (0–2dp max). No shadows. No gradients.

## Sunday (the Trial)
Distinct treatment from M–Sa: blood-red vignette, "THE TRIAL"
banner in Blood, pixel display font, stripped UI chrome.

## Asset pipeline
- Authoring: Aseprite
- Sprite sheets exported as PNG, placed in app/src/main/res/drawable-nodpi/
- AI-assisted generation acceptable for first-pass sprites;
  hand cleanup required before commit.

## MVP scope
- 1 biome
- 6 enemy sprites (one per stat)
- 6–8 boss sprites (one per week)
- 1 final biome boss