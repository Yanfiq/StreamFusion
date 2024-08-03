package com.yanfiq.streamfusion

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yanfiq.streamfusion.presentation.screens.BottomNavigationBar
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import com.yanfiq.streamfusion.utils.getHiddenMessage
import com.yanfiq.streamfusion.utils.loadImageFromAssets
import com.yanfiq.streamfusion.utils.loadImageFromDrawable
import com.yanfiq.streamfusion.utils.loadImageFromRaw

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MainScreen(this@MainActivity)
        }
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