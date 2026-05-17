package com.codelingo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.CardElevated
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.LessonInputFontFamily
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary

/**
 * Multiline code input. [BasicTextField] + [LessonInputFontFamily] (Nunito Latin + Cyrillic .ttf).
 * [KeyboardOptions.Default] — not Ascii/Email; language is chosen on the system keyboard.
 */
@Composable
fun CodeLingoMultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "Напиши код здесь...",
    minLines: Int = 5,
    borderColor: Color = Border,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardElevated)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        textStyle = TextStyle(
            color = Foreground,
            fontFamily = LessonInputFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            platformStyle = PlatformTextStyle(includeFontPadding = true),
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
        ),
        minLines = minLines,
        cursorBrush = SolidColor(Primary),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        color = MutedForeground,
                        fontFamily = LessonInputFontFamily,
                        fontSize = 14.sp,
                    )
                }
                inner()
            }
        },
    )
}
