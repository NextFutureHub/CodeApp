package com.codelingo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NunitoFamily = FontFamily.SansSerif

val CodeLingoTypography = Typography(
    displayLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Black, fontSize = 32.sp),
    headlineLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Black, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    titleLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyMedium = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    labelLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
    labelMedium = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp),
)
