package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.meetup.signup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.signuppage.*

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginpage)

        supportActionBar!!.hide()

        findViewById<Button>(R.id.login).setOnClickListener {
            performLogin()
        }

        findViewById<TextView>(R.id.signpage1).setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performLogin() {
        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter you email address or password", Toast.LENGTH_LONG)
                .show()
            return
        }

        Log.d("Login", "Email and Password: " + email + "$password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show()
                Log.d("Login", "${it.result?.user?.uid}")
                val intent = Intent(this, home::class.java)
                startActivity(intent)
                finish()
            }

            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    @Override
    override fun onBackPressed() {
        val intent = Intent(this@login, welcome::class.java)
        startActivity(intent)
        finish()
    }
}