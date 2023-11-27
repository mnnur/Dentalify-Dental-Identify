package com.ps108.dentify.ui.diagnosis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ps108.dentify.databinding.FragmentDiagnosisBinding

class DiagnosisFragment : Fragment() {
    private lateinit var binding : FragmentDiagnosisBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiagnosisBinding.inflate(inflater,container, false)

        return binding.root
    }
}