package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class signup : AppCompatActivity() {
    private companion object{
        private const val TAG="signup"
        private const val RC_SIGN_IN=78
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signuppage)

        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("690493792343-529rjos66qcji22mnnm9v3quse1tfgd6.apps.googleusercontent.com")
            .requestEmail()
            .build()

         googleSignInClient = GoogleSignIn.getClient(this, gso)



        findViewById<Button>(R.id.signup).setOnClickListener{
            performSignup()
        }

        findViewById<ImageButton>(R.id.signupwithgoogle).setOnClickListener{
            val signInIntent=googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }

        findViewById<TextView>(R.id.loginpage1).setOnClickListener{
            val intent= Intent(this,login::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        Log.w(TAG,"Signup"+requestCode)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

//    private fun updateUI(currentUser: FirebaseUser?) {
//        if(currentUser==null){
//            Log.w(TAG,"User is null, not going to navigate")
//            return
//        }
//        val intent= Intent(this,home::class.java)
////        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
//
//    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user=auth.currentUser
                    savetoFirebaseatabase(user!!.email.toString(),user!!.displayName.toString(),"")
                    val intent= Intent(this,home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
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
                savetoFirebaseatabase(username, email, password)
                val intent= Intent(this,home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            .addOnFailureListener{
                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
            }
    }

    private fun savetoFirebaseatabase(username:String,email:String,password:String) {
        val uid=FirebaseAuth.getInstance().uid?: ""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user=User(username,email,password)
        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("SignUp","Finally we saved the user to Firebase Database")
            }
    }
}

class User(val Name:String,val Email:String,val Password:String){
    constructor():this("","","")
}
