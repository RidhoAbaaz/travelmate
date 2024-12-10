package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.travelmate.databinding.ActivityMainBinding
import com.example.travelmate.notification.NotificationPermission
import com.example.travelmate.ui.login.LoginActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        NotificationPermission.handlePermission(this, isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase (jika diperlukan)
        FirebaseApp.initializeApp(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Konfigurasi AppBar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_progress,
                R.id.navigation_notifications,
                R.id.navigation_favorite
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration) // Aktifkan kembali jika ingin toolbar sync

        // Setup BottomNavigationView dengan NavController
        navView.setupWithNavController(navController)

        NotificationPermission.isPermitted(this, requestPermissionLauncher)
    }
}
