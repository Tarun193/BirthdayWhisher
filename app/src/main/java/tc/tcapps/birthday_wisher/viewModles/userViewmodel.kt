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

class UserViewModel : ViewModel() {
    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId
    // Reference to Firebase Auth
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore;

    private val TAG = "UserViewModel";

    fun setUserId(userId: String?) {
        _userId.value = userId
    }

    init {
        _userId.value = auth.currentUser?.uid
    }

    fun loginWithEmailAndPassword(email: String, password: String, act: Activity){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(act){task ->
                if(task.isSuccessful){
                    val user = auth.currentUser;
                    _userId.value = user?.uid;
                    addFCMTokenToUser();
                }
                else{
                    Log.i("Firebase", "Some Error occurred", task.exception)
                }
            }
    }

    fun signInWithGoogleAccount(idToken: String, act: Activity){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(act){task ->
                if(task.isSuccessful){
                    val user = auth.currentUser;
                    _userId.value = user?.uid;
                    addUserToCollection(user, user?.displayName);
                    addFCMTokenToUser();
                }
                else{
                    Log.i("Firebase", "Some Error occurred", task.exception)
                }
            }
    }



//    Signup using email and Password;

    fun signUpWithEmailAndPassword(email: String, password: String, name: String, act: Activity){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(act){task ->
                if(task.isSuccessful){
                    val user = auth.currentUser;
                    _userId.value = user?.uid;
                    addUserToCollection(user, name);
                    addFCMTokenToUser();
                }
                else{
                    Log.i("Firebase", "Some Error occurred", task.exception)
                }
            }
    }



//    Utility function to add user to collection
    private fun addUserToCollection(user: FirebaseUser?, name: String?) {
//        Now adding that user to User collection
        val userMap = hashMapOf(
            "email" to user?.email,
            "name" to name,
        )
        user?.uid?.let {
            val docRef =  db.collection("Users").document(it);
            docRef.get().addOnSuccessListener { doc ->
                if(!doc.exists()){
                    docRef.set(userMap)
                        .addOnSuccessListener {
                            Log.d("Firebase", "successfully account created");
                        }.addOnFailureListener { e ->
                            Log.w("Firebase", "Error writing document", e)
                        }
                }
            }
        }
    }

    fun Logout(){
        auth.signOut();
        _userId.value = null;
    }

    private fun addFCMTokenToUser() {
        val user = auth.currentUser;
        user?.uid?.let {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                val userRef = db.collection("Users").document(it)

                // Update the token in Firestore
                userRef.update("fcmToken", token).addOnSuccessListener {
                    Log.d(TAG, "FCM Token updated in Firestore")
                }.addOnFailureListener{
                    Log.w(TAG, "Error updating FCM Token in Firestore", it)
                }
            }

        }
    }
}
