package com.example.mediapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPref = getSharedPreferences("MeditationAppPrefs", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        val emailField = findViewById<EditText>(R.id.etEmail)
        val usernameField = findViewById<EditText>(R.id.etUsername)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = emailField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && username.isNotEmpty() && password.length >= 4) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // SharedPreferences'e kayıt
                            sharedPref.edit().apply {
                                putString("email", email)
                                putString("username", username)
                                putString("password", password)
                                putBoolean("isRegistered", true)
                                apply()
                            }

                            // Firebase'e kullanıcıyı yaz
                            val currentUserID = auth.currentUser?.uid
                            currentUserID?.let {
                                val userMap = HashMap<String, Any>()
                                userMap["username"] = username
                                userMap["email"] = email

                                dbRef.child("users").child(it).setValue(userMap)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        } else {
                                            Toast.makeText(this, "Veritabanına yazarken hata oluştu.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Kayıt başarısız: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Lütfen geçerli e-posta, kullanıcı adı ve şifre girin.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
