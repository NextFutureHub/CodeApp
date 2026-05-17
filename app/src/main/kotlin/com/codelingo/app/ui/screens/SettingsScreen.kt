package com.codelingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.viewmodel.SettingsViewModel
import com.codelingo.app.ui.theme.Background
import com.codelingo.app.ui.theme.Border
import com.codelingo.app.ui.theme.Card
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary
import com.codelingo.app.ui.theme.PrimaryForeground

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Foreground)
            }
            Icon(Icons.Default.Settings, contentDescription = null, tint = Primary, modifier = Modifier.padding(end = 8.dp))
            Text("Настройки", color = Foreground, fontWeight = FontWeight.Black, fontSize = 22.sp)
        }

        Text(
            "Оформление",
            modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 12.dp),
            color = MutedForeground,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = Primary,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Тёмная тема", color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(
                    if (isDarkTheme) "Классический тёмный интерфейс CodeLingo" else "Светлый интерфейс для дневного обучения",
                    color = MutedForeground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { settingsViewModel.setDarkTheme(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryForeground,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = PrimaryForeground,
                    uncheckedTrackColor = MutedForeground,
                ),
            )
        }

        Text(
            "Выключите переключатель, чтобы включить светлую тему.",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = MutedForeground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}
