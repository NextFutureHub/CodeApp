package com.codelingo.app.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.model.GameState
import com.codelingo.app.data.model.Lesson
import com.codelingo.app.data.model.Task
import com.codelingo.app.data.model.TaskType
import com.codelingo.app.ui.components.PrimaryButton
import com.codelingo.app.ui.tasks.BlocksTask
import com.codelingo.app.ui.tasks.CodeTask
import com.codelingo.app.ui.tasks.DebugTask
import com.codelingo.app.ui.tasks.FillGapsTask
import com.codelingo.app.ui.tasks.MatchTask
import com.codelingo.app.ui.tasks.QuizTask
import com.codelingo.app.ui.theme.Background
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Destructive
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.Muted
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.Xp
import com.codelingo.app.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private fun lessonXpEarned(xpReward: Int, correctCount: Int, taskCount: Int): Int {
    if (taskCount <= 0) return 0
    return (xpReward * (correctCount.toFloat() / taskCount)).roundToInt()
}

private sealed interface LessonPhase {
    data object Theory : LessonPhase
    data class TaskStep(val index: Int) : LessonPhase
    data object Finished : LessonPhase
}

@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    state: GameState,
    courseRepository: CourseRepository,
    gameViewModel: GameViewModel,
    onBack: () -> Unit,
) {
    val lesson = courseRepository.getLesson(courseId, lessonId)
    if (lesson == null) {
        Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Урок не найден", color = Foreground)
        }
        return
    }

    val taskCount = lesson.tasks.size
    var currentTask by remember(lessonId) { mutableIntStateOf(if (lesson.theory != null) -1 else 0) }
    var correctCount by remember(lessonId) { mutableIntStateOf(0) }
    var advanceAfterAnswer by remember(lessonId) { mutableStateOf(false) }

    LaunchedEffect(advanceAfterAnswer, taskCount) {
        if (!advanceAfterAnswer) return@LaunchedEffect
        delay(300)
        advanceAfterAnswer = false
        when {
            currentTask < taskCount - 1 -> currentTask++
            currentTask < taskCount -> currentTask = taskCount
        }
    }

    val lessonPhase: LessonPhase = when {
        currentTask == -1 -> LessonPhase.Theory
        currentTask >= taskCount -> LessonPhase.Finished
        else -> LessonPhase.TaskStep(currentTask)
    }
    val progress = if (taskCount > 0) {
        ((currentTask + 1).toFloat() / taskCount).coerceIn(0f, 1f)
    } else {
        1f
    }
    val progressAnim by animateFloatAsState(progress.coerceAtLeast(0.05f), animationSpec = tween(300))

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = MutedForeground)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Muted),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnim)
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Primary),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Destructive, modifier = Modifier.padding(end = 4.dp))
                Text("${state.lives}", color = Destructive, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
        }

        Crossfade(
            targetState = lessonPhase,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "lesson",
        ) { phase ->
            when (phase) {
                LessonPhase.Theory -> TheoryPhase(lesson, onContinue = { currentTask = 0 })
                LessonPhase.Finished -> FinishedPhase(
                    lesson = lesson,
                    correctCount = correctCount,
                    onFinish = {
                        val earnedXp = lessonXpEarned(lesson.xpReward, correctCount, taskCount)
                        gameViewModel.addXp(earnedXp)
                        if (correctCount == taskCount && taskCount > 0) {
                            gameViewModel.completeLesson(lesson.id)
                        }
                        onBack()
                    },
                )
                is LessonPhase.TaskStep -> {
                    val taskIndex = phase.index
                    if (taskIndex in 0 until taskCount) {
                        TaskContent(
                            task = lesson.tasks[taskIndex],
                            modifier = Modifier.fillMaxSize(),
                        ) { correct ->
                            if (correct) correctCount++
                            else gameViewModel.loseLife()
                            if (currentTask < taskCount) {
                                advanceAfterAnswer = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonScrollableContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        content()
    }
}

@Composable
private fun TheoryPhase(lesson: Lesson, onContinue: () -> Unit) {
    LessonScrollableContent(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("📖", fontSize = 48.sp)
            Text(lesson.title, color = Foreground, fontWeight = FontWeight.Black, fontSize = 20.sp, modifier = Modifier.padding(vertical = 16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Card)
                    .padding(20.dp),
            ) {
                Text(
                    lesson.theory ?: "",
                    color = Foreground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                )
            }
            PrimaryButton("ПРОДОЛЖИТЬ", onClick = onContinue, modifier = Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun FinishedPhase(lesson: Lesson, correctCount: Int, onFinish: () -> Unit) {
    val taskCount = lesson.tasks.size
    val emoji = when {
        taskCount > 0 && correctCount == taskCount -> "🏆"
        correctCount > 0 -> "⭐"
        else -> "😅"
    }
    val earnedXp = lessonXpEarned(lesson.xpReward, correctCount, taskCount)

    LessonScrollableContent(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 64.sp)
            Text("Урок завершён!", color = Foreground, fontWeight = FontWeight.Black, fontSize = 24.sp, modifier = Modifier.padding(top = 16.dp))
            Text("$correctCount из $taskCount правильно", color = MutedForeground, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Xp.copy(alpha = 0.1f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("⚡", fontSize = 24.sp)
                Text("+$earnedXp XP", color = Xp, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
            PrimaryButton("ПРОДОЛЖИТЬ", onClick = onFinish)
        }
    }
}

@Composable
private fun TaskContent(
    task: Task,
    modifier: Modifier = Modifier,
    onAnswer: (Boolean) -> Unit,
) {
    LessonScrollableContent(modifier = modifier) {
        when (task.type) {
            TaskType.QUIZ -> QuizTask(
                question = task.question,
                options = task.options.orEmpty(),
                correctAnswer = task.correctAnswer.orEmpty(),
                onAnswer = onAnswer,
            )
            TaskType.BLOCKS -> BlocksTask(
                question = task.question,
                blocks = task.blocks.orEmpty(),
                correctOrder = task.correctOrder.orEmpty(),
                onAnswer = onAnswer,
            )
            TaskType.CODE -> CodeTask(
                question = task.question,
                starterCode = task.starterCode.orEmpty(),
                expectedOutput = task.expectedOutput.orEmpty(),
                hint = task.hint,
                onAnswer = onAnswer,
            )
            TaskType.DEBUG -> DebugTask(
                question = task.question,
                buggyCode = task.buggyCode.orEmpty(),
                fixedCode = task.fixedCode.orEmpty(),
                onAnswer = onAnswer,
            )
            TaskType.MATCH -> MatchTask(
                question = task.question,
                pairs = task.pairs.orEmpty(),
                onAnswer = onAnswer,
            )
            TaskType.FILL -> FillGapsTask(
                question = task.question,
                textParts = task.textParts.orEmpty(),
                options = task.fillOptions.orEmpty(),
                correctFill = task.correctFill.orEmpty(),
                onAnswer = onAnswer,
            )
        }
    }
}
