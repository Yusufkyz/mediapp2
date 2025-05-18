package com.example.mediapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 2 saniye bekledikten sonra login ekranına geçiş
        Handler().postDelayed({
            // Eğer kullanıcı daha önce giriş yaptıysa MainActivity'e geç, yoksa LoginActivity'e geç.
            val sharedPref = getSharedPreferences("MeditationAppPrefs", MODE_PRIVATE)
            val isRegistered = sharedPref.getBoolean("isRegistered", false)

            if (isRegistered) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            finish()
        }, 2000) // 2000 milisaniye = 2 saniye
    }
}
