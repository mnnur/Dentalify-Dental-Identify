package com.ps108.dentify.ui.settings.theme

import android.os.Bundle
import com.ps108.dentify.R
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat

class ThemeSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.theme_preferences, rootKey)

        val darkModePreferenceFollowSystem = findPreference<CheckBoxPreference>("pref_dark_follow_system")
        val darkModePreferenceOff = findPreference<CheckBoxPreference>("pref_dark_off")
        val darkModePreferenceOn = findPreference<CheckBoxPreference>("pref_dark_on")

        if(darkModePreferenceFollowSystem != null && darkModePreferenceOff != null && darkModePreferenceOn != null){
            setCheckBoxChangeListener(darkModePreferenceFollowSystem, darkModePreferenceOff, darkModePreferenceOn)
        }

    }

    private fun setCheckBoxChangeListener(
        followSystem: CheckBoxPreference,
        lightMode: CheckBoxPreference,
        darkMode: CheckBoxPreference
    ) {
        val preferences = listOf(followSystem, lightMode, darkMode)

        preferences.forEach { preference ->
            preference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    updateTheme(preference.key)
                    preferences.forEach { otherPreference ->
                        if (otherPreference != preference) {
                            otherPreference.isChecked = false
                        }
                    }
                }
                true
            }
        }
    }

    private fun updateTheme(preferenceKey: String) {
        val nightMode = when (preferenceKey) {
            "pref_dark_follow_system" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "pref_dark_off" -> AppCompatDelegate.MODE_NIGHT_NO
            "pref_dark_on" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
    }
}