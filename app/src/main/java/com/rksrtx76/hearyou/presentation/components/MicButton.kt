package com.rksrtx76.hearyou.presentation.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rksrtx76.hearyou.presentation.viewmodel.VoiceState
import com.rksrtx76.hearyou.ui.theme.Amber
import com.rksrtx76.hearyou.ui.theme.MicActive
import com.rksrtx76.hearyou.ui.theme.MicIdle
import com.rksrtx76.hearyou.ui.theme.MicSpeaking
import com.rksrtx76.hearyou.ui.theme.SurfaceCard
import com.rksrtx76.hearyou.ui.theme.TextMuted

@Composable
fun MicButton(
    voiceState: VoiceState,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isListening  = voiceState is VoiceState.Listening
    val isProcessing = voiceState is VoiceState.Processing
    val isSpeaking   = voiceState is VoiceState.Speaking
    val isError      = voiceState is VoiceState.Error

    val accentColor = when {
        isListening  -> MicActive
        isSpeaking   -> MicSpeaking
        isError      -> MaterialTheme.colorScheme.error
        else         -> MicIdle
    }

    // Pulse rings — only shown when listening
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.7f,
        animationSpec  = infiniteRepeatable(
            animation = tween(1400, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue  = 0.35f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 2.1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, 200, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue  = 0.2f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, 200, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    // Speaking wave animation
    val speakingScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.15f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speakScale"
    )

    // Spinning arc for processing
    val rotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer pulse rings
        if (isListening) {
            PulseRing(scale = pulseScale2, alpha = pulseAlpha2, color = accentColor, size = 72.dp)
            PulseRing(scale = pulseScale1, alpha = pulseAlpha1, color = accentColor, size = 72.dp)
        }

        // Speaking glow
        if (isSpeaking) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(speakingScale)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
            )
        }

        // Processing ring
        if (isProcessing) {
            CircularProgressIndicator(
                modifier  = Modifier.size(80.dp),
                color     = Amber,
                strokeWidth = 2.dp
            )
        }

        // Mic core button
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isListening  -> MicActive
                        isSpeaking   -> MicSpeaking
                        isError      -> MaterialTheme.colorScheme.error
                        isProcessing -> SurfaceCard
                        else         -> SurfaceCard
                    }
                )
                .border(
                    width = 1.5.dp,
                    color = accentColor,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = ripple(bounded = false, color = accentColor),
                    enabled           = !isProcessing,
                    onClick           = onMicClick
                ),
            contentAlignment = Alignment.Center
        ) {
            val iconTint = when {
                isListening || isSpeaking -> Color.White
                isError      -> Color.White
                isProcessing -> TextMuted
                else         -> Amber
            }

            Icon(
                imageVector = when {
                    isListening  -> Icons.Filled.MicOff
                    isSpeaking   -> Icons.Filled.VolumeUp
                    isProcessing -> Icons.Filled.HourglassEmpty
                    isError      -> Icons.Filled.ErrorOutline
                    else         -> Icons.Filled.Mic
                },
                contentDescription = when {
                    isListening  -> "Stop listening"
                    isSpeaking   -> "Speaking"
                    isProcessing -> "Processing"
                    else         -> "Start speaking"
                },
                tint     = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PulseRing(
    scale: Float,
    alpha: Float,
    color: Color,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}