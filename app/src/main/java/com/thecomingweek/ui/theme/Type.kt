package com.thecomingweek.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.thecomingweek.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val vt323 = FontFamily(
    Font(googleFont = GoogleFont("VT323"), fontProvider = fontProvider)
)

private val jetbrainsMono = FontFamily(
    Font(googleFont = GoogleFont("JetBrains Mono"), fontProvider = fontProvider)
)

private val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = vt323),
    displayMedium = baseline.displayMedium.copy(fontFamily = vt323),
    displaySmall = baseline.displaySmall.copy(fontFamily = vt323),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = vt323),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = vt323),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = vt323),
    titleLarge = baseline.titleLarge.copy(fontFamily = vt323),
    titleMedium = baseline.titleMedium.copy(fontFamily = jetbrainsMono),
    titleSmall = baseline.titleSmall.copy(fontFamily = jetbrainsMono),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = jetbrainsMono),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = jetbrainsMono),
    bodySmall = baseline.bodySmall.copy(fontFamily = jetbrainsMono),
    labelLarge = baseline.labelLarge.copy(fontFamily = jetbrainsMono),
    labelMedium = baseline.labelMedium.copy(fontFamily = jetbrainsMono),
    labelSmall = baseline.labelSmall.copy(fontFamily = jetbrainsMono),
)
