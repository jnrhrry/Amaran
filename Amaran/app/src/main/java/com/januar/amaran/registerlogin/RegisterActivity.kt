package com.januar.amaran.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.januar.amaran.messages.LatestMessageActivity
import com.januar.amaran.R
import com.januar.amaran.models.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buRegister.setOnClickListener {
            performRegister()
        }

        tvHaveAccount.setOnClickListener {
            //Launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        buSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri:Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data


            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            ivSelectImage.setImageBitmap(bitmap)
            buSelectImage.alpha = 0f
            // val bitmapDrawable = BitmapDrawable(bitmap)
            // buSelectImage.setBackgroundDrawable(bitmapDrawable)


        }
    }

    fun performRegister(){

        val email = etEmail_register.text.toString()
        val password = etPassword_register.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email address and password.",
                Toast.LENGTH_LONG).show()
            return}

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if success
                Log.d("RegisterActivity", "Successfully created a new account with uid :${it.result!!.user!!.uid}")
                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create an account user : ${it.message}.")
                Toast.makeText(this, "Failed to create an account user : ${it.message}",
                    Toast.LENGTH_LONG).show()
            }

    }
    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("RegisterActivity", "Success upload image: ${it.metadata!!.path}")

            ref.downloadUrl.addOnSuccessListener {
                Log.d("RegisterActivity", "File Location: $it")


                saveUserToFirebaseDatabase(it.toString())
            }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to upload tje image to storage: ${it.message}")
                }
        }


    }

    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid!!,
            etUsername_register.text.toString(),
            profileImageUrl
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "We did it to save user to Firebase database.")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to set value to database: ${it.message}")
            }
    }
}

