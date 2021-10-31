package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.signuppage)
    supportActionBar!!.hide()

    findViewById<Button>(R.id.signup).setOnClickListener {
        performSignup()
    }

    findViewById<TextView>(R.id.loginpage2).setOnClickListener {
        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish()
    }
}

    private fun performSignup(){
        val username=findViewById<EditText>(R.id.username).text.toString()
        val email=findViewById<EditText>(R.id.email).text.toString()
        val password=findViewById<EditText>(R.id.password).text.toString()

        if(email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this,"Please enter you email address or password",Toast.LENGTH_LONG).show()
            return
        }

        Log.d("SignUp","Email and Password: "+email+"$password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(this,"Welcome "+username+"!",Toast.LENGTH_LONG).show()
                Log.d("SignUp","${it.result?.user?.uid}")
                savetoFirebaseatabase(username, email)
                val intent= Intent(this,Interested::class.java)
                startActivity(intent)
                finish()
            }

            .addOnFailureListener{
                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
            }
    }

    private fun savetoFirebaseatabase(username:String,email:String) {
        val uid=FirebaseAuth.getInstance().uid?: ""
        val ref= FirebaseDatabase.getInstance().getReference("/Users/$uid")
        val user=User(uid,username,email)
        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("SignUp","Finally we saved the user to Firebase Database")
            }
      }

    @Override
    override fun onBackPressed() {
        val intent =Intent(this@signup,welcome::class.java)
        startActivity(intent)
        finish()
    }
}

