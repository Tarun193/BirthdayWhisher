package com.example.birthday_wisher.viewModles

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch


class ContactsViewModel : ViewModel() {
    var contacts = mutableStateListOf<Map<String, Any>>()
        private set

    private var auth = Firebase.auth;
    private var db = Firebase.firestore;
    var user = auth.currentUser;

    init{
        fetchContacts();
    }

    private fun fetchContacts() {
        // Placeholder for fetch logic
        viewModelScope.launch {
            // Simulate fetching data
            Log.i("FirebaseTest", "${auth.currentUser?.uid}")
            user?.uid.let { id ->
                db.collection("Users").document(id!!).get().addOnSuccessListener { document ->
                    if (document != null) {
                        val contactsRaw = document.data?.get("contacts");
                        val contactsData: List<DocumentReference> = when (contactsRaw) {
                            is List<*> -> contactsRaw.filterIsInstance<DocumentReference>()
                            else -> emptyList() // Handle the case where contactsRaw is not a list or is null
                        }
                        contacts.clear();
                        Log.i("LiveData", "fetching contacts");
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
                    } else {
                        Log.i("Firebase", "document: not found");
                    }
                }.addOnFailureListener {
                    Log.i("Firebase", "An error occurred", it);
                }

            }
        }
    }

    fun addContact(contact: Map<String, Any>) {
        viewModelScope.launch {
            Log.i("Firebase", "${auth.currentUser?.uid}")
            db.collection("Contacts").add(contact).addOnSuccessListener { docRef ->
                val userRef = db.collection("Users").document(auth.currentUser!!.uid)
                userRef.update("contacts", FieldValue.arrayUnion(docRef))
                    .addOnSuccessListener {
                        Log.i("Firebase", "Contact reference added to user successfully");
                        contacts.add(contact);
                        Log.i("LiveData", contacts.size.toString());
                    }
                    .addOnFailureListener { e ->
                        Log.i("Firebase", "Error adding contact reference to user", e)
                    }
            }.addOnFailureListener {
                Log.i("Firebase", "Error occurred in contacts", it);
            }
        }
    }

    fun contactsExist(): Boolean {
        return contacts.isNotEmpty();
    }
}
