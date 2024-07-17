package com.yanfiq.streamfusion.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jamal.composeprefs3.ui.LocalPrefsDataStore
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.EditTextPref
import com.jamal.composeprefs3.ui.prefs.SliderPref
import com.jamal.composeprefs3.ui.prefs.SwitchPref
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.ui.theme.AppTheme
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(dataStore: DataStore<Preferences> = LocalContext.current.dataStore) {
    val scope = rememberCoroutineScope()

    val isDark by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }).collectAsState(initial = false)

    val maxResult by (dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
    }).collectAsState(initial = 10f)

    AppTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
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
            }
        }
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