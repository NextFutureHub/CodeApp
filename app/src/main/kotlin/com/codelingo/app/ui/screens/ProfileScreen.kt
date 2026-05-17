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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.model.GameState
import com.codelingo.app.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.TextButton
import com.codelingo.app.ui.components.ProgressBar
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.PrimaryForeground
import com.codelingo.app.ui.theme.PrimaryGlow
import com.codelingo.app.ui.theme.Streak
import com.codelingo.app.ui.theme.Warning
import com.codelingo.app.ui.theme.Xp
import com.codelingo.app.ui.theme.parseHslColor

@Composable
fun ProfileScreen(
    state: GameState,
    courseRepository: CourseRepository,
    authViewModel: AuthViewModel,
    onOpenSettings: () -> Unit,
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val totalLessons = courseRepository.totalLessonCount()
    val courses = courseRepository.getCourses()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Профиль",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                color = Foreground,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
            )
            IconButton(onClick = onOpenSettings) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Настройки",
                    tint = Primary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Primary, PrimaryGlow))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryForeground, modifier = Modifier.size(40.dp))
        }
        Text(state.userName, color = Foreground, fontWeight = FontWeight.Black, fontSize = 24.sp, modifier = Modifier.padding(top = 16.dp))
        Text("Уровень ${state.level}", color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        currentUser?.email?.let { email ->
            Text(email, color = MutedForeground, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
        if (authViewModel.isConfigured && currentUser != null) {
            TextButton(onClick = { authViewModel.signOut() }) {
                Text("Выйти", color = Primary, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProfileStat(Icons.Default.Bolt, "Всего XP", state.xp.toString(), Xp, Modifier.weight(1f))
            ProfileStat(Icons.Default.LocalFireDepartment, "Streak", "${state.streak} дн.", Streak, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProfileStat(Icons.Default.Book, "Уроков", "${state.completedLessons.size}/$totalLessons", Primary, Modifier.weight(1f))
            ProfileStat(Icons.Default.Star, "Достижения", state.achievements.size.toString(), Warning, Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(16.dp))
                .clickable(onClick = onOpenSettings)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
            Text("Настройки", color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MutedForeground)
        }

        Text(
            "Мои курсы",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 12.dp),
            color = Foreground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
        )

        courses.forEach { course ->
            val lessons = course.levels.flatMap { it.lessons }
            val completed = lessons.count { it.id in state.completedLessons }
            val progress = if (lessons.isNotEmpty()) completed.toFloat() / lessons.size else 0f
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(course.icon, fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(course.title, color = Foreground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    ProgressBar(progress, parseHslColor(course.color), modifier = Modifier.padding(top = 4.dp))
                }
                Text("$completed/${lessons.size}", color = MutedForeground, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProfileStat(icon: ImageVector, label: String, value: String, tint: Color, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Column {
            Text(value, color = Foreground, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(label, color = MutedForeground, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}
