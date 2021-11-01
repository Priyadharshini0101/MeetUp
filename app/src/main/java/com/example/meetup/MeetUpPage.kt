package com.example.meetup


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.feeds.*
import kotlinx.android.synthetic.main.homepage.*
import kotlinx.android.synthetic.main.meetup.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Feeds.newInstance] factory method to
 * create an instance of this fragment.
 */
class Feeds : Fragment() {
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
        val rootView = inflater.inflate(R.layout.meetup, container, false)
        val recyclerView = rootView.recycleView
        val adapter = GroupAdapter<GroupieViewHolder>()

        val uid = FirebaseAuth.getInstance().uid
        var currentUser1 = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        currentUser1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Feeds.currentUser = snapshot.getValue(User::class.java)
                if (Feeds.currentUser?.interested != null) {
                    Feeds.interest = Feeds.currentUser!!.interested
                    Feeds.about = Feeds.currentUser!!.about
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    var k = 0
                    if (user != null && user.uid != FirebaseAuth.getInstance().uid) {
                        for (i in interest!!) {
                            for (j in user.interested!!)
                                if (i == j) {
                                    ++k
                                }
                        }
                        if (k > 0) {
                            val uid = FirebaseAuth.getInstance().uid
                            val ref = FirebaseDatabase.getInstance()
                                .getReference("/Friends/$uid/Friends_List")
                            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                @RequiresApi(Build.VERSION_CODES.P)
                                override fun onDataChange(p0: DataSnapshot) {
                                    var bool = false
                                    var activity: Active? = null
                                    p0.children.forEach {
                                        val user1 = it.getValue(Friends_List::class.java)
                                        if (user1?.uid == user!!.uid) {
                                            bool = true
                                        }
                                    }
                                    if (bool == false) {
                                        adapter.add(UserItem(user))
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                        }

                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLog::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                }

                recyclerView.adapter = adapter
                rootView.loading_spinner5.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        listentoMessage()
        fetchCurrentUser()
        return rootView
    }
    val latestMessageHashMap = HashMap<String, ChatMessage>()
    val adapter = GroupAdapter<GroupieViewHolder>()
    private fun refreshTheLatestMessage() {
        adapter.clear()
        latestMessageHashMap.values.forEach {
            adapter.add(LatestMessage(it))
            Log.d("ChatMessage.text", it.text)
        }
    }


    private fun listentoMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latestMessage/$fromId/")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageHashMap[snapshot.key!!] = chatMessage
                refreshTheLatestMessage()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageHashMap[snapshot.key!!] = chatMessage
                refreshTheLatestMessage()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        var currentUser1 = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        currentUser1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatting.currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    class LatestMessage(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
        var chatPartnerUser: User? = null
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.newmessagetext).text = chatMessage.text
            val chatPartnerId: String
            if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                chatPartnerId = chatMessage.toId
            } else {
                chatPartnerId = chatMessage.fromId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/Users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(User::class.java)
                    viewHolder.itemView.findViewById<TextView>(R.id.newmessagename).text =
                        chatPartnerUser?.name
                    val displayPicture =
                        viewHolder.itemView.findViewById<CircleImageView>(R.id.newmessageimage)
                    Picasso.with(viewHolder.itemView.context).load(chatPartnerUser?.profilepic)
                        .into(displayPicture)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        override fun getLayout(): Int {
            return R.layout.newmessage
        }
    }
    companion object {
        var about: String? = null
        var currentUser: User? = null
        var interest: ArrayList<String>? = null
        val USER_KEY = "MeetUpPage"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Feeds.
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


class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.findViewById<TextView>(R.id.aboutfeeds).text = user.about
        val string = user.interested.toString()
        viewHolder.itemView.findViewById<TextView>(R.id.interestedfeeds).text =
            string.subSequence(1, string.length - 1)
        viewHolder.itemView.findViewById<TextView>(R.id.displaynamefeeds).text = user.name
        Picasso.with(viewHolder.itemView.context).load(user.profilepic)
            .into(viewHolder.itemView.findViewById<ImageView>(R.id.dpfeeds))

    }

    override fun getLayout(): Int {
        return R.layout.feeds
    }
}
