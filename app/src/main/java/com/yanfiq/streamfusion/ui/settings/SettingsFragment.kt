package com.yanfiq.streamfusion.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.ui.settings.about.AboutActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedSharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            "encrypted_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        findPreference<EditTextPreference>("youtube_api_key")?.apply {
            summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val storedValue = preference.text
                if (!storedValue.isNullOrEmpty()) {
                    storedValue.mask()
                } else {
                    "No API key set"
                }
            }
            setOnBindEditTextListener {
                it.setText("") // Clear the EditText field when the dialog is shown
            }
            setOnPreferenceChangeListener { _, newValue ->
                encryptedSharedPreferences.edit().putString(key, newValue as String).apply()
                true
            }
        }

        findPreference<EditTextPreference>("spotify_client_id")?.apply {
            summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val storedValue = preference.text
                if (!storedValue.isNullOrEmpty()) {
                    storedValue.mask()
                } else {
                    "No client id set"
                }
            }
            setOnBindEditTextListener {
                it.setText("") // Clear the EditText field when the dialog is shown
            }
            setOnPreferenceChangeListener { _, newValue ->
                encryptedSharedPreferences.edit().putString(key, newValue as String).apply()
                true
            }
        }

        findPreference<EditTextPreference>("spotify_client_secret")?.apply {
            summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val storedValue = preference.text
                if (!storedValue.isNullOrEmpty()) {
                    storedValue.mask()
                } else {
                    "No client secret set"
                }
            }
            setOnBindEditTextListener {
                it.setText("") // Clear the EditText field when the dialog is shown
            }
            setOnPreferenceChangeListener { _, newValue ->
                encryptedSharedPreferences.edit().putString(key, newValue as String).apply()
                true
            }
        }

        findPreference<Preference>("about")?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
            true
        }

        findPreference<SwitchPreference>("switch_dark_mode")?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                if (preference is SwitchPreference) {
                    val isEnabled = newValue as Boolean

                    if (isEnabled) {
                        ThemeUtils.saveThemePreference(requireContext(), "dark")
                    } else {
                        ThemeUtils.saveThemePreference(requireContext(), "light")
                    }
                    ThemeUtils.applyTheme(if (isChecked) "dark" else "light")
                    requireActivity().recreate()
                }
                true
            }
            isChecked = if (ThemeUtils.getThemePreference(requireContext()) == "dark") true else false
        }

        findPreference<EditTextPreference>("result_per_query")?.apply {
            summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val storedValue = preference.text
                if (!storedValue.isNullOrEmpty()) {
                    storedValue.mask()
                } else {
                    "Not set"
                }
            }
            setOnBindEditTextListener {editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { _, newValue ->
                sharedPreferences.edit().putString(key, newValue as String).apply()
                true
            }
        }
    }

    // Masking function to obfuscate sensitive information
    private fun String.mask(
        maskString: String = "*",
        maskAfterLetters: Int = 3,
        isFixSize: Boolean = true,
        maxSize: Int = 10,
        selector: String = "."
    ): String {
        if(maskAfterLetters < 0) throw RuntimeException("Invalid masking configuration - maskAfterLetters should be greater than 0")
        if(isFixSize && maxSize <= maskAfterLetters) throw RuntimeException("Invalid masking configuration - maxSize must be greater than maskAfterLetters")
        val text = if(isFixSize && length >= maxSize) substring(0, maxSize) else this
        val unmaskLength = if(maskAfterLetters <= length) maskAfterLetters else length
        return text.substring(0, unmaskLength) + text.substring(unmaskLength, text.length).replace(selector.toRegex(), maskString)
    }
}
