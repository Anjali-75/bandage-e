package com.example.bandagee_commerce.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bandagee_commerce.Activity.Cart
import com.example.bandagee_commerce.Activity.Product
import com.example.bandagee_commerce.R
import com.example.bandagee_commerce.helper.NetworkCheck
import com.example.bandagee_commerce.helper.Singleton
import com.example.bandagee_commerce.helper.initData
import com.example.bandagee_commerce.`interface`.UrlEndpoints
import com.example.bandagee_commerce.model.CartModel
import com.example.bandagee_commerce.model.CartProductModelItem
import com.example.bandagee_commerce.model.ResponseModel
import com.example.bandagee_commerce.models.ProductModel
import com.example.bandagee_commerce.models.Products
import com.example.bandagee_commerce.services.RetrofitClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class ProductList : Fragment() {

    private lateinit var v: View
    private  lateinit var retrofit:Retrofit
    private lateinit var urlEndpoints: UrlEndpoints
    lateinit var productListView:RecyclerView
    var productData: Products? = null
    var productAdapter:ProductAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_product_list, container, false)
        productListView = v.findViewById(R.id.product_list)
        initData()
        getProducts();

        var cartbtn = v.findViewById<ImageView>(R.id.cart)
        cartbtn.setOnClickListener{
            var  intent = Intent(context, Cart::class.java)
            startActivity(intent)
        }

        return v;
    }

    override fun onResume() {
        super.onResume()
        productAdapter?.notifyDataSetChanged()
    }



   fun fetchCart(access_token: String, productList: ProductModel?){
        if(NetworkCheck.isConnected(context))
        {
            urlEndpoints.getProduct(access_token)
                .enqueue(object : Callback<ArrayList<CartProductModelItem>> {
                    override fun onResponse(
                        call: Call<ArrayList<CartProductModelItem>>,
                        response: Response<ArrayList<CartProductModelItem>>
                    ) {
                        if (response.isSuccessful) {
                            println("response is" + response.body())
                            val cartList = response.body()
                            var productInCart: Products?
                            if (cartList != null) {
                                for(item in cartList) {
                                     productInCart = Singleton.dataList?.products?.find { products -> products?.id == item.id }
                                    productInCart?.count = item.quantity?:0
                                }
                            }
                        }

                        Log.d("respose jkfnvkje", response.body().toString())
                    }

                    override fun onFailure(call: Call<ArrayList<CartProductModelItem>>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(context, "An error Occurred", Toast.LENGTH_LONG).show()
                    }
                })
        }
        else
        {
            Toast.makeText(context, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }


    private fun getProducts(){
        if(NetworkCheck.isConnected(context)){
            urlEndpoints.getAllProducts().enqueue(
                object : Callback<ProductModel>{
                    override fun onResponse(
                        call: Call<ProductModel>,
                        response: Response<ProductModel>
                    ) {

                        if(response.isSuccessful){
                            val productList = response.body()
                            addDataToSingleton(response.body())
                            var sharedpreferences: SharedPreferences? = context?.getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
                            sharedpreferences?.getString("token"," ")?.let { fetchCart(it, productList) }


                             productAdapter = context?.let{ response.body()
                                ?.let { it1 -> ProductAdapter(it, it1.products, urlEndpoints) } }

                            productListView.layoutManager = GridLayoutManager(context, 2)
                            productListView.adapter = productAdapter

                        }
                    }

                    override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                    }

                }
            )
        }
    }

    private fun initData() {

        /*Creating the instance of retrofit */
        retrofit = RetrofitClass.getInstance()

        /*Get the reference of Api interface*/
        urlEndpoints = retrofit.create(UrlEndpoints::class.java)
    }

    fun addDataToSingleton(allProductList: ProductModel?){
        if (allProductList != null) {
            Singleton.dataList = allProductList
        }
    }


    inner class ProductAdapter(var context: Context, private var ProductList: ArrayList<Products?>, var urlEndpoints: UrlEndpoints): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

        inner class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            var productNameTextView = itemView.findViewById<TextView>(R.id.prod_name)
            var productImg = itemView.findViewById<ImageView>(R.id.img)
            var categoryTextView = itemView.findViewById<TextView>(R.id.category)
            var priceTextView = itemView.findViewById<TextView>(R.id.price)
            var addToCartBtn = itemView.findViewById<TextView>(R.id.addToCartBtn)
        }





        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder{
            val view = LayoutInflater.from(context). inflate(
                R.layout.activity_card, parent,
                false
            )
            return ProductViewHolder(view)
        }

        override fun getItemCount(): Int {
            return ProductList.size
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            var productModel = ProductList[position]
            if (productModel != null) {
                Log.d("checklOG",productModel.toString())
                holder.productNameTextView.text = productModel.name
                holder.categoryTextView.text = productModel.category
                holder.priceTextView.text = "Rs. ${productModel.price}"
                productModel.images?.get(0)?.let { setImage(context,holder.productImg, it) }
            }

            var productData = Singleton.dataList.products.find { product -> productModel?.id == product?.id }

            holder.itemView.setOnClickListener {
                    var intent = Intent(context, Product::class.java)
                    var bundle = Bundle()
                    if (productModel != null) {
                        intent.putExtra("id", productModel.id)
                        Log.d("hello kwjef", productModel.id.toString())
                    }
                    startActivity(context, intent, bundle)
                }

            if(productData?.count == 0) {
                holder.addToCartBtn.text = "Add To Cart"
                holder.addToCartBtn.setOnClickListener {
                    val cartData = CartModel(
                        "1",
                        productModel?.id.toString()
                    )
                    var sharedpreferences: SharedPreferences? = context?.getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
                    sharedpreferences?.getString("token"," ")?.let { addToCart(it, cartData,holder.addToCartBtn, productData) }

                }
            }
                else {
                holder.addToCartBtn.text = "Go To Cart"
                holder.addToCartBtn.setOnClickListener {
                    var bundle = Bundle()
                    var intent = Intent(context, Cart::class.java)
                    startActivity(context, intent, bundle)
                }
            }
        }

        private fun addToCart(access_token:String, cartData: CartModel,addToCartBtn:TextView, ProductData:Products?) {
            Log.d("cartModel", cartData.toString())
            if(NetworkCheck.isConnected(context))
            {
                urlEndpoints.addProduct(access_token, CartModel(cartData.quantity, cartData.productid))
                    .enqueue(object : Callback<ResponseModel> {
                        override fun onResponse(
                            call: Call<ResponseModel>,
                            response: Response<ResponseModel>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("response ", response.body().toString())
                                Toast.makeText(context, "Item Added to Cart", Toast.LENGTH_LONG).show()
                                ProductData?.count = 1
                                activity?.runOnUiThread {
                                    addToCartBtn.text = "Go To Cart"
                                    addToCartBtn.setOnClickListener{
                                        var bundle = Bundle()
                                        var  intent = Intent(context, Cart::class.java)
                                        startActivity(context, intent,bundle)
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(context, "An error Occurred", Toast.LENGTH_LONG).show()
                        }
                    })
            }
            else
            {
                Toast.makeText(context, "Please check you internet connection", Toast.LENGTH_LONG).show()
            }
        }

        private fun setImage(context: Context?, imageView: ImageView, imgURl: String){
            Glide
                .with(context!!)
                .load(imgURl)
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView)
        }


    }

}