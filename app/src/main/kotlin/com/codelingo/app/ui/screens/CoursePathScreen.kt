package com.codelingo.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.model.Course
import com.codelingo.app.data.model.GameState
import com.codelingo.app.data.model.Lesson
import com.codelingo.app.ui.theme.Background
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.Muted
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.PrimaryForeground
import com.codelingo.app.ui.theme.parseHslColor

private val NodeSize = 68.dp
private val RowHeight = 120.dp
private val Amplitude = 70.dp
private val ContainerWidth = 320.dp

private fun getNodeXOffset(index: Int): androidx.compose.ui.unit.Dp {
    val centerX = ContainerWidth / 2
    val positions = listOf(0.dp, Amplitude, 0.dp, (-Amplitude))
    return centerX + positions[index % 4] - NodeSize / 2
}

@Composable
fun CoursePathScreen(
    courseId: String,
    state: GameState,
    courseRepository: CourseRepository,
    onBack: () -> Unit,
    onLessonClick: (String, String) -> Unit,
) {
    val course = courseRepository.getCourse(courseId)
    if (course == null) {
        Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Курс не найден", color = Foreground)
        }
        return
    }

    val allLessons = courseRepository.getAllLessons(course)
    val courseColor = parseHslColor(course.color)
    val svgHeight = RowHeight * allLessons.size + 60.dp
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Foreground)
            }
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(courseColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(course.icon, fontSize = 20.sp)
            }
            Text(
                course.title,
                color = Foreground,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .width(ContainerWidth)
                    .height(svgHeight),
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    allLessons.forEachIndexed { i, _ ->
                        if (i == 0) return@forEachIndexed
                        val x1 = with(density) { getNodeXOffset(i - 1).toPx() + NodeSize.toPx() / 2 }
                        val y1 = with(density) { (RowHeight * (i - 1) + 40.dp + NodeSize / 2).toPx() }
                        val x2 = with(density) { getNodeXOffset(i).toPx() + NodeSize.toPx() / 2 }
                        val y2 = with(density) { (RowHeight * i + 40.dp).toPx() }
                        val prevCompleted = state.completedLessons.contains(allLessons[i - 1].id)
                        val isActive = prevCompleted
                        val midY = (y1 + y2) / 2f
                        val path = Path().apply {
                            moveTo(x1, y1)
                            cubicTo(x1, midY, x2, midY, x2, y2)
                        }
                        drawPath(
                            path = path,
                            color = if (isActive) courseColor.copy(alpha = 0.7f) else Border.copy(alpha = 0.4f),
                            style = Stroke(
                                width = 4.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = if (isActive) null else PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 8.dp.toPx())),
                            ),
                        )
                    }
                }

                allLessons.forEachIndexed { i, lesson ->
                    LessonNode(
                        lesson = lesson,
                        index = i,
                        state = state,
                        allLessons = allLessons,
                        courseColor = courseColor,
                        onClick = { onLessonClick(course.id, lesson.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonNode(
    lesson: Lesson,
    index: Int,
    state: GameState,
    allLessons: List<Lesson>,
    courseColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    val isCompleted = lesson.id in state.completedLessons
    val prevCompleted = index == 0 || allLessons[index - 1].id in state.completedLessons
    val isLocked = index > 0 && !prevCompleted
    val isCurrent = !isCompleted && prevCompleted

    Column(
        modifier = Modifier
            .offset(x = getNodeXOffset(index), y = RowHeight * index + 40.dp)
            .width(NodeSize),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(NodeSize)
                .alpha(if (isLocked) 0.4f else 1f)
                .clip(CircleShape)
                .background(if (isLocked) Muted else courseColor)
                .clickable(enabled = !isLocked, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isCompleted -> Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryForeground, modifier = Modifier.size(28.dp))
                isLocked -> Icon(Icons.Default.Lock, contentDescription = null, tint = MutedForeground, modifier = Modifier.size(22.dp))
                else -> Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryForeground, modifier = Modifier.size(24.dp))
            }
        }
        if (isCurrent) {
            Text(
                "НАЧАТЬ",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Primary)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = PrimaryForeground,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
            )
        }
        Text(
            lesson.title,
            modifier = Modifier.padding(top = if (isCurrent) 4.dp else 8.dp),
            color = if (isLocked) MutedForeground else Foreground,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
        )
    }
}
