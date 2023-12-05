package com.example.safehome.communityview

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.CommunityBlockAdapter
import com.example.safehome.adapter.EmergencyContactAdapter
import com.example.safehome.adapter.ResidentsAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityEmergencyInCommunityBinding
import com.example.safehome.databinding.ActivityResidentsBinding
import com.example.safehome.model.CommunityBlock
import com.example.safehome.model.DailyHelpHistoryModel
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.MyDuesMaintenanceDetails
import com.example.safehome.model.Residents
import com.example.safehome.policies.PoliciesAdapter
import com.example.safehome.policies.PoliciesModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class EmergencyContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergencyInCommunityBinding
    private var blockList: ArrayList<CommunityBlock> = ArrayList()
    private lateinit var emergencyContactAdapter: EmergencyContactAdapter
    private var blockPopupWindow: PopupWindow? = null
    private var selectedBlock: String = ""
    private var payUsingDialog: Dialog? = null

    private var emergrncyContactList: ArrayList<EmergencyContact.Data.EmergencyContact> = ArrayList()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getEmergentContactAPI: Call<EmergencyContact>
    var Auth_Token: String? = ""
    var contactTypeId: Int? = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyInCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()

        clickEvents()
        getAllEmergencyContactAPI()

    }

    private fun initData() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@EmergencyContactActivity)
        Auth_Token = Utils.getStringPref(this@EmergencyContactActivity, "Token", "")

        if (intent.extras?.getInt("contactTypeId") != null){
            contactTypeId = intent.extras?.getInt("contactTypeId")
        }
    }
    private fun clickEvents() {
        binding.backBtnClick.setOnClickListener {
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (emergrncyContactList.isNotEmpty()) {
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

        val myDuesList = ArrayList<EmergencyContact.Data.EmergencyContact>()

        val courseAry: ArrayList<EmergencyContact.Data.EmergencyContact> = emergrncyContactList

        for (eachCourse in courseAry) {
            if (!eachCourse.contactTypeName .isNullOrBlank() && eachCourse.contactTypeName .lowercase(Locale.getDefault()).contains(text.lowercase(
                    Locale.getDefault())) ||
                !eachCourse.contactPerson .isNullOrBlank() && eachCourse.contactPerson .lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.mobileNumber .isNullOrBlank() && eachCourse.mobileNumber .lowercase(Locale.getDefault()).contains(text.lowercase(
                    Locale.getDefault())) ||
                !eachCourse.secondaryNumber .isNullOrBlank() && eachCourse.secondaryNumber .lowercase(Locale.getDefault()).contains(text.lowercase(
                    Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        emergencyContactAdapter.filterList(myDuesList);
    }


    private fun emergencyContactAdapter() {

        if (emergrncyContactList.isNotEmpty()){

            binding.emergencyContactsRecyclerView.visibility = View.VISIBLE
            binding.emptyTxt.visibility = View.GONE
            binding.emergencyContactsRecyclerView.layoutManager = LinearLayoutManager(this)
            emergencyContactAdapter = EmergencyContactAdapter(this, emergrncyContactList)
            binding.emergencyContactsRecyclerView.adapter = emergencyContactAdapter
            emergencyContactAdapter.setCallback(this)
        }else{
            binding.emergencyContactsRecyclerView.visibility = View.GONE
            binding.emptyTxt.visibility = View.VISIBLE
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun contactPopup(contact: EmergencyContact.Data.EmergencyContact) {
        val layoutInflater: LayoutInflater =
           getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.emergency_contact_dialog, null)
        payUsingDialog = Dialog(this, R.style.CustomAlertDialog)
        payUsingDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payUsingDialog!!.setContentView(view)
        payUsingDialog!!.setCanceledOnTouchOutside(true)
        payUsingDialog!!.setCancelable(true)


        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val tvPrimaryNumber = view.findViewById<TextView>(R.id.tvPrimaryNumber)
        val tvSecondaryNumber = view.findViewById<TextView>(R.id.tvSecondaryNumber)
        val primaryCall = view.findViewById<ImageView>(R.id.primaryCall)
        val secondaryCall = view.findViewById<ImageView>(R.id.secondaryCall)
        val primaryMessage = view.findViewById<ImageView>(R.id.primaryMsg)
        val secondaryMessage = view.findViewById<ImageView>(R.id.secondaryMsg)

        if (contact.mobileNumber != null){

            tvPrimaryNumber.text = contact.mobileNumber
        }
        if (contact.secondaryNumber != null){

            tvSecondaryNumber.text = contact.secondaryNumber
        }
        primaryCall.setOnClickListener{
            makeCall(contact.mobileNumber)
        }
        secondaryCall.setOnClickListener{
            makeCall(contact.secondaryNumber)
        }
        primaryMessage.setOnClickListener {
            sendMessage(contact.mobileNumber)
        }
        secondaryMessage.setOnClickListener {
            sendMessage(contact.secondaryNumber)
        }



        close.setOnClickListener {
            if (payUsingDialog!!.isShowing) {
                payUsingDialog!!.dismiss()
            }
        }


        payUsingDialog!!.show()
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllEmergencyContactAPI() {
        Log.e("Token", Auth_Token.toString())
        customProgressDialog.progressDialogShow(this@EmergencyContactActivity, this.getString(R.string.loading))

        getEmergentContactAPI = apiInterface.getEmergentContactAPI("bearer " + Auth_Token, contactTypeId = contactTypeId)
        getEmergentContactAPI.enqueue(object : Callback<EmergencyContact> {
            override fun onResponse(
                call: Call<EmergencyContact>,
                response: Response<EmergencyContact>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (emergrncyContactList.isNotEmpty()) {
                                        emergrncyContactList.clear()
                                    }
                                    emergrncyContactList =
                                        response.body()!!.data.emergencyContacts as ArrayList<EmergencyContact.Data.EmergencyContact>
                                    emergencyContactAdapter()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@EmergencyContactActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@EmergencyContactActivity, response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<EmergencyContact>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@EmergencyContactActivity, t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun callAction(contact: EmergencyContact.Data.EmergencyContact) {

        contactPopup(contact)


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun messageAction(contact: EmergencyContact.Data.EmergencyContact) {
        contactPopup(contact)

    }

    private fun makeCall(mobileNumber : String){
         try {
          val phoneNumber = Uri.parse("tel:${mobileNumber}")
          val dialIntent = Intent(Intent.ACTION_DIAL, phoneNumber)

          startActivity(dialIntent)
      } catch (error: Exception) {
          Utils.showToast(this@EmergencyContactActivity, error.message.toString())
          println(error.message.toString())
      }
    }

    private fun sendMessage(mobileNumber :String){
         try {
           val phoneNumber = Uri.parse("smsto:${mobileNumber}")
           val smsIntent = Intent(Intent.ACTION_SENDTO, phoneNumber)

           startActivity(smsIntent)
       } catch (error: Exception) {
           Utils.showToast(this@EmergencyContactActivity, error.message.toString())
           println(error.message.toString())
       }
    }
}