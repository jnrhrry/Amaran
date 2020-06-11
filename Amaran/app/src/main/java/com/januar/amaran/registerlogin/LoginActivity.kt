package com.januar.amaran.registerlogin


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.januar.amaran.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etPassword_login

class LoginActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        buLogin.setOnClickListener {
            performLogin()
                }

        tvToRegister.setOnClickListener{
            finish()
        }
    }

    private fun performLogin(){

        val email = etEmail_login.text.toString()
        val password = etPassword_login.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email address and password.",
                Toast.LENGTH_LONG).show()
            return}

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if success
                Log.d("Login", "Welcome back, ${it.result!!.user!!.uid}!")
                Toast.makeText(this, "Welcome back, ${it.result!!.user!!.uid}!",
                    Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener {
                Log.d("Login", "Make sure to check your email and password is correct ${it.message}")
                Toast.makeText(this, "Make sure to check your email and password is correct ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
    }
}