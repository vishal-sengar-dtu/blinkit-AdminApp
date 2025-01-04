package com.example.blinkitadmin.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.blinkitadmin.Firebase
import com.example.blinkitadmin.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _isImageUploaded = MutableStateFlow<Boolean>(false)
    var isImageUploaded = _isImageUploaded

    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadedUrls = _downloadedUrls

    private val _isProductSaved = MutableStateFlow<Boolean>(false)
    var isProductSaved = _isProductSaved

    fun saveImageInStorage(imageUris : ArrayList<Uri>) {
        val downloadUrls = ArrayList<String?>()

        imageUris.forEach { uri ->

            val imageRef = Firebase.getFirebaseStorageReference()
                .child(Firebase.getCurrentUserId())
                .child("images/")
                .child(UUID.randomUUID().toString())

            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                downloadUrls.add(url.toString())

                if(downloadUrls.size == imageUris.size) {
                    _isImageUploaded.value = true
                    _downloadedUrls.value = downloadUrls
                }
            }
        }
    }

    fun saveProductInDB(product : Product) {
        Firebase.getDatabaseInstance().getReference("Admins").child("AllProducts/${product.id}").setValue(product)
            .addOnSuccessListener {
                Firebase.getDatabaseInstance().getReference("Admins").child("ProductCategory/${product.id}").setValue(product)
                    .addOnSuccessListener {
                        Firebase.getDatabaseInstance().getReference("Admins").child("ProductType/${product.id}").setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value = true
                            }
                    }
            }
    }

    fun fetchAllProducts(category: String): Flow<List<Product>> = callbackFlow {
        val db = Firebase.getDatabaseInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for(obj in snapshot.children) {
                    val product = obj.getValue(Product::class.java)
                    if(category == "All Products" || category == product?.category) {
                        products.add(product!!)
                    }
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        db.addValueEventListener(eventListener)

        awaitClose { db.removeEventListener(eventListener) }

    }

}