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
    private var _binding: FragmentHomeBinding? = null;
    private lateinit var act: Activity;

    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;


    private val contactsViewModel by activityViewModels<ContactsViewModel>();
    private val userViewModel by activityViewModels<UserViewModel>();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        auth = Firebase.auth;
        activity?.let{
            if(it is Activity){
                act = it;
            }
        }

        return binding.root;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);


        binding.TopBar.setContent {
            MyAppBar("Home", isLoggedIn = true, logoutClick = {
                userViewModel.Logout();
            });
        }

        binding.composeView.setContent {
            val contacts = contactsViewModel.contacts;
            updateUI(contacts, onClick = {
                findNavController().navigate(R.id.action_homeFragment_to_updateDeleteFragment)
            });
        }


    }
    @Composable
    fun updateUI(contacts: List<Map<String, Any>>, onClick: () -> Unit) {
        CustomListItem(contacts, onClick = onClick, contactsViewModel);

    }

}
