package com.example.birthday_wisher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.wear.compose.material.ContentAlpha
import com.example.birthday_wisher.databinding.FragmentHomeBinding
import com.example.birthday_wisher.viewModles.ContactsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null;

    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;

    private lateinit var contacts:List<Map<String, Any>>;

    private val contactsViewModel by activityViewModels<ContactsViewModel>();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.root;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        binding.button2.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addContactFragment);
        }

        binding.composeView.setContent {
            if(contactsViewModel.contacts.isEmpty()){
                contactsViewModel.fetchContacts();
            }
            contacts = contactsViewModel.contacts;
            updateUI(contacts);
        }

}
    @Composable
    fun updateUI(contacts: List<Map<String, Any>>) {
        CustomListItem(contacts);

    }


    @Composable
    fun CustomListItem(contacts:List<Map<String, Any>>) {
        val scrollState = rememberScrollState();
            Column(modifier = Modifier.verticalScroll(scrollState) ){
                for(contact in contacts){
                ListItem(
                    headlineContent = { Text(contact.get("name").toString()) },
                    supportingContent = { Text(DOBFormatter(contact.get("DOB").toString())) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "Localized description",
                            tint = Color.White,
                        )
                    },
                    colors = customListItemColors()
                )
            }
        }

    }

    @Composable
    fun customListItemColors(): ListItemColors {
        val textColor = Color.White
        val disabledColor = textColor.copy(alpha = ContentAlpha.disabled)
        val iconColor = LocalContentColor.current

        return ListItemColors(
            containerColor = Color.Transparent,
            headlineColor = textColor,
            overlineColor = textColor,
            supportingTextColor = textColor,
            leadingIconColor = iconColor,
            trailingIconColor = iconColor,
            disabledHeadlineColor = disabledColor,
            disabledLeadingIconColor = disabledColor,
            disabledTrailingIconColor = disabledColor
        )
    }

    private fun DOBFormatter(DOB: String): String{
        val date = DOB.split("-");
        when(date[1]){
            "1" -> return date[0] + " January";
            "2" -> return date[0] + " February";
            "3" -> return date[0] + " March";
            "4" -> return date[0] + " April";
            "5" -> return date[0] + " May";
            "6" -> return date[0] + " June";
            "7" -> return date[0] + " July";
            "8" -> return date[0] + " August";
            "9" -> return date[0] + " September";
            "10" -> return date[0] + " October";
            "11" -> return date[0] + " November";
            "12" -> return date[0] + " December";
            else -> return "Invalid Date";
        }
    }
}