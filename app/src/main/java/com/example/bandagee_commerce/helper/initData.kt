package com.example.bandagee_commerce.helper


import com.example.bandagee_commerce.`interface`.UrlEndpoints
import com.example.bandagee_commerce.services.RetrofitClass
import retrofit2.Retrofit

class initData {

    private lateinit var retrofit:Retrofit
    private lateinit var urlEndpoints: UrlEndpoints

    private fun initData(){
        /*Creating the instance of retrofit */
        retrofit = RetrofitClass.getInstance()

        /*Get the reference of Api interface*/
        urlEndpoints = retrofit.create(UrlEndpoints::class.java)
    }
}