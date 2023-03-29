package com.example.bandagee_commerce.models

import com.google.gson.annotations.SerializedName

data class ProductModel(

    @field:SerializedName("products")
    val products: ArrayList<Products?>
)

data class Products(

    @field:SerializedName("images")
    val images: List<String?>? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("category")
    val category: String? = null,

    var count: Int = 0
)
