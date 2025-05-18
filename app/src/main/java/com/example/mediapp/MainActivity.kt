package com.example.mediapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mediapp.API.RetrofitInstance
import com.example.mediapp.API.Quote
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var tvWelcome: TextView
    private lateinit var quoteTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var btnLogout: Button
    private lateinit var btnSettings: Button
    private lateinit var btnNotificationTest: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("MeditationAppPrefs", MODE_PRIVATE)

        tvWelcome = findViewById(R.id.tvWelcome)
        quoteTextView = findViewById(R.id.quoteTextView)
        authorTextView = findViewById(R.id.authorTextView)
        refreshButton = findViewById(R.id.refreshButton)
        btnLogout = findViewById(R.id.btnLogout)
        btnSettings = findViewById(R.id.btnSettings)
        btnNotificationTest = findViewById(R.id.btnNotificationTest)

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnNotificationTest.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }


        val username = sharedPref.getString("username", "Kullanıcı")
        tvWelcome.text = "Hoş geldin, $username!"

        getQuote()

        refreshButton.setOnClickListener {
            getQuote()
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().putBoolean("isRegistered", false).apply()
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()

            val intent = Intent(this, MusicPlayerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getQuote() {
        val call = RetrofitInstance.api.getRandomQuote()

        call.enqueue(object : Callback<List<Quote>> {
            override fun onResponse(call: Call<List<Quote>>, response: Response<List<Quote>>) {
                if (response.isSuccessful) {
                    val quote = response.body()?.firstOrNull()
                    quote?.let {
                        quoteTextView.text = "\"${it.q}\""
                        authorTextView.text = "- ${it.a}"
                    }
                } else {
                    quoteTextView.text = "Bir şeyler ters gitti..."
                    authorTextView.text = ""
                }
            }

            override fun onFailure(call: Call<List<Quote>>, t: Throwable) {
                quoteTextView.text = "Hata: ${t.localizedMessage}"
                authorTextView.text = ""
            }
        })
    }
}
