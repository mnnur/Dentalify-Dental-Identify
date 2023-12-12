package com.ps108.dentify.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.ps108.dentify.ui.login.LoginActivity
import com.ps108.dentify.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Glide.with(requireActivity()).load(user.photoUrl).into(binding.ivUserImage)
            binding.tvNameProfile.text = user.displayName
            binding.tvEmailProfile.text = user.email
        }

        binding.btnLanguage.setOnClickListener{
            /*
            val themePreferences = Intent(requireActivity(), SettingsPreferencesActivity::class.java)
            themePreferences.putExtra(SettingsPreferencesActivity.EXTRA_PREFERENCES_KEY, "LANGUAGE")
            requireActivity().startActivity(themePreferences)
            */
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.btnTheme.setOnClickListener {
            val themePreferences = Intent(requireActivity(), SettingsPreferencesActivity::class.java)
            themePreferences.putExtra(SettingsPreferencesActivity.EXTRA_PREFERENCES_KEY, "THEME")
            requireActivity().startActivity(themePreferences)
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }

        return binding.root
    }

}