package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {
    private val binding: ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-53b3f-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-53b3f-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imageButton2.setOnClickListener {
            finish()
        }


        binding.addBlogButton.setOnClickListener {

            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all the Fields", Toast.LENGTH_SHORT).show()
            }
            //getcurrentuser


            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val userName = user.displayName ?: "Anonymous"
                val userImageUrl = user.photoUrl ?: " "

                //Fetch user name and user Profile from database

                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                val userNameFromDB = userData.name
                                val userImageUrlFromDB = userData.profileImage

                                val currentDate = SimpleDateFormat("yyyy-MM-DD").format(Date())
                                // create a blog item model
                                val blogItem = BlogItemModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB
                                )
                                //genrate unique for blog post
                                val key = databaseReference.push().key
                                if (key != null) {
                                    blogItem.postId = key
                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@AddArticleActivity,
                                                "Failed to add blog",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                                }

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
        }
    }
}