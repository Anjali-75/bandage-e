package com.example.bandagee_commerce.model

import com.google.gson.annotations.SerializedName

data class RegistrationModel(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("name")
    val username: String? = null
)
