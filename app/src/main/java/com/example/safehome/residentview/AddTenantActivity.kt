package com.example.safehome.residentview

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AddTenantAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAddTenantBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.Tenant
import com.example.safehome.model.TenantDetails
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTenantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTenantBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var tenantCall: Call<TenantDetails>
    private lateinit var updatetenantCall: Call<MobileSignUp>
    private var tenantsList: ArrayList<Tenant> = ArrayList()
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
    private lateinit var Block_number: TextView
    private lateinit var email_address_et: EditText
    private lateinit var firstNameErrorCase: TextView
    private lateinit var lastNameErrorCase: TextView
    private lateinit var mobileNumberErrorCase: TextView
    private lateinit var updateFamilyDetailsCall: Call<MobileSignUp>
    private lateinit var deleteTenantCall: Call<MobileSignUp>
    private var tenantId: Int = 0
    private var clientId: Int = 2
    private var ageGroup: String = ""
    private var imageMultipartBody: MultipartBody.Part? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTenantBinding.inflate(layoutInflater)
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

        getTenantServiceCall()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getTenantServiceCall() {
        tenantsList.clear()
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here sign up service call
        tenantCall = apiInterface.getAllTenants(
            "bearer " + Auth_Token,
            User_Id!!.toInt()
        ) //User_Id!!.toInt()
        tenantCall.enqueue(object : Callback<TenantDetails> {
            override fun onResponse(
                call: Call<TenantDetails>,
                response: Response<TenantDetails>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (tenantsList.isNotEmpty()) {
                            tenantsList.clear()
                        }
                        tenantsList = response.body()!!.data as ArrayList<Tenant>
                        if (tenantsList.size > 0) {
                            setupRecyclerView(tenantsList)
                        }
                    } else {
                        // membersList.add(FamilyDetails(type = "new"))
                        setupRecyclerView(tenantsList)
                    }
                } else {
                    // membersList.add(FamilyDetails(type = "new"))
                    setupRecyclerView(tenantsList)
                }
            }

            override fun onFailure(call: Call<TenantDetails>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@AddTenantActivity, t.message.toString())
            }
        })

    }

    private fun setupRecyclerView(tenantsList: ArrayList<Tenant>) {

        if (tenantsList.size < 1) {
            tenantsList.add(Tenant(type = "new"))
        }

        binding.addTenantRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            val addTenantAdapter =
                AddTenantAdapter(this@AddTenantActivity, tenantsList)

            binding.addTenantRecyclerView.adapter = addTenantAdapter
            addTenantAdapter.setCallback(this@AddTenantActivity)
        }

    }

    fun selectAddMember(tenant: Tenant) {
        val intent = Intent(this, TenantActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun longPressMember(tenant: Tenant) {
        // here edit or delete pop up functionality

        if (tenant != null) {
            selectedMemberPopup(tenant)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun selectedMemberPopup(tenant: Tenant) {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.tenant_member_dialog_popup, null)
        memberDialog = Dialog(this, R.style.CustomAlertDialog)
        memberDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        memberDialog!!.setContentView(view)
        memberDialog!!.setCanceledOnTouchOutside(true)
        memberDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(memberDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        memberDialog!!.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close)
        val member_type: TextView = view.findViewById(R.id.member_type)
        val member_name: TextView = view.findViewById(R.id.member_name)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        val edit: TextView = view.findViewById(R.id.edit_btn)
        val delete: TextView = view.findViewById(R.id.delete_btn)

        var block_flat: String
        try {
            block_flat = tenant.block.toString() + "-" + tenant.flatNo.toString()
            block_flat.replace("Block", "")
        } catch (ex: Exception) {
            block_flat = "Block"
        }

        member_type.text = block_flat

        if (tenant.firstName.isNotEmpty()) {
            member_name.text =
                tenant.firstName.replace("\"", "") + " " + tenant.lastName.replace("\"", "")
        }
        if (tenant.mobileNo.isNotEmpty()) {
            member_mob_number.text = tenant.mobileNo
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
            editMemberPopup(tenant)
        }

        //delete button click
        delete.setOnClickListener {
            if (memberDialog!!.isShowing) {
                memberDialog!!.dismiss()
            }
            deleteMemberPopup(tenant)
        }
        memberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun editMemberPopup(tenant: Tenant) {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.edit_tenant_dialog_popup, null)
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
        Block_number = view.findViewById<EditText>(R.id.Block_number)
        mobile_number_et.setBackgroundResource(android.R.color.transparent)
        email_address_et = view.findViewById<EditText>(R.id.email_address_et)
        email_address_et.setBackgroundResource(android.R.color.transparent)
        val updateTv = view.findViewById<TextView>(R.id.update) as TextView
        firstNameErrorCase = view.findViewById(R.id.first_name_error_case) as TextView
        lastNameErrorCase = view.findViewById(R.id.last_name_error_case) as TextView
        mobileNumberErrorCase = view.findViewById(R.id.mobile_number_error_case) as TextView
        firstNameErrorCase = view.findViewById(R.id.first_name_error_case) as TextView

        if (tenant != null) {

            if (tenant.firstName.isNotEmpty()) {
                first_name_et.setText(tenant.firstName.replace("\"", ""))
            }
            if (tenant.lastName.isNotEmpty()) {
                last_name_et.setText(tenant.lastName.replace("\"", ""))
            }
            if (tenant.mobileNo.isNotEmpty()) {
                mobile_number_et.setText(tenant.mobileNo)
            }
            if (tenant.email.isNotEmpty()) {
                email_address_et.setText(tenant.email.replace("\"", ""))
            }
            var block_flat: String
            try {
                block_flat = tenant.block.toString() + "-" + tenant.flatNo.toString()
                block_flat.replace("Block", "")
            } catch (ex: Exception) {
                block_flat = "Block"
            }

            Block_number.setText(block_flat)
        }

        close.setOnClickListener {
            if (editMemberDialog!!.isShowing) {
                editMemberDialog!!.dismiss()
            }
        }

        updateTv.setOnClickListener {
            verifyData(tenant)
        }

        editMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun deleteMemberPopup(tenant: Tenant) {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
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
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
            deleteTenantServiceCall(tenant)
        }

        no.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }
        deleteMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifyData(tenant: Tenant) {
        // here get sign up user data
        firstName = first_name_et.text.toString().trim()
        first_name_et.addTextChangedListener(textWatcher);

        lastName = last_name_et.text.toString().trim()
        last_name_et.addTextChangedListener(textWatcher);

        mobileNumber = mobile_number_et.text.toString().trim()
        mobile_number_et.addTextChangedListener(textWatcher);

        emailId = email_address_et.text.toString().trim()

        if (doUserValidation()) {
            updateTenantServiceCall(tenant)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateTenantServiceCall(tenant: Tenant) {

        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        if (tenant.tenantsId != null) {
            tenantId = tenant.tenantsId
        }

        var mobileNo = mobileNumber!!.replace("\"", "")
        imageMultipartBody?.let {
            updatetenantCall = apiInterface.updateTenant(
                "bearer " + Auth_Token,
                tenantId,
                User_Id!!.toInt(),
                firstName!!,
                lastName!!,
                mobileNo.toLong(),
                emailId!!,
                imageMultipartBody!!
            )
        }.run {
            updatetenantCall = apiInterface.updateTenantNoImage(
                "bearer " + Auth_Token,
                tenantId,
                User_Id!!.toInt(),
                firstName!!,
                lastName!!,
                mobileNo.toLong(),
                emailId!!,
            )
        }
        updatetenantCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    navigationToAddTenantScreen(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@AddTenantActivity, t.message.toString())
            }
        })
    }

    private fun navigationToAddTenantScreen(body: MobileSignUp) {
        if (body.statusCode == 1) {
            val intent = Intent(this, AddTenantActivity::class.java)
            startActivity(intent)
            finish()
        } else if (body.statusCode == 2) {
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

    private fun doUserValidation(): Boolean {
        if (firstName.isNullOrEmpty()) {
            firstNameErrorCase.visibility = View.VISIBLE
        }

        if (lastName.isNullOrEmpty()) {
            lastNameErrorCase.visibility = View.VISIBLE
        }

        if (mobileNumber.isNullOrEmpty()) {
            mobileNumberErrorCase.visibility = View.VISIBLE
            mobileNumberErrorCase.text = this.getString(R.string.please_enter_mobile_number)
        }

        if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
            // here check valid number or not
            if (mobileNumber!!.length > 9) {
                // true
            } else {
                mobileNumberErrorCase.visibility = View.VISIBLE
                mobileNumberErrorCase.text =
                    this.getString(R.string.please_enter_valid_mobile_number)
            }
        }

        if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isNotEmpty() && mobileNumber?.length!! > 9)) {
            return true
        } else {
            return false
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
    private fun deleteTenantServiceCall(tenant: Tenant) {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        if (tenant.tenantsId != null) {
            tenantId = tenant.tenantsId
        }
        deleteTenantCall = apiInterface.deleteTenant("bearer " + Auth_Token, tenantId)
        deleteTenantCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    deleteMemberResponseStatus(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@AddTenantActivity, t.message.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteMemberResponseStatus(body: MobileSignUp) {
        if (body.statusCode == 1) {
            tenantsList.clear()

            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog?.dismiss()
            }
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
            getTenantServiceCall()

        } else if (body.statusCode == 2) {
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

}