package com.example.birthdaywhisher

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.birthdaywhisher.databinding.ActivityHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class activity_home: Activity() {

    private lateinit var binding: ActivityHomeBinding;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root);

        auth = Firebase.auth;
        db = Firebase.firestore;

        val user = auth.currentUser;

        user?.uid.let{id ->
            db.collection("Users").document(id!!).get().addOnSuccessListener { document ->
                if(document != null){
                   Toast.makeText(this,  "Welcome, " + document.data?.get("name").toString(), Toast.LENGTH_SHORT).show();
            }else{
                    Log.i("Firebase", "document: not found");
                }
            }.addOnFailureListener{
                Log.i("Firebase", "An error occurred", it);
            }

        }
    }
}