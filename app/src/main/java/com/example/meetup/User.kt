package com.example.meetup
//Common Database Model
 class User(val Name: String, val Email: String, val Password: String) {
        constructor() : this("", "", "")
 class User(val Name: String, val Email: String) {
        constructor() : this("", "")
    }

