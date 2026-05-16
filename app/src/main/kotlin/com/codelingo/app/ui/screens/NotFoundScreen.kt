package com.codelingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelingo.app.ui.theme.Foreground
import com.codelingo.app.ui.theme.Muted
import com.codelingo.app.ui.theme.MutedForeground
import com.codelingo.app.ui.theme.Primary

@Composable
fun NotFoundScreen(onGoHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Muted)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("404", color = Foreground, fontWeight = FontWeight.Bold, fontSize = 36.sp)
        Text("Oops! Page not found", color = MutedForeground, fontSize = 20.sp, modifier = Modifier.padding(vertical = 16.dp))
        TextButton(onClick = onGoHome) {
            Text("Return to Home", color = Primary, fontWeight = FontWeight.SemiBold)
        }
    }
}
