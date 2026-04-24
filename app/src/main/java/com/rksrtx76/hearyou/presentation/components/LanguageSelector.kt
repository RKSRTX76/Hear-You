package com.rksrtx76.hearyou.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.ui.theme.Amber
import com.rksrtx76.hearyou.ui.theme.BorderColor
import com.rksrtx76.hearyou.ui.theme.Midnight
import com.rksrtx76.hearyou.ui.theme.SurfaceCard
import com.rksrtx76.hearyou.ui.theme.TextSecondary

@Composable
fun LanguageSelector(
    selected: ConversationLanguage,
    onSelect: (ConversationLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(SurfaceCard)
            .border(1.dp, BorderColor, shape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConversationLanguage.entries.forEach { language ->
            val isSelected = selected == language
            val bgColor    = if (isSelected) Amber else Color.Transparent
            val textColor  = if (isSelected) Midnight else TextSecondary
            val weight     = if (isSelected) FontWeight.W700 else FontWeight.W400

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        animateColorAsState(bgColor, label = "langBg").value
                    )
                    .clickable { onSelect(language) }
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = language.displayName,
                    fontSize   = 12.sp,
                    fontWeight = weight,
                    color      = animateColorAsState(textColor, label = "langText").value
                )
            }
        }
    }
}
