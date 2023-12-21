package com.ps108.dentify.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.firestore.auth.User
import com.ps108.dentify.data.Diagnosis
import com.ps108.dentify.databinding.ActivityDetailBinding
import com.ps108.dentify.utils.descriptionSelector
import com.ps108.dentify.utils.titleSelector

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

        val disease = diagnosis?.diagnosis
        binding.tvDetailDate.text = diagnosis?.date
        binding.tvTitle.text = titleSelector(disease)
        Glide.with(this).load(diagnosis?.imageUrl).into(binding.ivImageDetail)
        if(disease != null) {
            binding.tvDescription.text = descriptionSelector(disease)
        }

    }
    companion object {
        const val DIAGNOSIS_RESULT = "diagnosis_result"
    }
}