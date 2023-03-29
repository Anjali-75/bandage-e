package com.example.bandagee_commerce.model

import com.google.gson.annotations.SerializedName

data class DetailsItem(

	@field:SerializedName("quantity")
	val quantity: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null
)

data class PlaceOrderModel(

	@field:SerializedName("details")
	val details: List<DetailsItem?>? = null
)
