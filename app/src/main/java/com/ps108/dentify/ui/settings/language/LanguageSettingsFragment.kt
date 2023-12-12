package com.ps108.dentify.ui.settings.language

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.ps108.dentify.R
import java.util.Locale


class LanguageSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.language_preferences, rootKey)

        val languageIndonesia = findPreference<CheckBoxPreference>("pref_indonesia")
        val languageEnglish = findPreference<CheckBoxPreference>("pref_english")

        if(languageIndonesia != null && languageEnglish != null){
            setCheckBoxChangeListener(languageIndonesia, languageEnglish)
        }

    }

    private fun setCheckBoxChangeListener(
        indonesia: CheckBoxPreference,
        english: CheckBoxPreference,
    ) {
        val preferences = listOf(indonesia, english)

        preferences.forEach { preference ->
            preference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    setLocale(preference.key)
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

    private fun setLocale(preferenceKey: String) {
        val language = when (preferenceKey) {
            "pref_indonesia" -> "in"
            "pref_english" -> "en"
            else -> "en"
        }

        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        requireActivity().recreate()
    }
}