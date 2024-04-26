package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.databinding.ActivityReadMoreBinding
import com.example.blogapp.model.BlogItemModel

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }

        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")

        if (blogs != null) {
            //retrive user related data.. Eg blog title, Date
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.userName
            binding.date.text = blogs.date
            binding.blogDescriptionBlogView.text = blogs.post
            val userImageUrl = blogs.profileImage
            Glide.with(this).load(userImageUrl).apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)

        } else {
            Toast.makeText(this, "Failed To load Blog", Toast.LENGTH_SHORT).show()
        }

    }
}

