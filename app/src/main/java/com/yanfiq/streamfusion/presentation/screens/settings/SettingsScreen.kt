package com.yanfiq.streamfusion.presentation.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.EditTextPref
import com.jamal.composeprefs3.ui.prefs.SliderPref
import com.jamal.composeprefs3.ui.prefs.SwitchPref
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(dataStore: DataStore<Preferences> = LocalContext.current.dataStore) {
    val isDark by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }).collectAsState(initial = false)

    val maxResult by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
    }).collectAsState(initial = 10f)

    val youtubeApiKey by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.YOUTUBE_API_KEY] ?: ""
    }).collectAsState(initial = "")

    val spotifyClientId by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SPOTIFY_CLIENT_ID] ?: ""
    }).collectAsState(initial = "")

    val spotifyClientSecret by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SPOTIFY_CLIENT_SECRET] ?: ""
    }).collectAsState(initial = "")

    AppTheme {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            content = {padding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PrefsScreen(dataStore) {
                        prefsGroup("General") {
                            prefsItem {
                                SwitchPref(key = "switch_dark_mode",
                                    title = "Dark mode",
                                    defaultChecked = isDark
                                )
                            }
                            prefsItem {
                                SliderPref(
                                    key = "result_per_search",
                                    title = "Result per search",
                                    valueRange = 10f..100f,
                                    steps = 8,
                                    defaultValue = maxResult,
                                    showValue = true
                                )
                            }
                        }
                        prefsGroup("Advanced") {
                            prefsItem {
                                EditTextPref(
                                    key = "youtube_api_key",
                                    title = "YouTube API key",
                                    dialogTitle = "Youtube API key",
                                    dialogMessage = "Enter your API key",
                                    summary = youtubeApiKey
                                )
                            }
                            prefsItem {
                                EditTextPref(
                                    key = "spotify_client_id",
                                    title = "Spotify client ID",
                                    dialogTitle = "Spotify client ID",
                                    dialogMessage = "Enter your client ID",
                                    summary = spotifyClientId
                                )
                            }
                            prefsItem {
                                EditTextPref(
                                    key = "spotify_client_secret",
                                    title = "Spotify client secret",
                                    dialogTitle = "Spotify client secret",
                                    dialogMessage = "Enter your client secret",
                                    summary = spotifyClientSecret
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

object PreferencesKeys {
    val DARK_MODE = booleanPreferencesKey("switch_dark_mode")
    val RESULT_PER_SEARCH = floatPreferencesKey("result_per_search")
    val YOUTUBE_API_KEY = stringPreferencesKey("youtube_api_key")
    val SPOTIFY_CLIENT_ID = stringPreferencesKey("spotify_client_id")
    val SPOTIFY_CLIENT_SECRET = stringPreferencesKey("spotify_client_secret")
}

@Composable
@Preview(showBackground = true)
private fun SettingsPreview(){
    SettingsScreen()
}