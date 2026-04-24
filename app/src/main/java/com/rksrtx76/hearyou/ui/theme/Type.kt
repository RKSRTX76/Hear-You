package com.rksrtx76.hearyou.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.W300,
        fontSize   = 48.sp,
        letterSpacing = (-0.5).sp,
        color      = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize   = 24.sp,
        letterSpacing = (-0.2).sp,
        color      = TextPrimary
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize   = 18.sp,
        color      = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = TextSecondary
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize   = 11.sp,
        letterSpacing = 0.5.sp,
        color      = TextMuted
    )
)