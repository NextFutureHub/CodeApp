package com.codelingo.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codelingo.app.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "codelingo_settings")

class SettingsRepository(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme-mode")

    val themeMode: Flow<ThemeMode> = context.settingsStore.data.map { prefs ->
        when (prefs[themeKey]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            else -> ThemeMode.DARK
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsStore.edit { prefs ->
            prefs[themeKey] = mode.name
        }
    }
}
