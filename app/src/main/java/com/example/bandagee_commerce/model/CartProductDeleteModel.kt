package com.example.bandagee_commerce.models

import com.google.gson.annotations.SerializedName

data class CartProductDeleteModel(

    @field:SerializedName("productid")
    val productid: String? = null
)
