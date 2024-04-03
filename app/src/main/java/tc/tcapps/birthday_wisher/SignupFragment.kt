package tc.tcapps.birthday_wisher

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
import tc.tcapps.birthday_wisher.databinding.FragmentSignupBinding
import tc.tcapps.birthday_wisher.ui.components.MyAppBar
import tc.tcapps.birthday_wisher.viewModles.UserViewModel


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;
    private lateinit var googleSignInClient: GoogleSignInClient;
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val userViewModel by activityViewModels<UserViewModel>();

    private val binding get() = _binding!!;

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth;
        super.onCreate(savedInstanceState)
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.i("Firebase", "${result.resultCode} ${Activity.RESULT_OK}");
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                } else {
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false);
        //        Grabbing firebase services instance
        auth = Firebase.auth;
        db = Firebase.firestore

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState);

        binding.topBar.setContent {
            MyAppBar("Sign Up", isLoggedIn = false, logoutClick = {});
        }

        if(auth.currentUser != null){
            userViewModel.setUserId(auth.currentUser?.uid);
        }

        binding.LoginLink.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_signup_to_fragment_login);
        }

        googleSignInClient = getGoogleSignInClient()

        binding.Google.setOnClickListener {
            signInWithGoogle()
        }

        binding.button.setOnClickListener{

//            Grabbing all the values from the form;
            val email = binding.editEmail.text.toString();
            val password1 = binding.editPassword1.text.toString();
            val password2 = binding.editPassword2.text.toString();
            val name = binding.editName.text.toString();

            var PasswordStrong = true;

            if(checkPassword()){
                Toast.makeText(activity, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                PasswordStrong = false;
            }


//            Checking weather all the fields are filled or not.
            if(!checkEmpty()) {
                // Checking whether both passwords are equal or not.
                if (password2 == password1) {
//                  creating user by calling createUserWithEmailAndPassword method on auth instance
                    /*                    it is an async method so we are adding a listener which will call  lambda
                                        function with task as an argument when the task is completed
                     */

                    activity?.let { act ->
                        if (act is Activity && PasswordStrong) {
                            userViewModel.signUpWithEmailAndPassword(email, password1, name, act);
                            Toast.makeText(act, "User created successfully!!", Toast.LENGTH_SHORT).show();
                            clearText();
                        }
                    }
                }
//                Showing required message when passwords are different using Toast and error
                else {
                    binding.editPassword2.error = "passwords doesn't matching";
                    Toast.makeText(activity, "Passwords are not matching", Toast.LENGTH_SHORT).show()
                }

            }
//            Showing appropriate message using toast to fill all the required fields.
            else{
                Toast.makeText(activity, "Please fill all the required fields", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun checkEmpty(): Boolean {
        var result = false;
        //            Checking if any of the field from the form is empty adding an error to that field

        if(binding.editName.text.toString().trim().isEmpty()){
            binding.editName.error = "Name required";
            result = true;
        }

        if(binding.editEmail.text.toString().trim().isEmpty()){
            binding.editEmail.error = "Email required";
            result = true;
        }

        if(binding.editPassword1.text.toString().trim().isEmpty()) {
            binding.editPassword1.error = "password required";
            result = true;
        }

        if(binding.editPassword2.text.toString().trim().isEmpty()){
            binding.editPassword2.error = "password confirmation required";
            result = true;
        }
        return result;
    }


    private fun clearText(){
        binding.editName.text?.clear();
        binding.editEmail.text?.clear();
        binding.editPassword2.text?.clear();
        binding.editPassword1.text?.clear();
    }

    private fun checkPassword(): Boolean {
        return binding.editPassword1.text.toString().length < 6;
    }

    private fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Make sure you have the correct web client id
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso)
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // You can now use the account's information to perform further authentication steps
            userViewModel.signInWithGoogleAccount(account.idToken!!, activity as Activity)
        } catch (e: ApiException) {
            // Handle sign-in failure
            Log.w("Firebase", "Google sign in failed", e)
        }
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

}
