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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
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


        val rootView =inflater.inflate(R.layout.meetup, container, false)


//        interesting=getParcelableExtra<ArrayList>(Interested.USER_KEY1)
        val recyclerView=  rootView.recycleView
        val adapter= GroupAdapter<GroupieViewHolder>()


        val uid=FirebaseAuth.getInstance().uid
        var currentUser1= FirebaseDatabase.getInstance().getReference("/Users/$uid")
        currentUser1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Feeds.currentUser =snapshot.getValue(User::class.java)
                if(Feeds.currentUser?.interested!=null){
                    Feeds.interest = Feeds.currentUser!!.interested
                    Feeds.about=Feeds.currentUser!!.about
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        Log.d("Dhanush","${Feeds.interest}")

        val ref= FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    var k=0


                        if (user != null && user.uid != FirebaseAuth.getInstance().uid){
                            for (i in interest!!) {
                                for(j in user.interested!!)
                            if(i==j) {
                               ++k
                            }
                            }
                            if(k>0){
                                adapter.add(UserItem(user))
                            }
                }
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem=item as UserItem
                    val intent= Intent(view.context, ChatLog::class.java)
                    intent.putExtra(Feeds.USER_KEY,userItem.user)
                    startActivity(intent)



                }

                recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        return rootView

    }




    companion object {
        var about:String?=null
        var currentUser:User?=null
        var interest:ArrayList<String>?=null
        val USER_KEY="MeetUpPage"
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



    class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val uid=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/Friends/$uid/Friends_List")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDataChange(p0: DataSnapshot) {
                var bool = false
                p0.children.forEach {
                    val user1 = it.getValue(Friends_List::class.java)
                    Log.d("Dhanush2","${user.uid}")
                    if (user1?.uid == user!!.uid) {
                        Log.d("Dhanush1","${user1?.uid}")
                         bool = true
                    }
                }
                if(bool==true)
                    viewHolder.itemView.findViewById<TextView>(R.id.iffriends).text="Your friend"
                else
                    viewHolder.itemView.findViewById<TextView>(R.id.iffriends).text="Send a hello request"
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        viewHolder.itemView.findViewById<TextView>(R.id.interestedfeeds).text= user.interested.toString()
        viewHolder.itemView.findViewById<TextView>(R.id.displaynamefeeds).text = user.name
            Picasso.with(viewHolder.itemView.context).load(user.profilepic)
                .into(viewHolder.itemView.findViewById<ImageView>(R.id.dpfeeds))
        }

    override fun getLayout(): Int {
        return R.layout.feeds
    }
}
