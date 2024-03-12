package tc.tcapps.birthday_wisher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import tc.tcapps.birthday_wisher.databinding.ActivityHomeBinding
import tc.tcapps.birthday_wisher.ui.components.BottomAppBar
import tc.tcapps.birthday_wisher.viewModles.UserViewModel

class activity_home: AppCompatActivity() {

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
    }

    private fun changeActivity(act: Activity) {
        val intent = Intent(act, activity_signup_login::class.java);
        startActivity(intent);

    }

}