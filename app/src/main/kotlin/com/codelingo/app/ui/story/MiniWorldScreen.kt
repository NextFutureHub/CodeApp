package com.codelingo.app.ui.story

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.model.MiniHotspot
import com.codelingo.app.data.model.MiniScene
import com.codelingo.app.data.model.Task
import com.codelingo.app.ui.components.PrimaryButton
import com.codelingo.app.ui.tasks.BlocksTask
import com.codelingo.app.ui.tasks.CodeTask
import com.codelingo.app.ui.tasks.DebugTask
import com.codelingo.app.ui.tasks.FillGapsTask
import com.codelingo.app.ui.tasks.MatchTask
import com.codelingo.app.ui.tasks.QuizTask
import com.codelingo.app.data.model.TaskType
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.Success

@Composable
fun MiniWorldScreen(
    scene: MiniScene,
    accentColor: Color,
    onComplete: () -> Unit,
    onLoseLife: () -> Unit,
) {
    var completed by remember(scene.title) { mutableStateOf(setOf<String>()) }
    var activeHotspot by remember { mutableStateOf<MiniHotspot?>(null) }
    val allDone = completed.size >= scene.hotspots.size && scene.hotspots.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(scene.title, color = Foreground, fontWeight = FontWeight.Black, fontSize = 20.sp)
            scene.subtitle?.let {
                Text(it, color = MutedForeground, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Text(
                "Пройдено ${completed.size}/${scene.hotspots.size}",
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (activeHotspot != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Card)
                    .padding(12.dp),
            ) {
                Column {
                    Text(
                        activeHotspot!!.label,
                        color = Foreground,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    MiniHotspotTask(
                        task = activeHotspot!!.task,
                        onAnswer = { correct ->
                            if (correct) {
                                completed = completed + activeHotspot!!.id
                                activeHotspot = null
                            } else {
                                onLoseLife()
                            }
                        },
                    )
                }
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(accentColor.copy(alpha = 0.35f), Color(0xFF1A2332), Color(0xFF0F1419)),
                        ),
                    ),
            ) {
                val maxW = maxWidth
                val maxH = maxHeight
                scene.hotspots.forEach { hotspot ->
                    val isDone = hotspot.id in completed
                    val pulse by animateFloatAsState(if (isDone) 1f else 0.6f + (hotspot.id.hashCode() % 20) / 100f, tween(600))
                    val tint by animateColorAsState(if (isDone) Success else Primary, tween(400))
                    Box(
                        modifier = Modifier
                            .offset(
                                x = maxW * hotspot.x - maxW * hotspot.width / 2,
                                y = maxH * hotspot.y - maxH * hotspot.height / 2,
                            )
                            .size(width = maxW * hotspot.width, height = maxH * hotspot.height)
                            .alpha(pulse)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tint.copy(alpha = if (isDone) 0.35f else 0.55f))
                            .border(2.dp, if (isDone) Success else Primary, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isDone) { activeHotspot = hotspot },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isDone) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Success)
                        } else {
                            Text(
                                hotspot.label,
                                color = Foreground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(4.dp),
                            )
                        }
                    }
                }
            }
        }

        PrimaryButton(
            text = if (allDone) "К ФИНАЛУ" else "Пропустить мини-мир",
            onClick = onComplete,
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Composable
private fun MiniHotspotTask(task: Task, onAnswer: (Boolean) -> Unit) {
    when (task.type) {
        TaskType.QUIZ -> QuizTask(task.question, task.options.orEmpty(), task.correctAnswer.orEmpty(), onAnswer)
        TaskType.BLOCKS -> BlocksTask(task.question, task.blocks.orEmpty(), task.correctOrder.orEmpty(), onAnswer)
        TaskType.CODE -> CodeTask(task.question, task.starterCode.orEmpty(), task.expectedOutput.orEmpty(), task.hint, onAnswer)
        TaskType.DEBUG -> DebugTask(task.question, task.buggyCode.orEmpty(), task.fixedCode.orEmpty(), onAnswer)
        TaskType.MATCH -> MatchTask(task.question, task.pairs.orEmpty(), onAnswer)
        TaskType.FILL -> FillGapsTask(task.question, task.textParts.orEmpty(), task.fillOptions.orEmpty(), task.correctFill.orEmpty(), onAnswer)
    }
}
