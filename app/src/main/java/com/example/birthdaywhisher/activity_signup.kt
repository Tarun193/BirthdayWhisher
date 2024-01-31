package com.example.birthdaywhisher

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.birthdaywhisher.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class activity_signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater);

//        Grabbing firebase services instance
        auth = Firebase.auth;
        db = Firebase.firestore

        setContentView(binding.root);

        binding.button.setOnClickListener{

//            Checking if any of the field from the form is empty adding an error to that field
            if(binding.editName.text.toString().trim().isEmpty()){
                binding.editName.error = "Name required";
            }

            if(binding.editEmail.text.toString().trim().isEmpty()){
                binding.editEmail.error = "Email required";
            }

            if(binding.editPassword1.text.toString().trim().isEmpty()){
                binding.editPassword1.error = "password required";
            }

            if(binding.editPassword2.text.toString().trim().isEmpty()){
                binding.editPassword2.error = "password confirmation required";
            }

//            Grabbing all the values from the form;
            var email = binding.editEmail.text.toString();
            var password1 = binding.editPassword1.text.toString();
            var password2 = binding.editPassword2.text.toString();
            var name = binding.editName.text.toString();

//            Checking weather all the fields are filled or not.
            if(!(email.isEmpty() || name.isEmpty() || password2.isEmpty() || password1.isEmpty())) {
                // Checking whether both passwords are equal or not.
                if (password2 == password1) {
//                  creating user by calling createUserWithEmailAndPassword method on auth instance
/*                    it is an async method so we are adding a listener which will call  lambda
                    function with task as an argument when the task is completed
 */
                    auth.createUserWithEmailAndPassword(email, password1)
                        .addOnCompleteListener(this) { task ->
//                            the task here store the information that creating user was a successful task or not.
                            if (task.isSuccessful) {
//                                Grabbing the current user as the user is signed in now.
                                val user = auth.currentUser;

//                                Now adding that user to User collection
                                val userMap = hashMapOf(
                                    "email" to user?.email,
                                    "name" to name,
                                )
                                user?.uid?.let {
                                    db.collection("Users").document(it).set(userMap)
                                        .addOnSuccessListener {
                                            Log.d("Firebase", "successfully account created");
                                        }.addOnFailureListener { e ->
                                        Log.w("Firebase", "Error writing document", e)
                                    }
                                }
                            }
                        }
                }
//                Showing required message when passwords are different using Toast and error
                else {
                    binding.editPassword2.error = "passwords doesn't matching";
                    Toast.makeText(this, "Passwords are not matching", Toast.LENGTH_SHORT).show()
                }

            }
//            Showing appropriate message using toast to fill all the required fields.
            else{
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()

            }
        }

    }
}