package com.example.birthday_wisher;

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.birthday_wisher.databinding.FragmentAddContactBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        val spinner: Spinner = binding.relationSpinner;
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

//            addContactToFirebase();

        }

    }


}
