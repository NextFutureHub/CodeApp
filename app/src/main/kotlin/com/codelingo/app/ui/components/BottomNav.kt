package com.codelingo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.navigation.Routes
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary

private data class NavItem(val route: String, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Routes.HOME, Icons.Default.Home, "Главная"),
    NavItem(Routes.COURSES, Icons.Default.Book, "Курсы"),
    NavItem(Routes.ACHIEVEMENTS, Icons.Default.EmojiEvents, "Награды"),
    NavItem(Routes.PROFILE, Icons.Default.Person, "Профиль"),
)

@Composable
fun CodeLingoBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Card)
            .padding(horizontal = 8.dp)
            .height(64.dp)
            .widthIn(max = 448.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navItems.forEach { item ->
            val isActive = currentRoute == item.route
            Column(
                modifier = Modifier
                    .clickable { onNavigate(item.route) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (isActive) Primary else MutedForeground,
                )
                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Primary else MutedForeground,
                )
            }
        }
    }
}

fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return true
    return !route.startsWith("lesson")
}
