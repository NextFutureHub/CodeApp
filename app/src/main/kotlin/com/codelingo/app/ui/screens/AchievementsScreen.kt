package com.codelingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.model.ACHIEVEMENTS
import com.codelingo.app.data.model.GameState
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Warning

@Composable
fun AchievementsScreen(state: GameState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            RowWithIcon()
            Text(
                "${state.achievements.size} из ${ACHIEVEMENTS.size} получено",
                color = MutedForeground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(ACHIEVEMENTS) { ach ->
                val unlocked = ach.id in state.achievements
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (unlocked) 1f else 0.5f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card)
                        .border(
                            2.dp,
                            if (unlocked) Warning.copy(alpha = 0.3f) else Border,
                            RoundedCornerShape(16.dp),
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(if (unlocked) ach.icon else "🔒", fontSize = 32.sp)
                    Text(ach.title, color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, textAlign = TextAlign.Center)
                    Text(ach.description, color = MutedForeground, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun RowWithIcon() {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Warning, modifier = Modifier.padding(0.dp))
        Text("Достижения", color = Foreground, fontWeight = FontWeight.Black, fontSize = 24.sp)
    }
}
