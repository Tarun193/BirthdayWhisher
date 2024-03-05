package com.example.birthday_wisher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.birthday_wisher.databinding.ActivityHomeBinding
import com.example.birthday_wisher.ui.components.BottomAppBar

class activity_home: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.bottomBar.setContent {
            BottomAppBar(
                onHomeClick = {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment);
                },
                onAddClick = {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.addContactFragment);

                }
            );
        }
    }
}