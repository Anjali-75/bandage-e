package com.example.bandagee_commerce.model

import com.google.gson.annotations.SerializedName

//data class CartProductModel(
//
//	@field:SerializedName("CartProductModel")
//	val cartProductModel: ArrayList<CartProductModelItem?> = null
//)

data class CartProductModelItem(

    @field:SerializedName("images")
    val images: List<String?>? = null,

    @field:SerializedName("quantity")
    val quantity: Int? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("category")
    val category: String? = null
)
