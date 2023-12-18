package com.ps108.dentify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ps108.dentify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(setAppTheme())
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }


        val navView: BottomNavigationView = binding.bottomNavigation

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val navController = navHostFragment?.findNavController()

        binding.fabDiagnosis.setOnClickListener {
            val currentDestination = navController?.currentDestination?.id
            if(currentDestination != R.id.navigation_diagnosis){
                navController?.navigateUp()
                navController?.navigate(R.id.navigation_diagnosis)
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_diagnosis, R.id.navigation_settings
            )
        )
        if(navController != null) {
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
        supportActionBar?.hide()

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {

                    val currentDestination = navController?.currentDestination?.id
                    if(currentDestination != R.id.navigation_home) {
                        navController?.navigateUp()
                    }
                    true
                }
                R.id.navigation_settings -> {

                    val currentDestination = navController?.currentDestination?.id
                    if(currentDestination != R.id.navigation_settings) {
                        navController?.navigateUp()
                        navController?.navigate(R.id.navigation_settings)
                    }
                    true
                }

                else -> {
                    true
                }
            }
        }

    }

    private fun setAppTheme(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkFollowSystem = sharedPreferences.getBoolean("pref_dark_follow_system", false)
        val isLightMode = sharedPreferences.getBoolean("pref_dark_off", false)
        val isDarkMode = sharedPreferences.getBoolean("pref_dark_on", false)

        return when {
            isDarkFollowSystem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            isLightMode -> AppCompatDelegate.MODE_NIGHT_NO
            isDarkMode -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}