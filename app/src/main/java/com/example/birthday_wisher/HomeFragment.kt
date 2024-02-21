package com.example.birthday_wisher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.birthday_wisher.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null;

    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button2.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addContactFragment);
        }

        auth = Firebase.auth;
        db = Firebase.firestore;

        val user = auth.currentUser;

        user?.uid.let{id ->
            db.collection("Users").document(id!!).get().addOnSuccessListener { document ->
                if(document != null){
//                   Toast.makeText(this,  "Welcome, " + document.data?.get("name").toString(), Toast.LENGTH_SHORT).show();
                    binding.editWelcome.text = "Hello ${document.data?.get("name").toString()}";
                }else{
                    Log.i("Firebase", "document: not found");
                }
            }.addOnFailureListener{
                Log.i("Firebase", "An error occurred", it);
            }

        }
    }
}