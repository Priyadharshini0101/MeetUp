package com.example.meetup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.meetup.view.*
import kotlinx.android.synthetic.main.recyclerview.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [chatting.newInstance] factory method to
 * create an instance of this fragment.
 */
class chatting : Fragment() {
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
        var intent: Boolean? = false
        val recyclerView = rootView.recycleView
        rootView.loading_spinner5.visibility=View.GONE
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            intent = true
            val intent = Intent(view.context, ChatLog::class.java)
            val row = item as LatestMessage
            intent.putExtra(Feeds.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listentoMessage()
        fetchCurrentUser()
        setHasOptionsMenu(true)
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
                currentUser = snapshot.getValue(User::class.java)
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
        var currentUser: User? = null
        val USER_KEY = "Intent to chat Log"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment chatting.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            chatting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
