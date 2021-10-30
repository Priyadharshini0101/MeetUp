package com.example.meetup

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
 class User(val uid:String,val name: String, val email: String, val profilepic:String?=null,val interested:ArrayList<String>?=null):Parcelable {
    constructor() : this("","", "")
}

class ChatMessage(val id:String,val fromId:String,val text:String,val toId:String,val timeStamp:Long){
    constructor():this("","","","",-1)
}

class Friends(val uid:String,val name: String, val email: String, val profilepic:String?=null,val interested:String?=null){
    constructor() : this("","", "")
}