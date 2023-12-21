package com.ps108.dentify.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.ps108.dentify.MainActivity
import com.ps108.dentify.R
import com.ps108.dentify.databinding.ActivitySignupBinding
import com.ps108.dentify.ui.login.LoginActivity
import com.ps108.dentify.utils.getCurrentDateTime

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivitySignupBinding.inflate(layoutInflater)
         setContentView(binding.root)

        binding.btnSignUp.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        auth = Firebase.auth
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
        binding.btnSignUp.setOnClickListener {
            createAccount()
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

    private fun storeToDb(name : String, email : String, user: FirebaseUser){
        val db = FirebaseFirestore.getInstance()
        val userProfile = hashMapOf(
            "strName" to name,
            "strEmail" to email,
            "strImageUrl" to ""
        )
        db.collection("profile").add(userProfile)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show()
                Log.d("DB", "DocumentSnapshot added with ID: ${documentReference.id}")
                updateUI(user)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal membuat akun", Toast.LENGTH_SHORT).show()
                Log.w("DB", "Error adding document : ${exception.message}")
            }
    }

    private fun createAccount() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val passwordConfirm = binding.passwordEditTextConfirm.text.toString()

        if(password != passwordConfirm){
            Toast.makeText(
                baseContext,
                "Konfirmasi password salah",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ACC", "createUserWithEmail:success")
                    val user = auth.currentUser
                    storeToDb(name, email, user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ACC", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainIntent)
            finish()
        }
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
        val passwordTextViewConfirm =
            ObjectAnimator.ofFloat(binding.passwordTextViewConfirm, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayoutConfirm =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayoutConfirm, View.ALPHA, 1f).setDuration(100)
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
                passwordTextViewConfirm,
                passwordEditTextLayoutConfirm,
                signup
            )
            startDelay = 100
        }.start()
    }
}