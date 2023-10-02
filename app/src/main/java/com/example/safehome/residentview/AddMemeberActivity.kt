package com.example.safehome.residentview

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AddFamilyMemberAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAddMemeberBinding
import com.example.safehome.model.FamilyDetail
import com.example.safehome.model.FamilyDetails
import com.example.safehome.model.MobileSignUp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMemeberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMemeberBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getMembersDetailsCall: Call<FamilyDetail>
    private var membersList: ArrayList<FamilyDetails> = ArrayList()
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private var memberDialog: Dialog? = null
    private var deleteMemberDialog: Dialog? = null
    private var editMemberDialog: Dialog? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var mobileNumber: String? = null
    private var emailId: String? = null
    private lateinit var first_name_et: EditText
    private lateinit var last_name_et: EditText
    private lateinit var mobile_number_et: EditText
    private lateinit var email_address_et: EditText
    private lateinit var firstNameErrorCase: TextView
    private lateinit var profile_picture_family: ImageView
    private lateinit var lastNameErrorCase: TextView
    private lateinit var mobileNumberErrorCase: TextView
    private lateinit var updateFamilyDetailsCall: Call<MobileSignUp>
    private lateinit var deleteFamilyDetailsCall: Call<MobileSignUp>
    private var id: Int = 0
    private var clientId: Int = 2
    private var ageGroup: String = ""


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemeberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "residentId", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        })

        getMembersServiceCall()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getMembersServiceCall() {
        membersList.clear()
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here sign up service call
        getMembersDetailsCall = apiInterface.getFamilyMemberDetails(
            "bearer " + Auth_Token,
            User_Id!!.toInt()
        ) //User_Id!!.toInt()
        getMembersDetailsCall.enqueue(object : Callback<FamilyDetail> {
            override fun onResponse(
                call: Call<FamilyDetail>,
                response: Response<FamilyDetail>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {

                        membersList = response.body()!!.data as ArrayList<FamilyDetails>
                        if (membersList.size > 0) {
                            setupRecyclerView(membersList)
                        }
                    } else {
                        // membersList.add(FamilyDetails(type = "new"))
                        setupRecyclerView(membersList)
                    }
                } else {
                    // membersList.add(FamilyDetails(type = "new"))
                    setupRecyclerView(membersList)
                }
            }

            override fun onFailure(call: Call<FamilyDetail>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@AddMemeberActivity, t.message.toString())
            }
        })
    }

    private fun setupRecyclerView(membersList: ArrayList<FamilyDetails>) {
        membersList.add(FamilyDetails(type = "new"))
        binding.addMyFamilyRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            val communityCitiesAdapter =
                AddFamilyMemberAdapter(this@AddMemeberActivity, membersList)

            binding.addMyFamilyRecyclerView.adapter = communityCitiesAdapter
            communityCitiesAdapter.setCallback(this@AddMemeberActivity)
        }
    }

    fun selectMember(s: FamilyDetails) {
        Toast.makeText(this@AddMemeberActivity, s.firstName, Toast.LENGTH_LONG).show()
    }

    fun selectAddMember(familyDetails: FamilyDetails) {
        val intent = Intent(this, MyFamilyActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun longPressMember(familyDetails: FamilyDetails) {
        // here edit or delete pop up functionality
        if (familyDetails != null) {
            selectedMemberPopup(familyDetails)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun selectedMemberPopup(familyDetails: FamilyDetails) {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.family_member_popup_dialog, null)
        memberDialog = Dialog(this, R.style.CustomAlertDialog)
        memberDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        memberDialog!!.setContentView(view)
        memberDialog!!.setCanceledOnTouchOutside(true)
        memberDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(memberDialog!!.window!!.attributes)
        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        memberDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val member_type: TextView = view.findViewById(R.id.member_type)
        val member_name: TextView = view.findViewById(R.id.member_name)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        val edit: TextView = view.findViewById(R.id.edit_btn)
        val delete: TextView = view.findViewById(R.id.delete_btn)
        val profile_picture_family: ImageView = view.findViewById(R.id.profile_picture_family)

        familyDetails.ageGroup?.let{
            if (familyDetails.ageGroup.isNotEmpty()) {
                member_type.text = it.replace("\"", "")
            }
        }
        familyDetails.firstName?.let {
            if (it.isNotEmpty()) {
                member_name.text = it.replace(
                    "\"",
                    ""
                ) + " " + familyDetails.lastName.replace("\"", "")
            }
        }
        familyDetails.mobileNo?.let{
            if (it.isNotEmpty()) {
                member_mob_number.text = it.replace("\"", "")
            }
        }
        familyDetails.ageGroup?.let{
            try {
                if (it.replace("\"", "").equals("Adult")) {
                    profile_picture_family.setImageResource(R.drawable.adult_icon)
                } else {
                    profile_picture_family.setImageResource(R.drawable.kids_icon)
                }
            } catch (ex: Exception) {
                profile_picture_family.setImageResource(R.drawable.profile)
            }
        }

        close.setOnClickListener {
            if (memberDialog!!.isShowing) {
                memberDialog!!.dismiss()
            }
        }

        //edit button click
        edit.setOnClickListener {
            if (memberDialog!!.isShowing) {
                memberDialog!!.dismiss()
            }
            editMemberPopup(familyDetails)
        }

        //delete button click
        delete.setOnClickListener {
            if (memberDialog!!.isShowing) {
                memberDialog!!.dismiss()
            }
            deleteMemberPopup(familyDetails)
        }
        memberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun editMemberPopup(familyDetails: FamilyDetails) {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.edit_family_member_popup_dialog, null)
        editMemberDialog = Dialog(this, R.style.CustomAlertDialog)
        editMemberDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        editMemberDialog!!.setContentView(view)
        editMemberDialog!!.setCanceledOnTouchOutside(true)
        editMemberDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(editMemberDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        editMemberDialog!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        first_name_et = view.findViewById<EditText>(R.id.first_name_et)
        first_name_et.setBackgroundResource(android.R.color.transparent)
        last_name_et = view.findViewById<EditText>(R.id.last_name_et)
        last_name_et.setBackgroundResource(android.R.color.transparent)
        mobile_number_et = view.findViewById<EditText>(R.id.mobile_number_et)
        mobile_number_et.setBackgroundResource(android.R.color.transparent)
        email_address_et = view.findViewById<EditText>(R.id.email_address_et)
        email_address_et.setBackgroundResource(android.R.color.transparent)
        val age_group_text: TextView = view.findViewById<TextView>(R.id.age_group_text) as TextView
        val updateTv = view.findViewById<TextView>(R.id.update) as TextView
        firstNameErrorCase = view.findViewById(R.id.first_name_error_case) as TextView
        lastNameErrorCase = view.findViewById(R.id.last_name_error_case) as TextView
        mobileNumberErrorCase = view.findViewById(R.id.mobile_number_error_case) as TextView
        firstNameErrorCase = view.findViewById(R.id.first_name_error_case) as TextView
        profile_picture_family = view.findViewById<ImageView>(R.id.profile_picture)

        familyDetails.ageGroup?.let{
            try {
                if (it.replace("\"", "").equals("Adult")) {
                    profile_picture_family.setImageResource(R.drawable.adult_icon)
                } else {
                    profile_picture_family.setImageResource(R.drawable.kids_icon)
                }
            } catch (ex: Exception) {
                profile_picture_family.setImageResource(R.drawable.profile)
            }
        }

        val mobile_number_header = view.findViewById<TextView>(R.id.mobile_number_header)
        familyDetails.ageGroup?.let {
            if(it.replace("\"","").equals("Kids") ||
                it.replace("\"","").equals("Kid")){
                mobile_number_header.text = "Mobile Number"
            }else{
                mobile_number_header.text =
                    Html.fromHtml(getString(R.string.mobile_number_hint) + "" + "<font color='white'>*</font>")
            }
        }

        if (familyDetails != null) {
            familyDetails.firstName?.let {
                if (it.isNotEmpty()) {
                    first_name_et.setText(it.replace("\"", ""))
                }
            }
            familyDetails.lastName?.let {
                if (familyDetails.lastName.isNotEmpty()) {
                    last_name_et.setText(it.replace("\"", ""))
                }
            }
            familyDetails.mobileNo?.let {
                if (it.isNotEmpty()) {
                    mobile_number_et.setText(it.replace("\"", ""))
                }
            }
            familyDetails.email?.let {
                if (it.isNotEmpty()) {
                    email_address_et.setText(it.replace("\"", ""))
                }
            }
            familyDetails.ageGroup?.let {
                age_group_text.setText(it.replace("\"", ""))
            }
        }

        close.setOnClickListener {
            if (editMemberDialog!!.isShowing) {
                editMemberDialog!!.dismiss()
            }
        }
        updateTv.setOnClickListener {
            verifyData(familyDetails)
        }

        editMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun deleteMemberPopup(familyDetails: FamilyDetails) {

        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_family_member_popup_dialog, null)
        deleteMemberDialog = Dialog(this, R.style.CustomAlertDialog)
        deleteMemberDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteMemberDialog!!.setContentView(view)
        deleteMemberDialog!!.setCanceledOnTouchOutside(true)
        deleteMemberDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(deleteMemberDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        deleteMemberDialog!!.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)

        close.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            deleteFamilyMemberServiceCall(familyDetails)
        }

        no.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }
        deleteMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifyData(familyDetails: FamilyDetails) {
        // here get sign up user data
        firstName = first_name_et.text.toString().trim()
        first_name_et.addTextChangedListener(textWatcher);

        lastName = last_name_et.text.toString().trim()
        last_name_et.addTextChangedListener(textWatcher);

        mobileNumber = mobile_number_et.text.toString().trim()
        mobile_number_et.addTextChangedListener(textWatcher);

        emailId = email_address_et.text.toString().trim()

        var ageGroup = familyDetails.ageGroup?.let {
            it.replace("\"","")
        }

        mobile_number_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ageGroup.equals("Kid")) {
                    if(mobile_number_et.text.length == 0){
                        mobileNumberErrorCase.visibility = View.GONE
                    }
                }
            }
        })

        if (doUserValidation(familyDetails)) {
            updateFamilyMemberDetailServiceCall(familyDetails)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateFamilyMemberDetailServiceCall(familyDetails: FamilyDetails) {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        if (familyDetails.id != null) {
            id = familyDetails.id
            Log.e("id", familyDetails.id.toString())
        }
        if (familyDetails.clientId != null) {
            clientId = familyDetails.clientId
        }
        if (familyDetails.ageGroup.isNotEmpty()) {
            ageGroup = familyDetails.ageGroup
        }

        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("Id", id)
            addProperty("ResidentId", User_Id)
            addProperty("FirstName", firstName)
            addProperty("LastName", lastName)
            addProperty("MobileNo", mobileNumber)
            addProperty("AgeGroup", ageGroup)
            addProperty("Email", emailId)
        }

        updateFamilyDetailsCall = apiInterface.updateMyFamily("bearer " + Auth_Token, jsonObject)
        updateFamilyDetailsCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    updateResponseStatus(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@AddMemeberActivity, t.message.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateResponseStatus(body: MobileSignUp) {
        if (body.statusCode == 1) {

            if (editMemberDialog!!.isShowing) {
                editMemberDialog?.dismiss()
            }
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
            getMembersServiceCall()

        } else if (body.statusCode == 2) {

            // already registered
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

    private fun doUserValidation(familyDetails: FamilyDetails): Boolean {
        if (firstName.isNullOrEmpty()) {
            firstNameErrorCase.visibility = View.VISIBLE
        }

        if (lastName.isNullOrEmpty()) {
            lastNameErrorCase.visibility = View.VISIBLE
        }

//        if (mobileNumber.isNullOrEmpty()) {
//            mobileNumberErrorCase.visibility = View.VISIBLE
//            mobileNumberErrorCase.text = this.getString(R.string.please_enter_mobile_number)
//        }
//
//        if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
//            // here check valid number or not
//            if (mobileNumber!!.length > 9) {
//                // true
//            } else {
//                mobileNumberErrorCase.visibility = View.VISIBLE
//                mobileNumberErrorCase.text =
//                    this.getString(R.string.please_enter_valid_mobile_number)
//            }
//        }
//
//        if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isNotEmpty() && mobileNumber?.length!! > 9)) {
//            return true
//        } else {
//            return false
//        }



       var ageGroup = familyDetails.ageGroup?.let {
            it.replace("\"","")
        }

        if (ageGroup.equals("Adult")|| ageGroup.equals("Adults")) {
            // selected Adult then check this below condition
            if (mobileNumber.isNullOrEmpty()) {
                mobileNumberErrorCase.visibility = View.VISIBLE
                mobileNumberErrorCase.text =
                    this.getString(R.string.please_enter_mobile_number)
            }

            if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
                // here check valid number or not
                if (mobileNumber!!.length == 10) {
                    // true
                } else {
                    mobileNumberErrorCase.visibility = View.VISIBLE
                    mobileNumberErrorCase.text =
                        this.getString(R.string.please_enter_valid_mobile_number)
                }
            }

            if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isNotEmpty() && mobileNumber?.length == 10)) {
                return true
            } else {
                return false
            }
        } else {
            if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
                if (mobileNumber?.length!! >= 1) {
                    // here check valid number or not
                    if (mobileNumber!!.length == 10) {
                        return true
                    } else {
                        mobileNumberErrorCase.visibility = View.VISIBLE
                        mobileNumberErrorCase.text =
                            this.getString(R.string.please_enter_valid_mobile_number)
                        return false
                    }
                    return false
                } else {
                    return true
                }
            }
            // selected Kid then check this below condition
            if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isEmpty() || mobileNumber?.length == 10)) {
                return true
            } else {
                return false
            }
        }

        return true
    }


    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (first_name_et.text.hashCode() === s.hashCode()) {
                firstNameErrorCase.visibility = View.GONE
            } else if (last_name_et.text.hashCode() === s.hashCode()) {
                lastNameErrorCase.visibility = View.GONE
            } else if (mobile_number_et.text.length == 10) {
                mobileNumberErrorCase.visibility = View.GONE
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteFamilyMemberServiceCall(familyDetails: FamilyDetails) {

        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        if (familyDetails.id != null) {
            id = familyDetails.id
        }
        if (familyDetails.clientId != null) {
            clientId = familyDetails.clientId
        }
        deleteFamilyDetailsCall = apiInterface.deleteFamilyMember("bearer " + Auth_Token, id)
        deleteFamilyDetailsCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    membersList.clear()
                    deleteMemberResponseStatus(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@AddMemeberActivity, t.message.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteMemberResponseStatus(body: MobileSignUp) {
        if (body.statusCode == 1) {

            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog?.dismiss()
            }
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
            getMembersServiceCall()

        } else if (body.statusCode == 2) {

            // already registered
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

}