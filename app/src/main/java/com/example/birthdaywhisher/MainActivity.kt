package com.example.birthdaywhisher

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.birthdaywhisher.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        val db = Firebase.firestore
        db.collection("Users").get().addOnSuccessListener {
            result -> for(document in result){
                Log.d("Firebase", "${document.id} => ${document.data}");
        }
        }.addOnFailureListener {
                exception ->
            Log.w("Firebase", "Error getting documents.", exception)
        }
        setContentView(binding.root);

        binding.button.setOnClickListener{
            var email = binding.editEmail.text.toString();
            var password = binding.editPassword.text.toString();

            Log.i("Test", "$email $password");
        }

    }
}