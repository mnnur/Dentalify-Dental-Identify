package com.ps108.dentify.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.ps108.dentify.ui.login.LoginActivity
import com.ps108.dentify.databinding.FragmentSettingsBinding
import com.ps108.dentify.ui.settings.theme.ThemeSettingsActivity


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

        binding.btnTheme.setOnClickListener {
            startActivity(Intent(requireActivity(), ThemeSettingsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }

        return binding.root
    }

}