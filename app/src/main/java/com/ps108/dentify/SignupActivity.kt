package com.ps108.dentify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.ps108.dentify.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivitySignupBinding.inflate(layoutInflater)
         setContentView(binding.root)

        binding.btnSignUp.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}