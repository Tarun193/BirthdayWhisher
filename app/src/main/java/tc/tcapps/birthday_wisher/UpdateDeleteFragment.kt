package tc.tcapps.birthday_wisher

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
import tc.tcapps.birthday_wisher.databinding.FragmentUpdateDeleteBinding
import tc.tcapps.birthday_wisher.ui.components.MyAppBar
import tc.tcapps.birthday_wisher.viewModles.ContactsViewModel
import tc.tcapps.birthday_wisher.viewModles.UserViewModel

class UpdateDeleteFragment: Fragment()  {
    private var _binding: FragmentUpdateDeleteBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var spinner: Spinner;
    private lateinit var act: Activity;

    private val contactsViewModel by activityViewModels<ContactsViewModel>();
    private val userViewModel by activityViewModels<UserViewModel>();
    private lateinit var contact: Map<String, Any>


    private lateinit var relationType: String;
    private lateinit var name: String;
    private lateinit var phone: String;
    private lateinit var wish: String;
    private lateinit var DateOfBirth: String;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateDeleteBinding.inflate(inflater, container, false);
        contact = contactsViewModel.contactTobeUpdated!!;
        activity?.let {
            if(it is Activity){
                act = it;
            }
        }
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.TopBar.setContent {
            MyAppBar("Update Info", isLoggedIn = true, logoutClick = {
                userViewModel.Logout();
            });
        }
        spinner =  binding.relationSpinner;
        binding.editName.setText(contact.get("name").toString());
        binding.editMessage.setText(contact.get("message").toString())
        binding.textDate.setText(contact.get("DOB").toString());
        binding.editPhone.setText(contact.get("phone").toString());
        setSpinner();
        binding.editDate.setOnClickListener{
            showCalander();
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

        binding.updateContact.setOnClickListener{
            name = binding.editName.text.toString();
            DateOfBirth = binding.textDate.text.toString();
            wish = binding.editMessage.text.toString();
            phone = binding.editPhone.text.toString();

            if(checkAllInputs()){
                contactsViewModel.updateContact(hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "relationType" to relationType,
                    "message" to wish,
                    "DOB" to DateOfBirth,
                    "user" to userViewModel.userId.value.toString()
                )
                )
                findNavController().navigate(R.id.action_updateDeleteFragment_to_homeFragment)
            }
        }

        binding.deleteContact.setOnClickListener{
            contactsViewModel.deleteContact();
            findNavController().navigate(R.id.action_updateDeleteFragment_to_homeFragment)
        }

    }

    private fun setSpinner(){
        spinner =  binding.relationSpinner;
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            act,
            R.array.relation_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
            val defultPos = adapter.getPosition(contact.get("relationType").toString());
            spinner.setSelection(defultPos)
        }
    }

    private fun showCalander(){
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
}