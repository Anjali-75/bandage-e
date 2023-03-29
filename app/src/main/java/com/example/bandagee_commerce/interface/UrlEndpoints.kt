package com.example.bandagee_commerce.`interface`


import com.example.bandagee_commerce.model.*
import com.example.bandagee_commerce.models.CartProductDeleteModel
import com.example.bandagee_commerce.models.ProductModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UrlEndpoints {

    @POST("/auth/registration")
    fun createUser(@Body params: RegistrationModel?): Call<RegistrationModel>

    @GET("/getProducts")
    fun getAllProducts(): Call<ProductModel>

    //@Headers("authorization":shared prefrenve)
    @POST("/auth/login")
    fun getUser( @Body params: LoginModel?): Call<LoginResponseModel>


    @POST("/cart/addProduct")
    fun addProduct(@Header("authorization") jwtToken :String , @Body params: CartModel?) : Call<ResponseModel>

    @GET("/cart/getProduct")
    fun getProduct(@Header("authorization") jwtToken: String): Call<ArrayList<CartProductModelItem>>

    @POST("/cart/deleteProduct")
    fun deleteProduct(@Header("authorization") jwtToken: String, @Body params: CartProductDeleteModel): Call<CartProductDeleteModel>

    @POST("/cart/updateProduct")
    fun updateProduct(@Header("authorization") jwtToken: String, @Body params: UpdateCartModel): Call<ResponseModel>

    @POST("/placeOrder")
    fun placeOrder(@Header("authorization") jwtToken: String, @Body params:PlaceOrderModel): Call<ResponseModel>
}


