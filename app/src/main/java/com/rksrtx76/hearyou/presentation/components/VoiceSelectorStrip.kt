package com.rksrtx76.hearyou.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rksrtx76.hearyou.data.model.ElevenLabsVoice
import com.rksrtx76.hearyou.ui.theme.Amber
import com.rksrtx76.hearyou.ui.theme.BorderColor
import com.rksrtx76.hearyou.ui.theme.Midnight
import com.rksrtx76.hearyou.ui.theme.SurfaceCard
import com.rksrtx76.hearyou.ui.theme.TextMuted
import com.rksrtx76.hearyou.ui.theme.TextSecondary

@Composable
fun VoiceSelectorStrip(
    voices: List<ElevenLabsVoice>,
    selectedVoice: ElevenLabsVoice,
    onVoiceSelect: (ElevenLabsVoice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(voices, key = { it.voiceId }) { voice ->
            VoiceChip(
                voice      = voice,
                isSelected = voice.voiceId == selectedVoice.voiceId,
                onClick    = { onVoiceSelect(voice) }
            )
        }
    }
}

@Composable
private fun VoiceChip(
    voice: ElevenLabsVoice,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor     = animateColorAsState(
        if (isSelected) Amber else SurfaceCard,
        label = "voiceBg"
    ).value
    val textColor   = animateColorAsState(
        if (isSelected) Midnight else TextSecondary,
        label = "voiceText"
    ).value
    val borderColor = animateColorAsState(
        if (isSelected) Amber else BorderColor,
        label = "voiceBorder"
    ).value
    val dotColor    = animateColorAsState(
        if (isSelected) Midnight.copy(alpha = 0.7f) else TextMuted,
        label = "voiceDot"
    ).value

    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Avatar dot with initial
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(dotColor.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = voice.name.first().toString(),
                fontSize   = 11.sp,
                fontWeight = FontWeight.W700,
                color      = dotColor
            )
        }

        Column {
            Text(
                text       = voice.name,
                fontSize   = 12.sp,
                fontWeight = if (isSelected) FontWeight.W700 else FontWeight.W500,
                color      = textColor,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = voice.description,
                fontSize = 9.sp,
                color    = if (isSelected) Midnight.copy(alpha = 0.6f) else TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}