package com.example.meetup

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.profile.*

class home : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        verifyUserLoginDetails()
        Interested()
        About()
        navigate()
      }

    private fun verifyUserLoginDetails() {
        val uid = FirebaseAuth.getInstance().uid


        if (uid == null) {
            val intent = Intent(this, welcome::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun Interested(){
        val uid = FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDataChange(p0: DataSnapshot) {
                var bool = false
                    val user1 = p0.getValue(User::class.java)
                    Log.d("Dhanush1","${user1?.interested}")
                    if (user1?.interested.toString()=="[]") {
                        Log.d("Dhanush1","${user1?.interested}")
                        bool = true

                    }

                if (bool == true) {
                    val intent = Intent(this@home, Interested::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun About(){
        val uid = FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDataChange(p0: DataSnapshot) {
                var bool = false
                val user1 = p0.getValue(User::class.java)
                Log.d("Dhanush1","${user1?.interested}")
                if (user1?.about.toString()=="") {
                    Log.d("Dhanush1","${user1?.interested}")
                    bool = true

                }

                if (bool == true) {
                    val intent = Intent(this@home, About::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private fun navigate(){
    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
    val navController=findNavController(R.id.fragmentContainerView)
    val appBarConfiguration= AppBarConfiguration(setOf(R.id.feeds,R.id.chatting,R.id.profile))
    setupActionBarWithNavController(navController,appBarConfiguration)
    bottomNavigationView.setupWithNavController(navController)
    }
}