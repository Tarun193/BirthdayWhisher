package com.example.birthdaywhisher

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.birthdaywhisher.databinding.FragmentLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment: Fragment(){
    private var _binding: FragmentLoginBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.SignupLink.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_login_to_fragment_signup);
        }

        auth = Firebase.auth;

        binding.button.setOnClickListener{
            val email = binding.editEmail.text.toString();
            val password = binding.editPassword.text.toString();

            activity?.let { act ->
                if (act is Activity) {
                    if(!checkEmpty()){
                        auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(act){task ->
                            if(task.isSuccessful){
                                val user = auth.currentUser;
                                Toast.makeText(act, "Logged in successfully!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.i("Firebase", "Some Error occurred", task.exception)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkEmpty(): Boolean {
        var result = false;
        if(binding.editEmail.text.toString().trim().isEmpty()){
            binding.editEmail.error = "Email  is required";
            result = true;
        }
        if(binding.editPassword.text.toString().trim().isEmpty()){
            binding.editPassword.error = "Password  is required";
            result = true;
        }
        return result;
    }
}