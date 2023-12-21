package com.ps108.dentify.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.ps108.dentify.WelcomeActivity
import com.ps108.dentify.data.Profile
import com.ps108.dentify.databinding.FragmentSettingsBinding
import com.ps108.dentify.ui.detail.DetailActivity
import com.ps108.dentify.ui.settings.profile.ProfileActivity


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var user : FirebaseUser
    private var profile : Profile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        user = FirebaseAuth.getInstance().currentUser!!

        val providers: List<UserInfo> = user.providerData
        for (userInfo in providers) {
            when (userInfo.providerId) {
                GoogleAuthProvider.PROVIDER_ID -> {
                    Glide.with(requireActivity()).load(user.photoUrl).into(binding.ivUserImage)
                    binding.tvNameProfile.text = user.displayName
                    binding.tvEmailProfile.text = user.email
                    binding.btnEditProfile.setOnClickListener {
                        Toast.makeText(requireActivity(), "Edit profile tidak didukung dengan akun google", Toast.LENGTH_SHORT).show()
                    }
                }
                EmailAuthProvider.PROVIDER_ID -> {
                    getInfoFromDB()
                    binding.btnEditProfile.setOnClickListener {
                        val profileIntent = Intent(requireActivity(), ProfileActivity::class.java)
                        profileIntent.putExtra("PROFILE", profile)
                        requireActivity().startActivity(profileIntent)
                    }
                }
                else -> {
                    // Handle other providers
                }
            }
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
            updateUI()
        }

        return binding.root
    }

    private fun getInfoFromDB(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("profile").whereEqualTo("strEmail", user.email)

        query.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                // Iterate through the documents
                for (documentSnapshot in queryDocumentSnapshots) {
                    // Access the data in each document
                    val name = documentSnapshot.getString("strName")
                    val email = documentSnapshot.getString("strEmail")
                    val imageUrl = documentSnapshot.getString("strImageUrl")
                    profile = Profile(
                        documentSnapshot.id,
                        name ?: "",
                        email ?: "",
                        imageUrl ?: ""
                    )
                    if(imageUrl != "") {
                        Glide.with(requireActivity()).load(imageUrl).into(binding.ivUserImage)
                    }
                    binding.tvNameProfile.text = name
                    binding.tvEmailProfile.text = email
                    Log.d("DB", "Name: $name, Email: $email")
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e("DB", "Error getting documents: ", e)
            }
    }

    private fun updateUI() {
        val welcomeIntent = Intent(requireActivity(), WelcomeActivity::class.java)
        welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(welcomeIntent)
        requireActivity().finish()
    }

}