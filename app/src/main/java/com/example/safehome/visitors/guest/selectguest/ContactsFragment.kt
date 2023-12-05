package com.example.safehome.visitors.guest.selectguest

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentContactsBinding
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.guest.GuestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactsFragment(
    private val approvalStatus: ArrayList<ApprovalStatusModel.Data>? = null,
    private val from: String?
) : Fragment() {

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var contactsModel: ContactsModel
    private var builder: StringBuilder? = null
    private lateinit var binding: FragmentContactsBinding
    private var contactsModelList: MutableList<ContactsModel> = ArrayList()
    private var uniqueContactsList: MutableList<ContactsModel> = ArrayList()
    private lateinit var contactsAdapter: ContactsAdapter
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactsBinding.inflate(inflater, container, false)
        customProgressDialog = CustomProgressDialog()

        //  setContactData()
        loadContactsData()
        clickEvents()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadContactsData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requireContext().checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            //callback onRequestPermissionsResult
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                customProgressDialog.progressDialogShow(
                    requireContext(),
                    requireContext().getString(R.string.loading)
                )
                contactsModelList = loadContacts()
                // Handle the list of contacts here, e.g., update your UI

                populateData()
            }

//            getContacts()
            Log.e("ContactsList", "" + contactsModelList.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContactsData()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun loadContacts(): MutableList<ContactsModel> = withContext(Dispatchers.IO) {
        val contactsList = mutableListOf<ContactsModel>()

        // Query the contacts using the ContentResolver
        val contentResolver = requireContext().contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null
        )

        cursor?.use { cursor ->
            val idColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(idColumnIndex)
                val contactName = cursor.getString(nameColumnIndex)

                // Get phone numbers for the contact
//                val phoneNumbers = mutableListOf<String>()
                val phoneCursor: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
                )

                phoneCursor?.use { phoneCursor ->
                    val phoneNumberColumnIndex =
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (phoneCursor.moveToNext()) {

                        val phoneNumber = phoneCursor.getString(phoneNumberColumnIndex)
//                        phoneNumbers.add(phoneNumber)
                        val phoneNumber1 = phoneNumber.replace(Regex("[^0-9+]"), "")

                        // Remove the country code (assuming the country code is '+91')
                        val formattedPhoneNumber = if (phoneNumber1.startsWith("+91")) {
                            phoneNumber1.substring(3)
                        } else {
                            phoneNumber1
                        }

                        // Log the processed phone number
                        Log.d("Contact", "Processed Phone: $formattedPhoneNumber")

                        // Check if the processed phone number is exactly 10 digits
//                        if (formattedPhoneNumber.length == 10) {
                            // Handle the contact information as needed (e.g., display in UI)
                            Log.d("Contact", "Name: $contactName, Phone: $formattedPhoneNumber")
                            contactsModel =
                                ContactsModel(contactId, contactName, formattedPhoneNumber)
//                        }

                    }
                }

                phoneCursor?.close()


                contactsList.add(contactsModel)
            }
        }

        cursor?.close()

        return@withContext contactsList
    }

    @SuppressLint("Range")
    private fun getContacts() {
        val resolver: ContentResolver = requireContext().contentResolver;
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = requireContext().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            /*   builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                                   phoneNumValue).append("\n\n")
                               Log.e("Name ===>",phoneNumValue);*/
                            contactsModel = ContactsModel("", name, phoneNumValue)
                        }
                    }

                    cursorPhone.close()
                }
                contactsModelList.add(contactsModel)
                populateData()
            }
        } else {
            //   toast("No contacts available!")
        }
        cursor.close()
    }

    private fun setContactData() {

        /* val c1 = ContactsModel("Aparna", "(+91)9183040604")
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
 */
    }

    private fun clickEvents() {

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (contactsModelList.isNotEmpty()) {

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

        val contactsModel = mutableListOf<ContactsModel>()
        val courseAry: MutableList<ContactsModel> = contactsModelList

        for (eachCourse in courseAry) {

            if (
                !eachCourse.name.isNullOrBlank() && eachCourse.name.lowercase(
                    Locale.getDefault()
                ).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.number.toString().isNullOrBlank() && eachCourse.number.toString()
                    .lowercase(
                        Locale.getDefault()
                    ).contains(text.lowercase(Locale.getDefault()))
            ) {
                contactsModel.add(eachCourse)
            }
        }

        contactsAdapter.filterList(contactsModel);
    }


    private fun populateData() {
        customProgressDialog.progressDialogDismiss()
        uniqueContactsList =
            contactsModelList.distinctBy { it.number } as MutableList<ContactsModel>
        if (uniqueContactsList.isEmpty()) {
            binding.tvNoDataFound.visibility = View.VISIBLE
            binding.rvContacts.visibility = View.GONE
        } else {
            binding.tvNoDataFound.visibility = View.GONE
            binding.rvContacts.visibility = View.VISIBLE
            binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
            contactsAdapter = ContactsAdapter(requireContext(), uniqueContactsList)
            contactsAdapter.setCallbackServiceType(this@ContactsFragment)

            binding.rvContacts.adapter = contactsAdapter
        }
    }

    fun setContact(contact: ContactsModel) {

        val intent = Intent(requireContext(), GuestActivity::class.java)
        intent.putExtra("selectedContact", contact)
        intent.putExtra("approvalStatus", approvalStatus)
        intent.putExtra("from", from)
        startActivity(intent)
    }


}