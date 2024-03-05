package com.example.birthday_wisher

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.birthday_wisher.databinding.FragmentHomeBinding
import com.example.birthday_wisher.ui.components.CustomListItem
import com.example.birthday_wisher.ui.components.MyAppBar
import com.example.birthday_wisher.viewModles.ContactsViewModel
import com.example.birthday_wisher.viewModles.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

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
            updateUI(contacts);
        }


    }
    @Composable
    fun updateUI(contacts: List<Map<String, Any>>) {
        CustomListItem(contacts);

    }

}
