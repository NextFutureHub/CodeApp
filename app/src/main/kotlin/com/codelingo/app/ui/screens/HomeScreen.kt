package com.codelingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.model.Course
import com.codelingo.app.data.model.GameState
import com.codelingo.app.ui.components.ProgressBar
import com.codelingo.app.ui.components.StatsBar
import com.codelingo.app.ui.components.XpProgressBar
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.CardElevated
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.Streak
import com.codelingo.app.ui.theme.Xp
import com.codelingo.app.ui.theme.parseHslColor

@Composable
fun HomeScreen(
    state: GameState,
    courseRepository: CourseRepository,
    onCourseClick: (String) -> Unit,
) {
    val courses = courseRepository.getCourses()
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(bottom = 80.dp),
    ) {
        StatsBar(state)

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Primary.copy(alpha = 0.2f), Card, CardElevated),
                    ),
                )
                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(24.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Primary)
                    }
                    Column {
                        Text("УРОВЕНЬ ${state.level}", color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Привет, ${state.userName}!", color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                XpProgressBar(state.xp, state.level)
                if (state.streak > 0) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Streak.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Streak)
                        Text("${state.streak} дней подряд! 🔥", color = Streak, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }

        Text(
            "Продолжить обучение",
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp),
            color = Foreground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
        )

        courses.forEach { course ->
            CourseListItem(course, state, onClick = { onCourseClick(course.id) })
        }

        Text(
            "Статистика",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp),
            color = Foreground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard("Уроков", state.completedLessons.size.toString(), Icons.Default.Book, Primary, Modifier.weight(1f))
            StatCard("XP", state.xp.toString(), Icons.Default.Bolt, Xp, Modifier.weight(1f))
            StatCard("Streak", state.streak.toString(), Icons.Default.LocalFireDepartment, Streak, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CourseListItem(course: Course, state: GameState, onClick: () -> Unit) {
    val lessons = course.levels.flatMap { it.lessons }
    val completed = lessons.count { it.id in state.completedLessons }
    val progress = if (lessons.isNotEmpty()) completed.toFloat() / lessons.size else 0f
    val courseColor = parseHslColor(course.color)

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(courseColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(course.icon, fontSize = 28.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(course.title, color = Foreground, fontWeight = FontWeight.ExtraBold)
            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProgressBar(progress, courseColor, modifier = Modifier.weight(1f))
                Text("${(progress * 100).toInt()}%", color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MutedForeground)
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Text(value, color = Foreground, fontWeight = FontWeight.Black, fontSize = 20.sp, modifier = Modifier.padding(top = 4.dp))
        Text(label, color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}
