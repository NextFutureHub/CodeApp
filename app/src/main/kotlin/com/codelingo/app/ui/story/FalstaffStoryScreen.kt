package com.codelingo.app.ui.story

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.R
import com.codelingo.app.data.model.StoryBeat
import com.codelingo.app.data.voice.FalstaffVoiceRepository
import com.codelingo.app.ui.components.PrimaryButton
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.PrimaryForeground

@Composable
fun FalstaffStoryScreen(
    lessonId: String,
    beats: List<StoryBeat>,
    title: String,
    voiceRepository: FalstaffVoiceRepository,
    onComplete: () -> Unit,
) {
    if (beats.isEmpty()) {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    var lineIndex by remember(lessonId, title) { mutableIntStateOf(0) }
    var showChoiceResponse by remember { mutableStateOf<String?>(null) }
    val beat = beats[lineIndex.coerceIn(beats.indices)]

    val bounce by rememberInfiniteTransition(label = "falstaff").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "bounce",
    )
    val emotionScale = when (beat.emotion) {
        "happy", "celebrate" -> 1f + bounce * 0.04f
        "think" -> 1f - bounce * 0.02f
        else -> 1f + bounce * 0.02f
    }

    val beatId = beat.id ?: "${lessonId}-line-$lineIndex"
    val displayText = showChoiceResponse ?: beat.text

    LaunchedEffect(lineIndex, showChoiceResponse, beatId) {
        voiceRepository.speak(lessonId, beatId, displayText, beat.audioUrl)
    }

    DisposableEffect(Unit) {
        onDispose { voiceRepository.stop() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(title, color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Фальстаф", color = Primary, fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.falstaff),
                contentDescription = "Фальстаф",
                modifier = Modifier
                    .size(120.dp)
                    .scale(emotionScale)
                    .offset(y = (-bounce * 6).dp),
                contentScale = ContentScale.Fit,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Card)
                    .padding(16.dp),
            ) {
                Text(displayText, color = Foreground, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 22.sp)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (showChoiceResponse == null && !beat.choices.isNullOrEmpty()) {
                beat.choices.forEach { choice ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            showChoiceResponse = choice.response ?: choice.text
                        },
                        label = { Text(choice.text) },
                        colors = FilterChipDefaults.filterChipColors(
                            labelColor = Foreground,
                            selectedContainerColor = Primary,
                            selectedLabelColor = PrimaryForeground,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                PrimaryButton(
                    text = if (lineIndex >= beats.lastIndex) "ДАЛЬШЕ" else "ПРОДОЛЖИТЬ",
                    onClick = {
                        voiceRepository.stop()
                        if (lineIndex >= beats.lastIndex) {
                            onComplete()
                        } else {
                            lineIndex++
                            showChoiceResponse = null
                        }
                    },
                )
            }
        }
    }
}
