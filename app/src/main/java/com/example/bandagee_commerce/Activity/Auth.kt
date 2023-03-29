package com.example.bandagee_commerce.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bandagee_commerce.Fragments.LoginFragment
import com.example.bandagee_commerce.R

class Auth : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        var sharedpreferences: SharedPreferences? = getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
        var token = sharedpreferences?.getString("token",null)

        if(token != null ){
            startActivity(Intent(this, Home::class.java))
            finish()
        }
        else {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.authFrame, LoginFragment())
                .commit()
        }
    }
}