package com.example.birthday_wisher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.birthday_wisher.databinding.FragmentLoginBinding
import com.example.birthday_wisher.ui.components.MyAppBar
import com.example.birthday_wisher.viewModles.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class LoginFragment: Fragment(){
    private var _binding: FragmentLoginBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;
    private lateinit var googleSignInClient: GoogleSignInClient;
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val userViewModel by activityViewModels<UserViewModel>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        auth = Firebase.auth;
        db = Firebase.firestore;

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data);
                handleSignInResult(task);
            }else {
                // Handle cancellation or error
                if (result.data != null) {
                    // Attempt to retrieve any error information from the intent
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                } else {
                    // No data available; the user likely cancelled the sign-in
                    Log.i("Firebase", "Sign-in cancelled or an error occurred.")
                }
            }

        }
    }

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

        binding.topBar.setContent {
            MyAppBar("Login", isLoggedIn = false, logoutClick = {});
        }


        googleSignInClient  = getGoogleSignInClient();
        binding.Google.setOnClickListener(){
            signInWithGoogle();
        }

        binding.button.setOnClickListener{
            val email = binding.editEmail.text.toString();
            val password = binding.editPassword.text.toString();

            activity?.let { act ->
                if (act is Activity) {
                    if(!checkEmpty()){
                        userViewModel.loginWithEmailAndPassword(email, password, act);
                        Toast.makeText(act, "Logged in successfully!!", Toast.LENGTH_SHORT).show();
                        clearText();
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

    private fun clearText(){
        binding.editEmail.text.clear();
        binding.editPassword.text.clear();
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // You can now use the account's information to perform further authentication steps
            userViewModel.signInWithGoogleAccount(account.idToken!!, activity as Activity);
        } catch (e: ApiException) {
            // Handle sign-in failure
            Log.w("Firebase", "Google sign in failed", e)
        }
    }


    private fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Make sure you have the correct web client id
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso)
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}