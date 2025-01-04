package com.example.blinkitadmin.model

import java.util.UUID

data class Product(
    var id : String? = null,
    var title : String? = null,
    var quantity : Int? = null,
    var unit : String? = null,
    var price : Int? = null,
    var stock : Int? = null,
    var discount : Int? = null,
    var category : String? = null,
    var type : String? = null,
    var itemCount : Int? = null,
    var adminUid : String? = null,
    var productImageUrl : ArrayList<String?>? = null
)
