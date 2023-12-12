package com.ps108.dentify.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.ps108.dentify.R
import com.ps108.dentify.ui.settings.language.LanguageSettingsFragment
import com.ps108.dentify.ui.settings.theme.ThemeSettingsFragment

class SettingsPreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_settings)
        val preferencesFragment : Fragment? = when (intent.getStringExtra(EXTRA_PREFERENCES_KEY)) {
            "THEME" -> {
                ThemeSettingsFragment()
            }
            "LANGUAGE" -> {
                LanguageSettingsFragment()
            }
            else -> {
                null
            }
        }

        if (savedInstanceState == null && preferencesFragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, preferencesFragment)
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Theme"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_PREFERENCES_KEY = "extra_preference_key"
    }

}