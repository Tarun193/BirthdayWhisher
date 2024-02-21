package com.example.birthday_wisher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.birthday_wisher.databinding.ActivityHomeBinding

class activity_home: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }
}