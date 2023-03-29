package com.example.bandagee_commerce.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bandagee_commerce.Fragments.ProductList
import com.example.bandagee_commerce.R
import com.example.bandagee_commerce.helper.NetworkCheck
import com.example.bandagee_commerce.helper.Singleton
import com.example.bandagee_commerce.`interface`.UrlEndpoints
import com.example.bandagee_commerce.model.*
import com.example.bandagee_commerce.models.CartProductDeleteModel
import com.example.bandagee_commerce.models.ProductModel
import com.example.bandagee_commerce.models.Products
import com.example.bandagee_commerce.services.RetrofitClass
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class Cart : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var urlEndpoints: UrlEndpoints
    private lateinit var recycleViewCart:RecyclerView
    private var totalCost = 0
    var productData: Products? = null
    var isSelectionFromTouch = false
    var cartAdapter: CartAdapter? = null
    lateinit var cartData:PlaceOrderModel
    var deatilsItem:ArrayList<DetailsItem?>?=null
    //var shared-preferences: SharedPreferences = getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        initData()
        val placeOrder = findViewById<TextView>(R.id.textView)

        var sharedpreferences: SharedPreferences = getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
        sharedpreferences.getString("token"," ")?.let { fetchCart(it) }



        placeOrder.setOnClickListener{
            println("cartdata $cartData")
            sharedpreferences.getString("token"," ")?.let { cartData?.let { it1 ->
                placeOrder(it,
                    it1
                )
            } }
            startActivity(Intent(this, OrderPlaced::class.java))
            finish()
        }

    }
    private fun initData() {

        /*Creating the instance of retrofit */
        retrofit = RetrofitClass.getInstance()

        /*Get the reference of Api interface*/
        urlEndpoints = retrofit.create(UrlEndpoints::class.java)
    }

    private fun placeOrder(access_token: String, detailList: PlaceOrderModel){
        if(NetworkCheck.isConnected(this))
        {
            urlEndpoints.placeOrder(access_token, detailList)
                .enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Cart, "Order Placed Successfully", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(this@Cart, "An error Occurred", Toast.LENGTH_LONG).show()
                    }
                })
        }
        else
        {
            Toast.makeText(this, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchCart(access_token: String){
        if(NetworkCheck.isConnected(this))
        {
            urlEndpoints.getProduct(access_token)
                .enqueue(object : Callback<ArrayList<CartProductModelItem>> {
                    override fun onResponse(
                        call: Call<ArrayList<CartProductModelItem>>,
                        response: Response<ArrayList<CartProductModelItem>>
                    ) {
                        if (response.isSuccessful) {
                            var itemDetails:ArrayList<DetailsItem>? = null
                            var responseis:ArrayList<CartProductModelItem>? = response.body()

                            if (responseis != null) {
                                for(item in responseis) {

                                     var deatilsItem = DetailsItem(
                                        item.quantity.toString(),
                                        item.id
                                    )

                                    itemDetails?.add(deatilsItem)
                                }
                                cartData = PlaceOrderModel(
                                    itemDetails
                                )
                                    recycleViewCart = findViewById(R.id.recycleViewCart)
                                    recycleViewCart.layoutManager = GridLayoutManager(this@Cart,1)
                                    recycleViewCart.adapter =
                                        response.body()?.let { CartAdapter(this@Cart, it) }
                                }
                            }

                        Log.d("respose jkfnvkje", response.body().toString())
                    }

                    override fun onFailure(call: Call<ArrayList<CartProductModelItem>>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(this@Cart, "An error Occurred", Toast.LENGTH_LONG).show()
                    }
                })
        }
        else
        {
            Toast.makeText(this, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }

    inner class CartAdapter(var context: Context, var cartList: ArrayList<CartProductModelItem> ): RecyclerView.Adapter<CartAdapter.ProductViewHolder>(){


        inner class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            var spinner: Spinner = itemView.findViewById(R.id.dropdown_menu)
            var productNameTextView = itemView.findViewById<TextView>(R.id.prodname)
            var categoryTextView = itemView.findViewById<TextView>(R.id.prodcategory)
            var priceTextView = itemView.findViewById<TextView>(R.id.prodprice)
            var img = itemView.findViewById<ImageView>(R.id.prodimg)
            var deleteCartbtn = itemView.findViewById<TextView>(R.id.deleteCart)
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder{
            val view = LayoutInflater.from(context). inflate(
                R.layout.activity_cart_card, parent,
                false
            )
            totalCost = calTotCost(cartList)

            var orderTot = findViewById<TextView>(R.id.orderValueInt)
            val totInt = findViewById<TextView>(R.id.totalValueInt)

            orderTot.text = totalCost.toString()
            totInt.text = totalCost.toString()


            return ProductViewHolder(view)
        }

        override fun getItemCount(): Int {
            return cartList.size
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            var productModel = cartList[position]
            Log.d("cartList", cartList.toString())

            if (productModel != null) {
                holder.productNameTextView.text = productModel.name
                holder.categoryTextView.text = productModel.category
                holder.priceTextView.text = "Rs. ${productModel.price}"
                productModel.images?.get(0)?.let { setImage(context, holder.img, it) }

            }

            val numArray = arrayOf(
                1,
                2,
                3,
                4
            )

            Log.d("msggg", productModel.id.toString())

            val arrayAdapter =  ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                numArray
            )
            holder.spinner.setOnTouchListener { _, _ ->
                isSelectionFromTouch = true
                false
            }
            productModel.quantity?.let { holder.spinner.setSelection(it) }
               arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.spinner.adapter = arrayAdapter

            holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    var sharedpreferences: SharedPreferences = getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
                    var token =  sharedpreferences.getString("token"," ")
                    if (!isSelectionFromTouch) { return }

                    when (position) {

                        0-> {token?.let { productModel.id?.let { it1 -> updateCart(1, it, it1,cartList,holder) } }

                            var updateProduct = cartList.find { product -> product.id == productModel.id}
                            var idx = cartList.indexOf(updateProduct)
                            var newProd = CartProductModelItem(
                                images = updateProduct?.images,
                                quantity =1,
                                id = updateProduct?.id,
                                price = updateProduct?.price,
                                name = updateProduct?.name,
                                category = updateProduct?.category
                            )
                            cartList.set(idx,newProd)

                            notifyDataSetChanged()


                            totalCost = calTotCost(cartList)

                            var orderTot = findViewById<TextView>(R.id.orderValueInt)
                            val totInt = findViewById<TextView>(R.id.totalValueInt)

                            orderTot.text = totalCost.toString()
                            totInt.text = totalCost.toString()
                        }

                        1 -> {token?.let { productModel.id?.let { it1 -> updateCart(2, it, it1, cartList,holder) } }

                            var updateProduct = cartList.find { product -> product.id == productModel.id}
                            var idx = cartList.indexOf(updateProduct)
                            var newProd = CartProductModelItem(
                                images = updateProduct?.images,
                                quantity =2,
                                id = updateProduct?.id,
                                price = updateProduct?.price,
                                name = updateProduct?.name,
                                category = updateProduct?.category
                            )
                            cartList.set(idx,newProd)

                            notifyDataSetChanged()
                            totalCost = calTotCost(cartList)

                            var orderTot = findViewById<TextView>(R.id.orderValueInt)
                            val totInt = findViewById<TextView>(R.id.totalValueInt)

                            orderTot.text = totalCost.toString()
                            totInt.text = totalCost.toString()
                        }

                        2 ->{ token?.let { productModel.id?.let { it1 -> updateCart(3, it, it1,cartList,holder) } }
                            var updateProduct = cartList.find { product -> product.id == productModel.id}
                            var idx = cartList.indexOf(updateProduct)
                            var newProd = CartProductModelItem(
                                images = updateProduct?.images,
                                quantity =3,
                                id = updateProduct?.id,
                                price = updateProduct?.price,
                                name = updateProduct?.name,
                                category = updateProduct?.category
                            )
                            cartList.set(idx,newProd)

                            notifyDataSetChanged()
                            totalCost = calTotCost(cartList)

                            var orderTot = findViewById<TextView>(R.id.orderValueInt)
                            val totInt = findViewById<TextView>(R.id.totalValueInt)

                            orderTot.text = totalCost.toString()
                            totInt.text = totalCost.toString()}

                        3 -> {token?.let { productModel.id?.let { it1 -> updateCart(4, it, it1,cartList,holder) } }
                            var updateProduct = cartList.find { product -> product.id == productModel.id}
                            var idx = cartList.indexOf(updateProduct)
                            var newProd = CartProductModelItem(
                                images = updateProduct?.images,
                                quantity =4,
                                id = updateProduct?.id,
                                price = updateProduct?.price,
                                name = updateProduct?.name,
                                category = updateProduct?.category
                            )
                            cartList.set(idx,newProd)

                            notifyDataSetChanged()
                            totalCost = calTotCost(cartList)

                            var orderTot = findViewById<TextView>(R.id.orderValueInt)
                            val totInt = findViewById<TextView>(R.id.totalValueInt)

                            orderTot.text = totalCost.toString()
                            totInt.text = totalCost.toString()
                        }

                    }

                    isSelectionFromTouch = false

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    productModel.quantity?.let { holder.spinner.setSelection(it)
                        isSelectionFromTouch = false
                    }
                }

            }


            holder.deleteCartbtn.setOnClickListener {
                initData()
                var sharedpreferences: SharedPreferences = getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);

                val cartDeleteData = CartProductDeleteModel(
                    productModel.id
                )
                Log.d("productmODEL", cartDeleteData.toString())
                sharedpreferences.getString("token", "")
                    ?.let { it1 -> deleteCartProduct(it1, cartDeleteData,holder) }
            }
        }

        private fun deleteCartProduct(access_token: String, cartDeleteModel: CartProductDeleteModel, holder: ProductViewHolder) {
            if (NetworkCheck.isConnected(context)) {
                urlEndpoints.deleteProduct(access_token, cartDeleteModel)
                    .enqueue(object : Callback<CartProductDeleteModel> {
                        override fun onResponse(
                            call: Call<CartProductDeleteModel>,
                            response: Response<CartProductDeleteModel>
                        ) {
                            if (response.isSuccessful) {
                                val deleteProduct = cartList.find { product -> product.id == cartDeleteModel.productid }
                                productData = Singleton.dataList.products.find { product -> cartDeleteModel.productid == product?.id }
                                cartList.remove(deleteProduct)
                                totalCost = calTotCost(cartList)

                                notifyDataSetChanged()

                                var orderTot = findViewById<TextView>(R.id.orderValueInt)
                                val totInt = findViewById<TextView>(R.id.totalValueInt)

                                orderTot.text = totalCost.toString()
                                totInt.text = totalCost.toString()

                                productData?.count = 0

                            }

                        }
                        override fun onFailure(
                            call: Call<CartProductDeleteModel>,
                            t: Throwable
                        ) {
                            t.printStackTrace()
                            Toast.makeText(context, "An error Occurred", Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            } else {
                Toast.makeText(this@Cart, "Please check you internet connection", Toast.LENGTH_LONG)
                    .show()
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

    private fun updateCart(quantity: Int, access_token: String, productId: String, cartList: ArrayList<CartProductModelItem>,holder: CartAdapter.ProductViewHolder){

        val updateCartData= UpdateCartModel(
            quantity= quantity.toString(),
            productId = productId
        )

        if(NetworkCheck.isConnected(this))
        {
            urlEndpoints.updateProduct(access_token, updateCartData)
                .enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Cart, "Updated Successfully", Toast.LENGTH_LONG).show()
                            cartAdapter?.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(this@Cart, "An error Occurred", Toast.LENGTH_LONG).show()
                    }
                })
        }
        else
        {
            Toast.makeText(this, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }

    fun calTotCost(cartList:ArrayList<CartProductModelItem>):Int{
        var tempCost=0
        for(items in cartList){
            tempCost += Integer.parseInt(items.price) * items.quantity!!
        }
        Log.d("tempc", tempCost.toString())
        return tempCost
   }
}
