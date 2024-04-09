package tc.tcapps.birthday_wisher;

import android.app.Activity
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import tc.tcapps.birthday_wisher.databinding.FragmentAddContactBinding
import tc.tcapps.birthday_wisher.ui.components.MyAppBar
import tc.tcapps.birthday_wisher.viewModles.ContactsViewModel
import tc.tcapps.birthday_wisher.viewModles.UserViewModel


public class AddContactFragment: Fragment(){
    // Declare variables for binding, activity, authentication, and Firestore
    private var _binding: FragmentAddContactBinding? = null;
    private lateinit var act: Activity;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;

    // Declare variables for contact details
    private lateinit var relationType: String;
    private lateinit var name: String;
    private lateinit var phone: String;
    private lateinit var wish: String;
    private lateinit var DateOfBirth: String;

    // Declare a spinner for the relationship type
    private lateinit var spinner: Spinner;

    // Creating an  ViewModels object for Contacts and User
    private val contactsViewModel by activityViewModels<ContactsViewModel>();
    private val userViewModel by activityViewModels<UserViewModel>();

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false);
        auth = Firebase.auth;
        activity?.let{
            if(it is Activity){
                act = it;
            }
        }

        return binding.root;
    }


//    setup the view and the listeners once the view is created.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

//        set the top bar content to the app bar with the title "Add Contact"
//        and the logout button on the right side of the app bar.
        binding.TopBar.setContent {
            MyAppBar("Add Contact", isLoggedIn = true, logoutClick = {
                userViewModel.Logout();
                act.finish();
            });
        }

        spinner =  binding.relationSpinner;
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            act,
            R.array.relation_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        binding.editDate.setOnClickListener{
            // on below line we are getting  
            // the instance of our calendar. 
            val c = Calendar.getInstance()

            // on below line we are getting 
            // our day, month and year. 
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a  
            // variable for date picker dialog. 
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context. 
                act,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting 
                    // date to our text view.
                    binding.textDate.text =
                        (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                },
                // on below line we are passing year, month 
                // and day for the selected date in our date picker. 
                year,
                month,
                day
            )
            // at last we are calling show  
            // to display our date picker dialog. 
            datePickerDialog.show()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item is selected. You can retrieve the selected item using
                relationType = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.i("Spinner","Chill");
            }

        }

//        set the click listener for the add contact button
        binding.addContactButton.setOnClickListener{
            name = binding.editName.text.toString();
            phone = binding.editPhone.text.toString();
            wish = binding.editMessage.text.toString();
            DateOfBirth = binding.textDate.text.toString();

//            check if all the inputs are valid
            if(checkAllInputs()){
//                add the contact to the database
                contactsViewModel.addContact(hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "relationType" to relationType,
                    "message" to wish,
                    "DOB" to DateOfBirth,
                    "user" to auth.currentUser?.uid!!
                )
                )
//                clear the inputs and navigate to the home fragment
                clearInputs();
//                Navigate to the home fragment
                findNavController().navigate(R.id.action_addContactFragment_to_homeFragment);
            }
        }

    }


//    method checks if all the inputs are valid
    private fun checkAllInputs(): Boolean{
        var result = true;
        if(binding.editName.text.toString().trim().isEmpty()){
            binding.editName.error = "Name is required";
            result = false;
        }

        if(binding.editPhone.text.toString().trim().isEmpty()){
            binding.editName.error = "Phone number is required";
            result = false;
        }

        if(relationType == "Relationship Type"){
            Toast.makeText(act, "Please select the relationship type", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if(DateOfBirth == "Select Date"){
            Toast.makeText(act, "Please select the date of birth", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }

//    method to clear all the inputs.

    private fun clearInputs(){
        binding.editName.text?.clear();
        binding.editPhone.text?.clear();
        binding.editMessage.text?.clear();
        spinner.setSelection(0);
        binding.textDate.text = "Select Date";
    }

}
