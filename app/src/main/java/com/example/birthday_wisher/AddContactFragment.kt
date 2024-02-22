package com.example.birthday_wisher;

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
import com.example.birthday_wisher.databinding.FragmentAddContactBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore



public class AddContactFragment: Fragment(){
    private var _binding: FragmentAddContactBinding? = null;
    private lateinit var act: Activity;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseFirestore;


    private lateinit var relationType: String;
    private lateinit var name: String;
    private lateinit var phone: String;
    private lateinit var wish: String;
    private lateinit var DOB: String;

    private lateinit var spinner: Spinner;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false);
        activity?.let{
            if(it is Activity){
                act = it;
            }
        }
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        db = Firebase.firestore;
        auth = Firebase.auth;

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

        binding.addContactButton.setOnClickListener{
            name = binding.editName.text.toString();
            phone = binding.editPhone.text.toString();
            wish = binding.editMessage.text.toString();
            DOB = binding.textDate.text.toString();

            if(checkAllInputs()){
                Log.i("AddContact", "All set")
                addContactToFirebase();
                Log.i("AddContact", "Contact Created")
                clearInputs();
                Log.i("AddContact", "Input resets")
            }

        }

    }

    private fun addContactToFirebase(){
        val contactMap = hashMapOf(
            "name" to name,
            "phone" to phone,
            "relationType" to relationType,
            "message" to wish,
            "DOB" to DOB,
            "user" to auth.currentUser?.uid!!
        )

        Log.i("Firebase", "${auth.currentUser?.uid}")
        db.collection("Contacts").add(contactMap).addOnSuccessListener {docRef ->
            val userRef = db.collection("Users").document(auth.currentUser!!.uid)
            userRef.update("contacts", FieldValue.arrayUnion(docRef))
                .addOnSuccessListener {
                    Log.i("Firebase", "Contact reference added to user successfully")
                }
                .addOnFailureListener { e ->
                    Log.i("Firebase", "Error adding contact reference to user", e)
                }
        }.addOnFailureListener{
            Log.i("Firebase","Error occurred in contacts" ,it);
        }
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

        if(DOB == "Select Date"){
            Toast.makeText(act, "Please select the date of birth", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }

    private fun clearInputs(){
        binding.editName.text.clear();
        binding.editPhone.text.clear();
        binding.editMessage.text.clear();
        spinner.setSelection(0);
        binding.textDate.text = "Select Date";
    }

}
