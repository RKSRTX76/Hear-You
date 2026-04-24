package com.rksrtx76.hearyou.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rksrtx76.hearyou.data.model.ChatMessage
import com.rksrtx76.hearyou.data.model.ChatRole
import com.rksrtx76.hearyou.ui.theme.Amber
import com.rksrtx76.hearyou.ui.theme.Rose
import com.rksrtx76.hearyou.ui.theme.SurfaceCard
import com.rksrtx76.hearyou.ui.theme.TextPrimary

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role is ChatRole.User

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) {
                // AI avatar dot
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 4.dp)
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Amber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = message.voiceName?.first()?.toString() ?: "A",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.W700,
                        color      = Amber
                    )
                }
            }

            Column(
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                if (!isUser && message.voiceName != null) {
                    Text(
                        text      = message.voiceName,
                        fontSize  = 10.sp,
                        fontWeight = FontWeight.W600,
                        color     = Amber,
                        modifier  = Modifier.padding(start = 4.dp, bottom = 3.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart    = if (isUser) 18.dp else 4.dp,
                                topEnd      = if (isUser) 4.dp  else 18.dp,
                                bottomStart = 18.dp,
                                bottomEnd   = 18.dp
                            )
                        )
                        .background(
                            if (isUser) Rose.copy(alpha = 0.18f) else SurfaceCard
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text      = message.text,
                        fontSize  = 15.sp,
                        lineHeight = 22.sp,
                        color     = TextPrimary
                    )
                }
            }

            if (isUser) {
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}
