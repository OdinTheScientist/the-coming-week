package com.thecomingweek.ui.screen.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val FADE_IN_MS = 800
private const val DISPLAY_MS = 2500
private const val FADE_OUT_MS = 600
private const val PULSE_DURATION_MS = 1500
private const val PULSE_MIN_ALPHA = 0.7f

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var screenVisible by remember { mutableStateOf(false) }
    var fadingOut by remember { mutableStateOf(false) }
    var pulseActive by remember { mutableStateOf(false) }

    val screenAlpha by animateFloatAsState(
        targetValue = when {
            fadingOut -> 0f
            screenVisible -> 1f
            else -> 0f
        },
        animationSpec = tween(if (fadingOut) FADE_OUT_MS else FADE_IN_MS),
        label = "screen_alpha",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "title_pulse")
    val titlePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = PULSE_MIN_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_DURATION_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "title_pulse_alpha",
    )

    LaunchedEffect(Unit) {
        screenVisible = true
        delay(FADE_IN_MS.toLong())
        pulseActive = true
        delay(DISPLAY_MS.toLong())
        pulseActive = false
        fadingOut = true
        delay(FADE_OUT_MS.toLong())
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .alpha(screenAlpha),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onBackground),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "The Coming Week",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    letterSpacing = 4.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(if (pulseActive) titlePulse else 1f),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onBackground),
            )
        }
    }
}
