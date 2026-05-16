@file:OptIn(ExperimentalLayoutApi::class)

package com.codelingo.app.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.model.MatchPair
import com.codelingo.app.ui.components.PrimaryButton
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.CardElevated
import com.codelingo.app.ui.theme.Destructive
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.Muted
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.PrimaryForeground
import com.codelingo.app.ui.theme.Success
import com.codelingo.app.ui.theme.Warning
import kotlinx.coroutines.delay

private fun cleanCode(s: String) = s.replace(Regex("\\s+"), " ").trim().lowercase()

@Composable
fun QuizTask(
    question: String,
    options: List<String>,
    correctAnswer: String,
    onAnswer: (Boolean) -> Unit,
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var answered by remember { mutableStateOf(false) }

    TaskContainer(question) {
        options.forEach { option ->
            val isCorrect = answered && option == correctAnswer
            val isWrong = answered && option == selected && option != correctAnswer
            val borderColor = when {
                !answered && option == selected -> Primary
                isCorrect -> Success
                isWrong -> Destructive
                else -> Border
            }
            val bgColor = when {
                !answered && option == selected -> Primary.copy(alpha = 0.1f)
                isCorrect -> Success.copy(alpha = 0.1f)
                isWrong -> Destructive.copy(alpha = 0.1f)
                else -> Card
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                    .clickable(enabled = !answered) { selected = option }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(option, color = if (answered && !isCorrect && !isWrong) MutedForeground else Foreground, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                if (isCorrect) Icon(Icons.Default.Check, contentDescription = null, tint = Success)
                if (isWrong) Icon(Icons.Default.Close, contentDescription = null, tint = Destructive)
            }
        }
        if (!answered) {
            PrimaryButton("ПРОВЕРИТЬ", onClick = {
                if (selected != null) answered = true
            }, enabled = selected != null)
        } else {
            SubmitAnswerEffect(answered) { onAnswer(selected == correctAnswer) }
        }
    }
}

@Composable
fun BlocksTask(
    question: String,
    blocks: List<String>,
    correctOrder: List<String>,
    onAnswer: (Boolean) -> Unit,
) {
    var placed by remember { mutableStateOf(listOf<String>()) }
    var available by remember { mutableStateOf(blocks) }
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    TaskContainer(question) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (answered && isCorrect) Success.copy(0.05f) else if (answered) Destructive.copy(0.05f) else Card)
                .border(2.dp, if (answered && isCorrect) Success else if (answered) Destructive else Border, RoundedCornerShape(16.dp))
                .padding(12.dp),
        ) {
            if (placed.isEmpty()) {
                Text("Перетащи блоки сюда...", color = MutedForeground, fontSize = 14.sp)
            } else {
                FlowRowBlocks(placed, enabled = !answered) { idx ->
                    val block = placed[idx]
                    placed = placed.filterIndexed { i, _ -> i != idx }
                    available = available + block
                }
            }
        }
        FlowRowBlocks(available, enabled = !answered) { idx ->
            val block = available[idx]
            placed = placed + block
            available = available.filterIndexed { i, _ -> i != idx }
        }
        if (answered) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(if (isCorrect) Icons.Default.Check else Icons.Default.Close, contentDescription = null, tint = if (isCorrect) Success else Destructive)
                Text(if (isCorrect) "Правильно! 🎉" else "Неверно, попробуй ещё раз", color = if (isCorrect) Success else Destructive, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        if (!answered) {
            PrimaryButton("ПРОВЕРИТЬ", onClick = {
                isCorrect = placed == correctOrder
                answered = true
            }, enabled = placed.isNotEmpty())
        } else {
            SubmitAnswerEffect(answered) { onAnswer(isCorrect) }
        }
    }
}

@Composable
private fun SubmitAnswerEffect(answered: Boolean, onSubmit: () -> Unit) {
    LaunchedEffect(answered) {
        if (answered) {
            delay(1200)
            onSubmit()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowBlocks(items: List<String>, enabled: Boolean, onRemove: (Int) -> Unit) {
    FlowRow(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEachIndexed { idx, block ->
            Text(
                block,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.2f))
                    .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable(enabled = enabled) { onRemove(idx) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = Primary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun CodeTask(
    question: String,
    starterCode: String,
    expectedOutput: String,
    hint: String?,
    onAnswer: (Boolean) -> Unit,
) {
    var code by remember { mutableStateOf(starterCode) }
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    TaskContainer(question) {
        CodeEditor(code, answered, isCorrect, onValueChange = { code = it })
        if (hint != null && !answered) {
            Row(
                modifier = Modifier.clickable { showHint = !showHint },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Warning, modifier = Modifier.padding(0.dp))
                Text(if (showHint) hint else "Показать подсказку", color = Warning, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        AnswerFeedback(answered, isCorrect, success = "Отлично! 🎉", failure = "Ожидалось: $expectedOutput")
        if (!answered) {
            PrimaryButton("ПРОВЕРИТЬ", onClick = {
                isCorrect = cleanCode(code).contains(cleanCode(expectedOutput))
                answered = true
            }, enabled = code.trim().isNotEmpty())
        } else {
            SubmitAnswerEffect(answered) { onAnswer(isCorrect) }
        }
    }
}

@Composable
fun DebugTask(
    question: String,
    buggyCode: String,
    fixedCode: String,
    onAnswer: (Boolean) -> Unit,
) {
    var code by remember { mutableStateOf(buggyCode) }
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    TaskContainer(question, header = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BugReport, contentDescription = null, tint = Destructive)
            Text(question, color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }
    }) {
        CodeEditor(code, answered, isCorrect, onValueChange = { code = it })
        AnswerFeedback(answered, isCorrect, success = "Баг исправлен! 🐛✨", failure = "Правильный код: $fixedCode")
        if (!answered) {
            PrimaryButton("ПРОВЕРИТЬ", onClick = {
                isCorrect = cleanCode(code) == cleanCode(fixedCode)
                answered = true
            })
        } else {
            SubmitAnswerEffect(answered) { onAnswer(isCorrect) }
        }
    }
}

@Composable
fun MatchTask(
    question: String,
    pairs: List<MatchPair>,
    onAnswer: (Boolean) -> Unit,
) {
    val rightOptions = remember(pairs) { pairs.map { it.right }.shuffled() }
    var selectedLeft by remember { mutableStateOf(pairs.firstOrNull()?.left) }
    var matches by remember { mutableStateOf(mapOf<String, String>()) }
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    TaskContainer(question, header = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Link, contentDescription = null, tint = Primary)
            Text(question, color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }
    }) {
        Text("Выбери термин слева, затем подходящий вариант справа.", color = MutedForeground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        pairs.forEach { pair ->
            val isSelected = selectedLeft == pair.left
            val currentMatch = matches[pair.left]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Primary.copy(0.1f) else Card)
                    .border(2.dp, if (isSelected) Primary else Border, RoundedCornerShape(16.dp))
                    .clickable(enabled = !answered) { selectedLeft = pair.left }
                    .padding(16.dp),
            ) {
                Text(pair.left, color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                if (currentMatch != null) {
                    Text(currentMatch, color = Foreground, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                } else {
                    Text("Свяжи с вариантом справа", color = MutedForeground, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rightOptions.forEach { option ->
                val isUsed = option in matches.values
                Text(
                    option,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isUsed) Primary.copy(0.1f) else CardElevated)
                        .border(1.dp, if (isUsed) Primary.copy(0.4f) else Border, RoundedCornerShape(16.dp))
                        .clickable(enabled = !answered && selectedLeft != null) {
                            val left = selectedLeft ?: return@clickable
                            val next = matches.filter { it.key != left && it.value != option }.toMutableMap()
                            next[left] = option
                            matches = next
                            selectedLeft = pairs.find { it.left !in next }?.left
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (isUsed) Primary else Foreground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
        AnswerFeedback(answered, isCorrect, success = "Все соответствия верны!", failure = "Есть неточность. Сравни термины и варианты еще раз.")
        if (!answered) {
            PrimaryButton("ПРОВЕРИТЬ", onClick = {
                isCorrect = pairs.all { matches[it.left] == it.right }
                answered = true
            }, enabled = matches.size == pairs.size)
        } else {
            SubmitAnswerEffect(answered) { onAnswer(isCorrect) }
        }
    }
}

@Composable
fun FillGapsTask(
    question: String,
    textParts: List<String>,
    options: List<String>,
    correctFill: List<String>,
    onAnswer: (Boolean) -> Unit,
) {
    var answers by remember { mutableStateOf(List<String?>(correctFill.size) { null }) }
    var available by remember { mutableStateOf(options) }
    var activeBlank by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    TaskContainer(question) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(24.dp))
                .padding(16.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                textParts.forEachIndexed { index, part ->
                    Text(part, color = Foreground, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    if (index < correctFill.size) {
                        val ans = answers[index]
                        Text(
                            ans ?: "_____",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (activeBlank == index) Primary.copy(0.1f) else CardElevated)
                                .border(2.dp, if (activeBlank == index) Primary else Border, RoundedCornerShape(12.dp))
                                .clickable(enabled = !answered) { activeBlank = index }
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            color = if (activeBlank == index) Primary else Foreground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            available.forEachIndexed { idx, option ->
                Text(
                    option,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardElevated)
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .clickable(enabled = !answered) {
                            val target = answers.indexOfFirst { it == null }.takeIf { it >= 0 } ?: activeBlank
                            val prev = answers[target]
                            val newAvailable = available.filterIndexed { i, _ -> i != idx }.toMutableList()
                            if (prev != null) newAvailable.add(prev)
                            answers = answers.toMutableList().also { it[target] = option }
                            available = newAvailable
                            activeBlank = answers.indexOfFirst { it == null }.takeIf { it >= 0 } ?: target
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Foreground,
                )
            }
        }
        AnswerFeedback(answered, isCorrect, success = "Пропуски заполнены верно!", failure = "Правильный порядок: ${correctFill.joinToString(", ")}")
        if (!answered) {
            PrimaryButton("ПРОВЕРИТЬ", onClick = {
                isCorrect = answers.mapIndexed { i, a -> a == correctFill[i] }.all { it }
                answered = true
            }, enabled = answers.none { it == null })
        } else {
            SubmitAnswerEffect(answered) { onAnswer(isCorrect) }
        }
    }
}

@Composable
private fun CodeEditor(value: String, answered: Boolean, isCorrect: Boolean, onValueChange: (String) -> Unit) {
    val borderColor = when {
        answered && isCorrect -> Success
        answered -> Destructive
        else -> Border
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = !answered,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardElevated)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Foreground,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
        ),
        cursorBrush = SolidColor(Primary),
    )
}

@Composable
private fun AnswerFeedback(answered: Boolean, isCorrect: Boolean, success: String, failure: String) {
    if (answered) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(if (isCorrect) Icons.Default.Check else Icons.Default.Close, contentDescription = null, tint = if (isCorrect) Success else Destructive)
            Text(if (isCorrect) success else failure, color = if (isCorrect) Success else Destructive, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun TaskContainer(
    question: String,
    header: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (header != null) header() else {
            Text(question, color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }
        content()
    }
}
