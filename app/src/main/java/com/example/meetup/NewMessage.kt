package com.example.meetup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.profile.*
import java.util.*

class NewMessage : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)

        supportActionBar?.title = "Friends"
        fetchUsers()
    }

    private fun fetchUsers() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycleViewer)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Friends/$uid/Friends_List")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(Friends_List::class.java)
                    Log.d("Message", user.toString())
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    class UserItem(val user: Friends_List) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.friendsabout).text = user.about
            viewHolder.itemView.findViewById<TextView>(R.id.friendsname).text = user.name
            Picasso.with(viewHolder.itemView.context).load(user.profilepic)
                .into(viewHolder.itemView.findViewById<CircleImageView>(R.id.friendsdp))
        }

        override fun getLayout(): Int {
            return R.layout.friends
        }
    }
}