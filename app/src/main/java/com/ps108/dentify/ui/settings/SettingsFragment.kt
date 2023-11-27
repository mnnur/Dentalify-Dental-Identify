package com.ps108.dentify.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ps108.dentify.WelcomeActivity
import com.ps108.dentify.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater,container, false)

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
        }

        return binding.root
    }

}