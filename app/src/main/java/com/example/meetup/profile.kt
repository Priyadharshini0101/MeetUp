package com.example.meetup

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile.*
import kotlinx.android.synthetic.main.profile1.*
import kotlinx.android.synthetic.main.profile1.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView= inflater.inflate(R.layout.profile1, container, false)

        rootView.editprofile.setOnClickListener {
            activity?.let {
                val intent=Intent(it,EditProfile::class.java)
                it.startActivity(intent)
            }
        }

        val uid=FirebaseAuth.getInstance().uid



        val ref1 = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDataChange(p0: DataSnapshot) {
                val user=p0.getValue(User::class.java)
                if (user != null) {
                    if (user.profilepic!="") {
                        displayname.setText(user.name)
                        Picasso.with(context).load(user.profilepic).into(dp)
//                        val interests= user..toString()
//                        interested.text=interests.subSequence(1,interests.length-1)

                    } else {
                        displayname.setText(user.name)
//                        val interests= user.Interested_in.toString()
//                        interested.text=interests.subSequence(1,interests.length-1)
                    }
                   loading_spinner1.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        val ref2 = FirebaseDatabase.getInstance().getReference("/Users/")
        ref2.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.children.forEach {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null && user.uid == FirebaseAuth.getInstance().uid) {
                        if (user.profilepic !=null) {
                            displayname.setText(user.name)
                            Picasso.with(context).load(user.profilepic).into(dp)




                        } else {
                            displayname.text = user.name

                        }
                       loading_spinner1.visibility = View.GONE
                    }
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        setHasOptionsMenu(true)
        return rootView
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_settings -> {
                activity?.let {
                    val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(it)
                        alert.setMessage("Are you sure you want to Signout?")
                            .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                                Firebase.auth.signOut()
                                val intent=Intent(it,welcome::class.java)
                                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                            }).setNegativeButton("NO", null)

                        val alert1: android.app.AlertDialog? = alert.create()
                        if (alert1 != null) {
                            alert1.show()
                        }

                    }
                }
            }


        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
         inflater.inflate(R.menu.settingmenu,menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


