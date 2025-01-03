package com.example.blinkitadmin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object Firebase {
    private var firebaseAuthInstance : FirebaseAuth? = null
    private var firebaseDatabaseInstance : FirebaseDatabase? = null
    private var firebaseStorageInstance : FirebaseStorage? = null

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

    fun getFirebaseStorageReference() : StorageReference {
        if(firebaseStorageInstance == null) {
            firebaseStorageInstance = FirebaseStorage.getInstance()
        }
        return firebaseStorageInstance!!.reference
    }

    fun getCurrentUserId() : String {
        return getAuthInstance().currentUser?.uid.toString()
    }
 }