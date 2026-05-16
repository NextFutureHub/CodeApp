package com.codelingo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.model.GameState
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Destructive
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.Muted
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.PrimaryForeground
import com.codelingo.app.ui.theme.PrimaryGlow
import com.codelingo.app.ui.theme.Streak
import com.codelingo.app.ui.theme.Xp

@Composable
fun ContentWidth(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 448.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val bg = if (enabled) Primary else Muted
    val fg = if (enabled) PrimaryForeground else MutedForeground
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = fg, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@Composable
fun StatsBar(state: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("🔥", fontSize = 18.sp)
            Text("${state.streak}", color = Streak, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("⚡", fontSize = 18.sp)
            Text("${state.xp}", color = Xp, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("❤️", fontSize = 16.sp)
            Text("${state.lives}", color = Destructive, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun XpProgressBar(xp: Int, level: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("${xp % 100} / 100 XP", color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Уровень ${level + 1}", color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(Muted),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((xp % 100) / 100f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Primary, PrimaryGlow))),
            )
        }
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(Muted),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
    }
}

@Composable
fun CardSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val base = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Card)
        .border(1.dp, Border, RoundedCornerShape(16.dp))
    Box(
        modifier = if (onClick != null) {
            base.then(Modifier.clickable(onClick = onClick))
        } else {
            base
        }.then(modifier),
    ) {
        content()
    }
}

@Composable
fun GradientText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Primary,
        fontWeight = FontWeight.Black,
    )
}
