package com.example.bandagee_commerce.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bandagee_commerce.R
import com.example.bandagee_commerce.helper.NetworkCheck
import com.example.bandagee_commerce.helper.Singleton
import com.example.bandagee_commerce.`interface`.UrlEndpoints
import com.example.bandagee_commerce.model.CartModel
import com.example.bandagee_commerce.model.ResponseModel
import com.example.bandagee_commerce.models.Products
import com.example.bandagee_commerce.services.RetrofitClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class Product : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var urlEndpoints: UrlEndpoints;
    var productData: Products? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val name = findViewById<TextView>(R.id.name)
        val price = findViewById<TextView>(R.id.price)
        val desc = findViewById<TextView>(R.id.desc)
        val mainImg = findViewById<ImageView>(R.id.img_main)
        val img1 = findViewById<ImageView>(R.id.img_small1)
        val img2 = findViewById<ImageView>(R.id.img_small2)
        val addToCartBtn = findViewById<TextView>(R.id.addToCartBtnInside)
        val gotoCartBtn = findViewById<ImageView>(R.id.cart)




        val idOfProd = intent.getStringExtra("id")

        productData = Singleton.dataList.products.find { product -> idOfProd == product?.id }

        if(productData == null) finish()

        Log.d("data hai",productData.toString())

        name.setText(productData?.name)
        price.setText(productData?.price)
        desc.setText(productData?.description)
        productData?.images?.get(0)?.let { setImage(this, mainImg, it) }
        productData?.images?.get(1)?.let { setImage(this, img1, it) }
        productData?.images?.get(2)?.let { setImage(this, img2, it) }

        img1.setOnClickListener{
            val mainDrawable=  mainImg.drawable
            intent.getStringExtra("img2")?.let { setImage(this, mainImg, it) }
            img1.setImageDrawable(mainDrawable)
        }

        img2.setOnClickListener{
            val mainDrawable=  mainImg.drawable
            intent.getStringExtra("img3")?.let { setImage(this, mainImg, it) }
            img2.setImageDrawable(mainDrawable)
        }

        if(productData?.count!! == 0){
        addToCartBtn.setOnClickListener {
            var sharedpreferences: SharedPreferences =
                getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
            val quantity = 1;
            val productId = intent.getStringExtra("id")

            val cartData = CartModel(
                quantity.toString(),
                productId
            )
            initData()
            sharedpreferences.getString("token", "")?.let { it1 -> addToCart(it1, cartData) }
        }
        }
        else{

            addToCartBtn.setText("Go To Cart")
            addToCartBtn.setOnClickListener{
                var  intent = Intent(this@Product, Cart::class.java)
                startActivity(intent)
            }
        }

        gotoCartBtn.setOnClickListener{
            var  intent = Intent(this@Product, Cart::class.java)
            finish()
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        var addToCartBtn = findViewById<TextView>(R.id.addToCartBtnInside)
        addToCartBtn.setOnClickListener {
            if (productData?.count == 0) {

                var sharedpreferences: SharedPreferences =
                    getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
                val quantity = 1;
                val productId = intent.getStringExtra("id")

                val cartData = CartModel(
                    quantity.toString(),
                    productId
                )
                initData()
                sharedpreferences.getString("token", "")?.let { it1 -> addToCart(it1, cartData) }
            }
            else{

                addToCartBtn.setText("Go To Cart")
                addToCartBtn.setOnClickListener{
                    var  intent = Intent(this@Product, Cart::class.java)
                    startActivity(intent)
                }
            }

        }
    }




    private fun initData() {

        /*Creating the instance of retrofit */
        retrofit = RetrofitClass.getInstance()

        /*Get the reference of Api interface*/
        urlEndpoints = retrofit.create(UrlEndpoints::class.java)
    }

    private fun addToCart(access_token:String, cartData: CartModel) {
        Log.d("cartModel", cartData.toString())
        if(NetworkCheck.isConnected(this))
        {
            urlEndpoints.addProduct(access_token, CartModel(cartData.quantity, cartData.productid))
                .enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(
                        call: Call< ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("response ", response.body().toString())
                            Toast.makeText(this@Product, "Item Added to Cart", Toast.LENGTH_LONG).show()
                            productData?.count = 1
                            runOnUiThread {
                                var addToCartBtn = findViewById<TextView>(R.id.addToCartBtnInside)
                                addToCartBtn.setText("Go To Cart")
                                addToCartBtn.setOnClickListener{
                                    var  intent = Intent(this@Product, Cart::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(this@Product, "An error Occurred", Toast.LENGTH_LONG).show()
                    }
                })
        }
        else
        {
            Toast.makeText(this, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun setImage(context: Context?, imageView: ImageView, imgURl: String) {
        Glide
            .with(context!!)
            .load(imgURl)
            .fitCenter()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(imageView)
    }
}