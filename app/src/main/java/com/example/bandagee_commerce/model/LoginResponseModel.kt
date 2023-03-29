package com.example.bandagee_commerce.model

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("success")
	val success: Int? = null
)
