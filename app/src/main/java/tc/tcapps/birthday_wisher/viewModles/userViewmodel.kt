package tc.tcapps.birthday_wisher.viewModles

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

// ViewModel class managing user data and authentication processes
class UserViewModel : ViewModel() {
    // LiveData for observing changes in the user's ID across the app components
    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    // Firebase Authentication and Firestore instances for handling user data and authentication
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore;

    private val TAG = "UserViewModel"; // Tag used for logging

    // Method to update the current user ID value stored in LiveData
    fun setUserId(userId: String?) {
        _userId.value = userId
    }
    // Initialize the ViewModel by setting the current user's ID
    init {
        _userId.value = auth.currentUser?.uid
    }

    // Authenticates a user with email and password, updates the user ID on success
    fun loginWithEmailAndPassword(email: String, password: String, act: Activity){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(act){ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    _userId.value = user?.uid
                    addFCMTokenToUser() // Updates the Firebase Cloud Messaging (FCM) token for the user
                } else {
                    Log.i("Firebase", "Some Error occurred", task.exception)
                }
            }
    }

    // Signs in a user with Google account credentials, adds user to Firestore, and updates FCM token
    fun signInWithGoogleAccount(idToken: String, act: Activity){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(act){ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    _userId.value = user?.uid
                    addUserToCollection(user, user?.displayName) // Adds the user to Firestore with display name
                    addFCMTokenToUser() // Updates FCM token
                } else {
                    Log.i("Firebase", "Some Error occurred", task.exception)
                }
            }
    }

    // Registers a new user with email and password, adds the user to Firestore, and updates FCM token
    fun signUpWithEmailAndPassword(email: String, password: String, name: String, act: Activity){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(act){ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    _userId.value = user?.uid
                    addUserToCollection(user, name) // Adds the new user to Firestore with the specified name
                    addFCMTokenToUser() // Updates FCM token
                } else {
                    Log.i("Firebase", "Some Error occurred", task.exception)
                }
            }
    }

    // Adds or updates the given user in the Firestore "Users" collection
    private fun addUserToCollection(user: FirebaseUser?, name: String?) {
        val userMap = hashMapOf(
            "email" to user?.email,
            "name" to name
        )
        user?.uid?.let {
            db.collection("Users").document(it).get().addOnSuccessListener { doc ->
                if(!doc.exists()){
                    db.collection("Users").document(it).set(userMap)
                        .addOnSuccessListener {
                            Log.d("Firebase", "successfully account created")
                        }.addOnFailureListener { e ->
                            Log.w("Firebase", "Error writing document", e)
                        }
                }
            }
        }
    }

    // Logs out the current user and clears the stored user ID
    fun Logout(){
        auth.signOut()
        _userId.value = null
    }

    // Updates the user's FCM token in Firestore
    private fun addFCMTokenToUser() {
//        Fetches the FCM token and updates it in Firestore
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
//            Update the FCM token in Firestore

            val token = task.result // Get the new FCM token
//            Update the FCM token in Firestore
            auth.currentUser?.uid?.let {
                db.collection("Users").document(it).update("fcmToken", token)
                    .addOnSuccessListener {
                        Log.d(TAG, "FCM Token updated in Firestore")
                    }.addOnFailureListener {
                        Log.w(TAG, "Error updating FCM Token in Firestore", it)
                    }
            }
        }
    }
}
