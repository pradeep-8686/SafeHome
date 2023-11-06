package com.example.safehome.visitors.guest.selectguest

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.databinding.FragmentContactsBinding
import java.util.Locale

class ContactsFragment : Fragment() {

    private lateinit var binding : FragmentContactsBinding
    private var contactsModelList: ArrayList<ContactsModel> = ArrayList()
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactsBinding.inflate(inflater, container, false)

        setContactData()
        clickEvents()
        return binding.root
    }

    private fun setContactData() {

        val c1 = ContactsModel("Aparna", "(+91)9183040604")
        contactsModelList.add(c1)
        val c2 = ContactsModel("Dinesh", "(+91)9184020604")
        contactsModelList.add(c2)
        val c3 = ContactsModel("Ganesh", "(+91)9184020604")
        contactsModelList.add(c3)
        val c4 = ContactsModel("Rajesh", "(+91)9184020604")
        contactsModelList.add(c4)
        val c5 = ContactsModel("Supriya", "(+91)9184020604")
        contactsModelList.add(c5)
        val c6 = ContactsModel("Shirisha", "(+91)9184020604")
        contactsModelList.add(c6)
        val c7 = ContactsModel("Rajesh", "(+91)9184020604")
        contactsModelList.add(c7)
        val c8 = ContactsModel("Rajesh", "(+91)9184020604")
        contactsModelList.add(c8)
        val c9 = ContactsModel("Rajesh", "(+91)9184020604")
        contactsModelList.add(c9)


        populateData()

    }

    private fun clickEvents() {

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (contactsModelList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


    }
    fun filter(text: String) {

        val contactsModel = ArrayList<ContactsModel>()
        val courseAry: ArrayList<ContactsModel> = contactsModelList

        for (eachCourse in courseAry) {

            if (
                !eachCourse.name.isNullOrBlank() && eachCourse.name.lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.number.toString().isNullOrBlank() && eachCourse.number.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                contactsModel.add(eachCourse)
            }
        }

        contactsAdapter.filterList(contactsModel);
    }


    private fun populateData() {
        if (contactsModelList.isEmpty()){
            binding.tvNoDataFound.visibility = View.VISIBLE
            binding.rvContacts.visibility = View.GONE
        }else {
            binding.tvNoDataFound.visibility = View.GONE
            binding.rvContacts.visibility = View.VISIBLE
            binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
            contactsAdapter = ContactsAdapter(requireContext(), contactsModelList)
            contactsAdapter.setCallbackServiceType(this@ContactsFragment)

            binding.rvContacts.adapter = contactsAdapter
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setContact(contact: ContactsModel) {

    }

    

}