package tc.tcapps.birthday_wisher

import android.app.Activity
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
}