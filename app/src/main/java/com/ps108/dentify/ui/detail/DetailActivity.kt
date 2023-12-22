package com.ps108.dentify.ui.detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ps108.dentify.data.Diagnosis
import com.ps108.dentify.databinding.ActivityDetailBinding
import com.ps108.dentify.utils.descriptionSelector

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val diagnosis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("DIAGNOSIS_RESULT", Diagnosis::class.java)
        } else {
            intent.getParcelableExtra<Diagnosis>("DIAGNOSIS_RESULT")
        }

        if(diagnosis != null) {
            val disease = diagnosis.diagnosis
            binding.tvDetailDate.text = diagnosis.date
            binding.tvTitle.text = disease
            binding.tvDetailConfidence.text = String.format("Akurasi %.1f%%", diagnosis.confidence * 100)
            Glide.with(this).load(diagnosis.imageUrl).into(binding.ivImageDetail)
            binding.tvDescription.text = descriptionSelector(disease)
        }

    }
    companion object {
        const val DIAGNOSIS_RESULT = "diagnosis_result"
    }
}