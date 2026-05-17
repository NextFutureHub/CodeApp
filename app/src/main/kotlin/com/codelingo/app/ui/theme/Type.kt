package com.codelingo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codelingo.app.R

/**
 * Nunito (Duolingo-style) with explicit Latin + Cyrillic subsets from Google Fonts / Fontsource.
 * Latin and Cyrillic are separate .ttf files; Compose picks the next font when a glyph is missing.
 */
val NunitoFamily: FontFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_cyrillic_400, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_cyrillic_600, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_cyrillic_700, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_cyrillic_800, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black),
    Font(R.font.nunito_cyrillic_900, FontWeight.Black),
)

/** Same family for code inputs — no KeyboardType.Ascii, no forced locale. */
val LessonInputFontFamily: FontFamily = NunitoFamily

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
