package com.example.birthdaywhisher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.birthdaywhisher.databinding.ActivitySignupLoginBinding

class activity_signup_login : AppCompatActivity() {
    private lateinit var binding: ActivitySignupLoginBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)

    }
}