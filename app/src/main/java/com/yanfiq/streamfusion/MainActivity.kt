package com.yanfiq.streamfusion

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yanfiq.streamfusion.ui.theme.AppTheme

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MainScreen(this@MainActivity)
        }
//        SpotifyApi.initialize(this)

//        val themePref = ThemeUtils.getThemePreference(this)
//        ThemeUtils.applyTheme(themePref)
    }

    @Composable
    fun MainScreen(context: Context) {
        AppTheme {
            Column {
                BottomNavigationBar(context)
            }
        }
    }
}