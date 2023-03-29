package com.example.bandagee_commerce.Fragments

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.bandagee_commerce.R
import com.example.bandagee_commerce.helper.NetworkCheck
import com.example.bandagee_commerce.`interface`.UrlEndpoints
import com.example.bandagee_commerce.model.RegistrationModel
import com.example.bandagee_commerce.services.RetrofitClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RegisterFragment : Fragment() {

    private lateinit var v: View;
    private lateinit var retrofit:Retrofit
    lateinit var urlEndpoints: UrlEndpoints

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_register, container, false);
        val registerEmail = v.findViewById<EditText>(R.id.emailTextBox)
        val registerUsername = v.findViewById<EditText>(R.id.nameTextBox)
        val registerPassword = v.findViewById<EditText>(R.id.passwordTextBox)
        val cnfRegisterPassword = v.findViewById<EditText>(R.id.confirmpasswordTextBox)
        val registerBtn = v.findViewById<TextView>(R.id.Registerbtn)
        val loginBtn = v.findViewById<TextView>(R.id.loginpage)

        v.findViewById<TextView>(R.id.loginText).setOnClickListener{
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.authFrame, LoginFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        loginBtn.setOnClickListener{
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.authFrame, LoginFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        registerBtn.setOnClickListener {

            val email = registerEmail.text.toString()
            val username = registerUsername.text.toString()
            val cnfPass = cnfRegisterPassword.text.toString()
            val pass = registerPassword.text.toString()

            if (email.isEmpty() || username.isEmpty() || cnfPass.isEmpty()
                || pass.isEmpty()
            ) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                registerEmail.error = "Invalid Email"
            } else if (pass != cnfPass) {
                Log.i("pass", "onCreateView: ${pass} ${cnfPass} ")
                registerPassword.text.clear()
                cnfRegisterPassword.text.clear()
                Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show()
            } else {
                val userRegistrationData: RegistrationModel = RegistrationModel(
                    registerPassword.text.toString(),
                    registerEmail.text.toString(),
                    registerUsername.text.toString(),
                )
                initData()
                registerUser(userRegistrationData)
            }
        }

        return v;
    }

    private fun initData() {

        /*Creating the instance of retrofit */
        retrofit = RetrofitClass.getInstance()

        /*Get the reference of Api interface*/
        urlEndpoints = retrofit.create(UrlEndpoints::class.java)
    }

    private fun registerUser(userData: RegistrationModel) {
        if(NetworkCheck.isConnected(context))
        {
            urlEndpoints.createUser(RegistrationModel(userData.password, userData.email, userData.username))
                .enqueue(object : Callback<RegistrationModel> {
                    override fun onResponse(
                        call: Call<RegistrationModel>,
                        response: Response<RegistrationModel>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()
                            activity?.supportFragmentManager
                                ?.beginTransaction()
                                ?.replace(R.id.authFrame, LoginFragment())
                                ?.addToBackStack(null)
                                ?.commit()

                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<RegistrationModel>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                })
        }
        else
        {
            Toast.makeText(context, "Please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }
}