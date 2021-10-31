package com.example.meetup

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class Interested : AppCompatActivity() {

    var next: Boolean? = false
    var i = 0
    private lateinit var database: FirebaseDatabase
    var interests: ArrayList<String>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interested)

        database = FirebaseDatabase.getInstance()

        findViewById<Button>(R.id.sports).setOnClickListener {
            findViewById<Button>(R.id.sports).setBackgroundColor(resources.getColor(R.color.DuskyWhite))
            interests!!.add("Sports")
            interest()
        }
        findViewById<Button>(R.id.technology).setOnClickListener {
            findViewById<Button>(R.id.technology).setBackgroundColor(resources.getColor(R.color.DuskyWhite))
            interests!!.add("Technology")
            interest()
        }
        findViewById<Button>(R.id.music).setOnClickListener {
            findViewById<Button>(R.id.music).setBackgroundColor(resources.getColor(R.color.DuskyWhite))
            interests!!.add("Music")
            interest()
        }

        findViewById<Button>(R.id.arts).setOnClickListener {
            findViewById<Button>(R.id.arts).setBackgroundColor(resources.getColor(R.color.DuskyWhite))
            interests!!.add("Arts")
            interest()
        }
        findViewById<Button>(R.id.travel).setOnClickListener {
            findViewById<Button>(R.id.travel).setBackgroundColor(resources.getColor(R.color.DuskyWhite))
            interests!!.add("Traveling")
            interest()
        }

        findViewById<Button>(R.id.next).setOnClickListener {
            nextpage()
        }
    }

    private fun interest() {
        i++
        if (i == 3) {
            findViewById<Button>(R.id.sports).isClickable = false
            findViewById<Button>(R.id.technology).isClickable = false
            findViewById<Button>(R.id.music).isClickable = false
            findViewById<Button>(R.id.arts).isClickable = false
            findViewById<Button>(R.id.travel).isClickable = false
        }

    }

    private fun nextpage() {
        val uid = FirebaseAuth.getInstance().uid
        val ref1 = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    val fieldname = user.uid
                    val ref2 = database.getReference()
                    ref2.child("Users/$fieldname/interested").setValue(interests)
                    val intent = Intent(this@Interested, About::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
