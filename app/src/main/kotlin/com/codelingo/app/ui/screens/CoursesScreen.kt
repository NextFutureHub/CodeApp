package com.codelingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.model.GameState
import com.codelingo.app.ui.components.ProgressBar
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.parseHslColor

@Composable
fun CoursesScreen(
    state: GameState,
    courseRepository: CourseRepository,
    onCourseClick: (String) -> Unit,
) {
    val courses = courseRepository.getCourses()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 80.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Курсы", color = Foreground, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Text("Выбери свой путь", color = MutedForeground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        courses.forEach { course ->
            val lessons = course.levels.flatMap { it.lessons }
            val completed = lessons.count { it.id in state.completedLessons }
            val progress = if (lessons.isNotEmpty()) completed.toFloat() / lessons.size else 0f
            val courseColor = parseHslColor(course.color)

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Card)
                    .border(2.dp, Border, RoundedCornerShape(24.dp))
                    .clickable { onCourseClick(course.id) }
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(courseColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(course.icon, fontSize = 32.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(course.title, color = Foreground, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text(
                            "${lessons.size} уроков · ${course.levels.size} уровень",
                            color = MutedForeground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MutedForeground)
                }
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProgressBar(progress, courseColor, modifier = Modifier.weight(1f), height = 12.dp)
                    Text("${(progress * 100).toInt()}%", color = courseColor, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        }
    }
}
