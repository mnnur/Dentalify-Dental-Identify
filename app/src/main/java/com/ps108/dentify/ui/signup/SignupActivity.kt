package com.ps108.dentify.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ps108.dentify.R
import com.ps108.dentify.databinding.ActivitySignupBinding
import com.ps108.dentify.ui.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivitySignupBinding.inflate(layoutInflater)
         setContentView(binding.root)

        binding.btnSignUp.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        setupView()
        setupAction()
        playAnimation()

    }

    private fun setupAction(){
        binding.cbTos.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                showTos()
            }
            else{
                binding.btnSignUp.isEnabled = false
            }
        }
    }

    private fun showTos() {
        val dialogBuilder = AlertDialog.Builder(this)

        val view = layoutInflater.inflate(R.layout.popup_tos, null)
        dialogBuilder.setView(view)


        val dialog = dialogBuilder.create()
        val btnAgree = view.findViewById<Button>(R.id.btn_agree_popup)

        view.findViewById<CheckBox>(R.id.cb_tos_popup).setOnCheckedChangeListener{_, isChecked ->
            btnAgree.isEnabled = isChecked
        }

        btnAgree.setOnClickListener {
            binding.btnSignUp.isEnabled = true
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.btn_disagree_popup).setOnClickListener {
            binding.btnSignUp.isEnabled = false
            binding.cbTos.isChecked = false
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
                ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
                ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
                ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
                ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
                ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
                ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}