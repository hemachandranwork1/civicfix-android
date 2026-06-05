package com.civicfix.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CivicTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 34.sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 15.sp, lineHeight = 22.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 14.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 11.sp, letterSpacing = 0.5.sp)
)
