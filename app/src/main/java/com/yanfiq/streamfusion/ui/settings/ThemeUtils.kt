package com.yanfiq.streamfusion.ui.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    private const val PREFS_NAME = "theme_prefs"
    private const val THEME_KEY = "theme"

    fun applyTheme(themePref: String?) {
        when (themePref) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun saveThemePreference(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(THEME_KEY, theme)
            apply()
        }
    }

    fun getThemePreference(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(THEME_KEY, null)
    }
}
