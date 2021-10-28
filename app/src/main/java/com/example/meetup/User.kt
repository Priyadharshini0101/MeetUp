package com.example.meetup

 class User(val uid:String,val name: String, val email: String, val profilepic:String?=null,val Interested_in:ArrayList<String>?=null) {
    constructor() : this("","", "")
}
class Sports(val uid:String,val name: String, val email: String, val profilepic:String?=null) {
    constructor() : this("","", "")
}
class Technology(val uid:String,val name: String, val email: String, val profilepic:String?=null) {
    constructor() : this("","", "")
}

class Music(val uid:String,val name: String, val email: String, val profilepic:String?=null) {
    constructor() : this("","", "")
}

class Arts(val uid:String,val name: String, val email: String, val profilepic:String?=null) {
    constructor() : this("","", "")
}

class Entertainment(val uid:String,val name: String, val email: String, val profilepic:String?=null) {
    constructor() : this("","", "")
}


