package com.thecomingweek.domain.usecase.internal

// ASCII art for the placeholder Warden, shown on the Boss screen. ASCII is a
// deliberate aesthetic choice — late-80s PC ritual, doom-metal sigil — not a
// stand-in for a sprite we owe later.
//
// Every line is padded to the same width (13 chars) so that monospaced,
// centre-aligned rendering keeps the columns square. The two '=' glyphs are the
// only '='s in the block, which lets the UI tint exactly them Blood to give the
// Warden narrowed, judging eyes without any per-offset bookkeeping.
//
// post-MVP: authored bosses each carry their own art the way this constant
// stands in for the one placeholder.
internal const val WARDEN_ART: String =
    "    _____    \n" +
    "   /     \\   \n" +
    "  /       \\  \n" +
    "  | (= =) |  \n" +
    "  |  \\_/  |  \n" +
    "  |       |  \n" +
    "  |       |  \n" +
    " /         \\ \n" +
    "/___________\\"
