package tc.tcapps.birthday_wisher

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import tc.tcapps.birthday_wisher.databinding.ActivityHomeBinding
import tc.tcapps.birthday_wisher.ui.components.BottomAppBar
import tc.tcapps.birthday_wisher.viewModles.UserViewModel


class activity_home: AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            Toast.makeText(this, "You will not get birthday notifications", Toast.LENGTH_SHORT).show();
        }
    }


    private lateinit var binding: ActivityHomeBinding;
    private val userViewModel by viewModels<UserViewModel>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root);

        userViewModel.userId.observe(this, Observer { userId ->
            if (userId == null) {
                changeActivity(this);
                this.finish();
            }
        });

        binding.bottomBar.setContent {
            BottomAppBar(
                onHomeClick = {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment);
                },
                onAddClick = {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.addContactFragment);

                }
            );
        }
        askNotificationPermission();
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun changeActivity(act: Activity) {
        val intent = Intent(act, activity_signup_login::class.java);
        startActivity(intent);

    }

}