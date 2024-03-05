package com.example.birthday_wisher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.birthday_wisher.databinding.ActivitySignupLoginBinding
import com.example.birthday_wisher.viewModles.UserViewModel

class activity_signup_login : AppCompatActivity() {
    private lateinit var binding: ActivitySignupLoginBinding;
    private val userViewModel by viewModels<UserViewModel>();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)


        userViewModel.userId.observe(this, Observer { userId ->
            userId?.let {
                changeActivity(this);
            }
        })

    }

    private fun changeActivity(act: Activity){
        val intent  = Intent(act, activity_home::class.java);
        startActivity(intent);
        act.finish();
    }
}