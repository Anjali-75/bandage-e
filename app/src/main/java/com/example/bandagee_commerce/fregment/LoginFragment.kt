package com.example.bandagee_commerce.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.bandagee_commerce.Activity.Auth
import com.example.bandagee_commerce.Activity.Home
import com.example.bandagee_commerce.R
import com.example.bandagee_commerce.helper.NetworkCheck
import com.example.bandagee_commerce.`interface`.UrlEndpoints
import com.example.bandagee_commerce.model.LoginModel
import com.example.bandagee_commerce.model.LoginResponseModel
import com.example.bandagee_commerce.model.ResponseModel
import com.example.bandagee_commerce.services.RetrofitClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginFragment : Fragment() {

    private lateinit var v: View
    private lateinit var retrofit: Retrofit
    private lateinit var urlEndpoints: UrlEndpoints
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false)
        val loginBtn = v.findViewById<TextView>(R.id.loginBtn)
        val loginToRegisterBtn = v.findViewById<TextView>(R.id.registerTextBox)
        val loginPassword = v.findViewById<TextView>(R.id.passwordTextBoxLogin)
        val loginEmail  = v.findViewById<TextView>(R.id.emailTextBoxLogin)

        loginToRegisterBtn.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.authFrame, RegisterFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

            loginBtn.setOnClickListener{
                val userLoginData = LoginModel(
                    loginPassword.text.toString(),
                    loginEmail.text.toString()
                )
                initData()
                loginUser(userLoginData)
            }


        return v;
    }

//    override fun onResume() {
//        super.onResume()
//        var sharedpreferences: SharedPreferences? = activity?.getSharedPreferences("access_key_preference", Context.MODE_PRIVATE);
//        var token = sharedpreferences?.getString("token","")
//        if(token != null){
//            startActivity(Intent(context, Home::class.java))
//            activity?.finish()
//        }
//        else{
//            startActivity(Intent(context, Auth::class.java))
//        }
//    }


    private fun initData() {

        /*Creating the instance of retrofit */
        retrofit = RetrofitClass.getInstance()

        /*Get the reference of Api interface*/
        urlEndpoints = retrofit.create(UrlEndpoints::class.java)
    }


    private fun loginUser(userData: LoginModel?) {
        if(NetworkCheck.isConnected(context))
        {
            urlEndpoints.getUser(LoginModel(userData?.password, userData?.email))
                .enqueue(object : Callback<LoginResponseModel> {
                    override fun onResponse(
                        call: Call<LoginResponseModel>,
                        response: Response<LoginResponseModel>
                    ){
                        if (response.isSuccessful) {

                            if(response.body()?.success == 401){
                                Toast.makeText(context,"Register Email",Toast.LENGTH_LONG).show()
                            }
                            else if(response.body()?.success == 400){
                                Toast.makeText(context, "wrong email or password",Toast.LENGTH_LONG).show()
                            }
                            else {
                                var token = response.body()?.accessToken.toString()

                                var pref = activity?.getSharedPreferences(
                                    "access_key_preference",
                                    Context.MODE_PRIVATE
                                )?.edit()

                                pref?.putString("token", token)?.commit()

                                startActivity(Intent(context, Home::class.java))

                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                        Toast.makeText(context, "Something went wrong",Toast.LENGTH_LONG).show()
                        Log.d("wait", "wait")
                    }
                })
        }
        else
        {
            Toast.makeText(context, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }

}