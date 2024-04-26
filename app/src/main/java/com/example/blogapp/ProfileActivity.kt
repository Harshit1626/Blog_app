package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blogapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private val binding:ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}