package tc.tcapps.birthday_wisher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import tc.tcapps.birthday_wisher.databinding.ActivitySignupLoginBinding
import tc.tcapps.birthday_wisher.viewModles.UserViewModel

// This is the activity for signup and login
class activity_signup_login : AppCompatActivity() {
    // Declare a binding variable for the activity
    private lateinit var binding: ActivitySignupLoginBinding;
    // Declare a ViewModel variable
    private val userViewModel by viewModels<UserViewModel>();

    // This function is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this activity using ViewBinding
        binding = ActivitySignupLoginBinding.inflate(layoutInflater);
        // Set the inflated layout as the content view for the activity
        setContentView(binding.root)

        // Observe changes in the userId property of the ViewModel
        userViewModel.userId.observe(this, Observer { userId ->
            // If userId is not null, it means the user is logged in
            // So, we change the activity to the home activity
            userId?.let {
                changeActivity(this);
            }
        })
    }

    // This function is used to change the current activity to the home activity
    private fun changeActivity(act: Activity){
        // Create an intent for the home activity
        val intent  = Intent(act, activity_home::class.java);
        // Start the home activity
        startActivity(intent);
        // Finish the current activity (signup/login activity)
        act.finish();
    }
}
