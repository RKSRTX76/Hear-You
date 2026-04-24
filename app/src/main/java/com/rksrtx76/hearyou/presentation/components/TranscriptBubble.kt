package com.rksrtx76.hearyou.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rksrtx76.hearyou.ui.theme.MicActive
import com.rksrtx76.hearyou.ui.theme.TextSecondary

@Composable
fun TranscriptBubble(
    transcript: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = transcript.isNotBlank(),
        enter   = fadeIn() + slideInVertically { it },
        exit    = fadeOut() + slideOutVertically { it },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MicActive.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text      = transcript,
                fontSize  = 15.sp,
                fontStyle = FontStyle.Italic,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}
