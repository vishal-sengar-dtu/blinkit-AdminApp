package com.example.blinkitadmin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Firebase {
    private var firebaseAuthInstance : FirebaseAuth? = null
    private var firebaseDatabaseInstance : FirebaseDatabase? = null

    fun getAuthInstance() : FirebaseAuth {
        if(firebaseAuthInstance == null) {
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getDatabaseInstance() : FirebaseDatabase {
        if(firebaseDatabaseInstance == null) {
            firebaseDatabaseInstance = FirebaseDatabase.getInstance()
        }
        return firebaseDatabaseInstance!!
    }

    fun getCurrentUserId() : String {
        return getAuthInstance().currentUser?.uid.toString()
    }
 }