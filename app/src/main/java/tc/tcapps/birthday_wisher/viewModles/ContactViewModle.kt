package tc.tcapps.birthday_wisher.viewModles

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
import com.google.firebase.functions.functions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale


class ContactsViewModel : ViewModel() {
    // Mutable list of contacts, publicly readable but privately modifiable
    var contacts = mutableStateListOf<Map<String, Any>>()
        private set

    // Firebase authentication and Firestore database references
    private var auth = Firebase.auth;
    private var db = Firebase.firestore;
    private var user = auth.currentUser; // Current signed-in user
    private var functions = Firebase.functions; // Firebase Cloud Functions reference

    private val TAG = "ContactsViewModel"; // Tag for logging

    // Variable to hold a contact that is selected for updating
    var contactTobeUpdated: Map<String, Any>? = null
        private set

    // Initializer block that fetches contacts as soon as the ViewModel is created
    init{
        fetchContacts();
    }

    // Private function to fetch contacts from Firestore based on the current user's ID
    private fun fetchContacts() {
        viewModelScope.launch {
            user?.uid?.let { id ->
                // Asynchronously retrieve the document for the current user
                val document = db.collection("Users").document(id).get().await()
                if (document != null) {
                    // Extract the 'contacts' field from the user document
                    val contactsRaw = document.data?.get("contacts")
                    val contactsData: List<DocumentReference> = when (contactsRaw) {
                        // Ensure the contacts data is a list of DocumentReferences
                        is List<*> -> contactsRaw.filterIsInstance<DocumentReference>()
                        else -> emptyList()
                    }

                    // Clear existing contacts and fetch new ones concurrently
                    contacts.clear();
                    val fetchJobs = contactsData.map { contactRef ->
                        async {
                            // For each contact reference, fetch the document data
                            val contactDoc = contactRef.get().await()
                            if (contactDoc.exists()) {
                                // Add each fetched contact to the 'contacts' list with its document ID
                                contactDoc.data?.let {
                                    it["id"] = contactDoc.id;
                                    contacts.add(it)
                                }
                            }
                        }
                    }
                    fetchJobs.awaitAll() // Wait for all asynchronous fetches to complete

                    // Once contacts are fetched, sort them (implementation not shown here)
                    sortContactsByMonthAndDay();
                } else {
                    Log.i("Firebase", "Document not found")
                }
            }
        }
    }

    // Public function to add a new contact to Firestore and update the UI
    fun addContact(contact: Map<String, Any>) {
        viewModelScope.launch {
            // Add the contact to the "Contacts" collection
            db.collection("Contacts").add(contact).addOnSuccessListener { docRef ->
                // On success, update the user's 'contacts' field with the new contact reference
                val userRef = db.collection("Users").document(auth.currentUser!!.uid)
                userRef.update("contacts", FieldValue.arrayUnion(docRef))
                    .addOnSuccessListener {
                        // Fetch and sort contacts again after successful addition
                        fetchContacts();
                        sortContactsByMonthAndDay();
                        // Optionally, call a cloud function (not shown here)
                        callCloudFunctionToSendNotifications();
                    }
                    .addOnFailureListener { e ->
                        Log.i("Firebase", "Error adding contact reference to user", e)
                    }
            }.addOnFailureListener {
                Log.i("Firebase", "Error occurred in contacts", it);
            }
        }
    }

//    Function to check if contacts exist
    fun contactsExist(): Boolean {
        return contacts.isNotEmpty();
    }


//    Function to sort contacts by month and day
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

//    Function to set the contact to be updated
    fun setContactTobeUpdated(contact: Map<String, Any>) {
        contactTobeUpdated = contact;
    }


    fun updateContact(updateData: Map<String, Any>){
//        Update the contact in the database with the new data and update the UI
        db.collection("Contacts")
            .document(contactTobeUpdated?.get("id").toString())
            .update(updateData).addOnSuccessListener {
                Log.i("Updated", "DataUpdated")
                fetchContacts();
                sortContactsByMonthAndDay();
                callCloudFunctionToSendNotifications();
                contactTobeUpdated = null;
            }
    }

//    Function to delete a contact
    fun deleteContact(){
//        Get the reference of the contact to be deleted
        val contactRef =db.collection("Contacts").document(contactTobeUpdated?.get("id").toString());
//    Delete the contact reference from the contacts collection and update the user's contacts.
        contactRef.delete().addOnSuccessListener {
                db.collection("Users")
                    .document(user?.uid!!)
                    .update("contacts", FieldValue.arrayRemove(contactRef)).addOnSuccessListener {
                        Log.i("Updated", "DataDeleted")
                        fetchContacts();
                        sortContactsByMonthAndDay();
                        contactTobeUpdated = null;
                    }

            }
    }

//    Function to call the cloud function to send notifications to the contacts whose birthday is today
    private fun callCloudFunctionToSendNotifications(){
        // Call cloud function to send notifications
        functions.getHttpsCallable("sendBirthdayNotifications")
            .call()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Successfully sent notifications")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error sending notifications", e)
            }
    }
}
