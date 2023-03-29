package com.example.bandagee_commerce.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bandagee_commerce.Fragments.ProductList
import com.example.bandagee_commerce.R

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, ProductList())
            .commit()
    }

    override fun onRestart() {
      super.onRestart()
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.frameLayout, ProductList())
//            .commit()
   }
}