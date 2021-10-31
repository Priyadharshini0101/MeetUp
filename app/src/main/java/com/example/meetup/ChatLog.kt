package com.example.meetup

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*

class ChatLog : AppCompatActivity() {
    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val recyclerView1 = findViewById<RecyclerView>(R.id.recycleView1)

        user = intent.getParcelableExtra<User>(Feeds.USER_KEY)

        supportActionBar?.title = user?.name

        recyclerView1.adapter = adapter

        ListenMessage()
        findViewById<Button>(R.id.textmessage).setOnClickListener {
            performSendMessage()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_chatting_to_friends -> {
                val uid = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("/Friends/$uid/Friends_List")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.P)
                    override fun onDataChange(p0: DataSnapshot) {
                        var bool = false
                        var activity: Active? = null
                        p0.children.forEach {
                            val user1 = it.getValue(Friends_List::class.java)
                            Log.d("Dhanush1", "${user1?.uid}")
                            if (user1?.uid == user!!.uid) {
                                Toast.makeText(
                                    this@ChatLog,
                                    "The person is already add to your friends list",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                bool = true
                            }
                        }
                        if (bool == false) {
                            val ref1 = FirebaseDatabase.getInstance().getReference("/Friends/${uid}")
                            val friend = Friends_List(user!!.uid, user!!.name, user!!.email, user!!.profilepic, user!!.interested.toString(), user!!.about
                            )
                            ref1.child("Friends_List/${user!!.uid}").setValue(friend)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun ListenMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == fromId) {
                        val currentUSer = chatting.currentUser ?: return

                        adapter.add(ChatFrom(chatMessage.text, currentUSer))
                    } else {
                        adapter.add(ChatTo(chatMessage.text, user!!))
                    }
                }
                findViewById<RecyclerView>(R.id.recycleView1).scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {
        val edittext = findViewById<EditText>(R.id.editText).text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        if (fromId == null) return
        if (toId == null) return
        if (edittext == "") return
        val reference = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId").push()
        val referenceTo = FirebaseDatabase.getInstance().getReference("/user-message/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!, fromId, edittext, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message:${reference.key}")
                findViewById<EditText>(R.id.editText).text.clear()
                findViewById<RecyclerView>(R.id.recycleView1).scrollToPosition(adapter.itemCount - 1)
            }

        referenceTo.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message:${reference.key}")
            }

        val latestMessage = FirebaseDatabase.getInstance().getReference("/latestMessage/$fromId/$toId")
        val latestMessageTo = FirebaseDatabase.getInstance().getReference("/latestMessage/$toId/$fromId")
        latestMessage.setValue(chatMessage)
        latestMessageTo.setValue(chatMessage)
    }

}

class ChatFrom(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.chatname1).text = user.name
        viewHolder.itemView.findViewById<TextView>(R.id.chat_from).text = text
        val displayPicture = viewHolder.itemView.userfrom_dp
        Picasso.with(viewHolder.itemView.context).load(user.profilepic).into(displayPicture)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from
    }
}

class ChatTo(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.chatname).text = user.name
        viewHolder.itemView.findViewById<TextView>(R.id.chat_to).text = text
        val displayPicture = viewHolder.itemView.userto_dp
        Picasso.with(viewHolder.itemView.context).load(user.profilepic).into(displayPicture)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to
    }
}

