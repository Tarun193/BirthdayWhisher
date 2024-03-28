package tc.tcapps.birthday_wisher.utilities

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body} ${it.title}")
        }
    }

    override fun onNewToken(token: String) {
        // Handle new or refreshed FCM registration token
        Log.d(TAG, "Refreshed token: $token")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Assuming you have a method to get the current user's ID
            val userId = Firebase.auth.currentUser?.uid?.let{
                val userRef = Firebase.firestore.collection("users").document(it)
                userRef.update("fcmToken", token).addOnSuccessListener {
                    Log.d(TAG, "FCM Token updated in Firestore")
                }
            }
            }


    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}