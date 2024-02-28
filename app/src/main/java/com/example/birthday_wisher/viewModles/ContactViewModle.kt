package com.example.birthday_wisher.viewModles

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch


class ContactsViewModel : ViewModel() {
        var contacts = mutableStateListOf<Map<String, Any>>()
            private set

    private var auth = Firebase.auth;
    private var db = Firebase.firestore;
    var user = auth.currentUser;

        fun fetchContacts() {
            // Placeholder for fetch logic
            viewModelScope.launch {
                // Simulate fetching data
                user?.uid.let{id ->
                    db.collection("Users").document(id!!).get().addOnSuccessListener { document ->
                        if(document != null){
                            val contactsData = document.data?.get("contacts") as List<DocumentReference>;
                            contacts.clear();

                            for (contact in contactsData) {
                                contact.get().addOnSuccessListener { document ->
                                    if (document != null) {
                                        Log.i("Firebase", "document: ${document.data}");
                                        document.data?.let {
                                            contacts.add(it);
                                        }
                                    } else {
                                        Log.i("Firebase", "document: not found");
                                    }
                                }.addOnFailureListener {
                                    Log.i("Firebase", "An error occurred", it);
                                }
                            }
                        }else{
                            Log.i("Firebase", "document: not found");
                        }
                    }.addOnFailureListener{
                        Log.i("Firebase", "An error occurred", it);
                    }

                }
            }
        }
    }
