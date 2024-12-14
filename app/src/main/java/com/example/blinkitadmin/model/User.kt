package com.example.blinkitadmin.model

data class User(
    var isAdmin : Boolean = false,
    var uId : String? = null,
    val userPhoneNumber : String? = null,
    var userAddress : String? = null,
)
