package com.example.birthday_wisher.viewModles

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale


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
        viewModelScope.launch {
            user?.uid?.let { id ->
                val document = db.collection("Users").document(id).get().await() // Await the document
                if (document != null) {
                    val contactsRaw = document.data?.get("contacts")
                    val contactsData: List<DocumentReference> = when (contactsRaw) {
                        is List<*> -> contactsRaw.filterIsInstance<DocumentReference>()
                        else -> emptyList()
                    }

                    // Fetch contacts concurrently and convert them to Contact instances
                    val fetchJobs = contactsData.map { contactRef ->
                        async {
                            val contactDoc = contactRef.get().await() // Await each contact document
                            if (contactDoc.exists()) {
                                contactDoc.data?.let { contacts.add(it) }
                            }
                        }
                    }
                    fetchJobs.awaitAll() // Wait for all fetch operations to complete

                    // Now that all contacts have been fetched, sort them based on your criteria
                    sortContactsByMonthAndDay();

                    // Use fetchedContacts as needed, now fully fetched and sorted
                } else {
                    Log.i("Firebase", "Document not found")
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


    private fun sortContactsByMonthAndDay() {
        val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.US)

        contacts.sortWith { a, b ->
            // Parse the DOB strings into Date objects
            val aDOB = dateFormat.parse(a["DOB"].toString())
            val bDOB = dateFormat.parse(b["DOB"].toString())

            // Extract month and day from aDOB and bDOB
            val calendarA = Calendar.getInstance().apply { time = aDOB }
            val calendarB = Calendar.getInstance().apply { time = bDOB }

            // Compare first by month, then by day
            val monthComparison = calendarA.get(Calendar.MONTH).compareTo(calendarB.get(Calendar.MONTH))
            if (monthComparison != 0) {
                monthComparison
            } else {
                calendarA.get(Calendar.DAY_OF_MONTH).compareTo(calendarB.get(Calendar.DAY_OF_MONTH))
            }
        }
    }

}
