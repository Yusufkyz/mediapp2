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

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = getSharedPreferences("MeditationAppPrefs", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        val emailField = findViewById<EditText>(R.id.etEmailLogin)
        val usernameField = findViewById<EditText>(R.id.etUsernameLogin)
        val passwordField = findViewById<EditText>(R.id.etPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString()

            val savedUsername = sharedPref.getString("username", null)
            val savedEmail = sharedPref.getString("email", null)

            if (email.isNotEmpty() && username.isNotEmpty() && password.length >= 4) {
                if (username == savedUsername && email == savedEmail) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val currentUserID = auth.currentUser?.uid
                                currentUserID?.let {
                                    val userMap = HashMap<String, Any>()
                                    userMap["username"] = username
                                    userMap["email"] = email

                                    dbRef.child("users").child(it).setValue(userMap)
                                }

                                Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Kayıtlı kullanıcı adı/e-posta ile eşleşmiyor.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen geçerli e-posta, kullanıcı adı ve şifre girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
