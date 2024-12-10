package com.example.travelmate.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.travelmate.R
import com.example.travelmate.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Ambil referensi ImageView
        val arrowIcon = findViewById<ImageView>(R.id.splash_icon)

        // Set OnClickListener untuk tombol panah
        arrowIcon.setOnClickListener {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup SplashActivity agar tidak kembali saat tombol Back ditekan
        }
    }
}
