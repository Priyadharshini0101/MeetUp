package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class About : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)

        database = FirebaseDatabase.getInstance()

        findViewById<Button>(R.id.aboutnext).setOnClickListener {
            val about = findViewById<EditText>(R.id.about).text.toString()
            val uid = FirebaseAuth.getInstance().uid
            val ref1 = FirebaseDatabase.getInstance().getReference("/Users/$uid")

            ref1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    if (user != null) {
                        val fieldname = user.uid
                        val ref2 = database.getReference()
                        Toast.makeText(this@About, "Welcome " + user.name + "!", Toast.LENGTH_LONG).show()
                        ref2.child("Users/$fieldname/about").setValue(about)
                        val intent = Intent(this@About, home::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}

