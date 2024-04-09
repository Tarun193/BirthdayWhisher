package tc.tcapps.birthday_wisher

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import tc.tcapps.birthday_wisher.databinding.FragmentHomeBinding
import tc.tcapps.birthday_wisher.ui.components.CustomListItem
import tc.tcapps.birthday_wisher.ui.components.MyAppBar
import tc.tcapps.birthday_wisher.viewModles.ContactsViewModel
import tc.tcapps.birthday_wisher.viewModles.UserViewModel

class HomeFragment: Fragment() {
//    // Declare variables for binding, activity, authentication.
    private var _binding: FragmentHomeBinding? = null;
    private lateinit var act: Activity;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;

//   Creating an  ViewModels object for Contacts and User
    private val contactsViewModel by activityViewModels<ContactsViewModel>();
    private val userViewModel by activityViewModels<UserViewModel>();

//    Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
//       Initialize Firebase authentication
        auth = Firebase.auth;
//    Get the activity
        activity?.let{
            if(it is Activity){
                act = it;
            }
        }

        return binding.root;
    }


//    This function is called when the fragment is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

//       Set the content for the top bar and logout button with it's click listener
        binding.TopBar.setContent {
            MyAppBar("Home", isLoggedIn = true, logoutClick = {
                userViewModel.Logout();
            });
        }

//    Set the content for the compose view
        binding.composeView.setContent {
//            Get the contacts from the ViewModel
            val contacts = contactsViewModel.contacts;
//            Call the updateUI function to display the contacts anf pass the onClick function
//            which will navigate to the updateDeleteFragment of specific contact.
            updateUI(contacts, onClick = {
                findNavController().navigate(R.id.action_homeFragment_to_updateDeleteFragment)
            });
        }
    }

//    This function is used to update the UI with the contacts.
    @Composable
    fun updateUI(contacts: List<Map<String, Any>>, onClick: () -> Unit) {
        CustomListItem(contacts, onClick = onClick, contactsViewModel);

    }

}
