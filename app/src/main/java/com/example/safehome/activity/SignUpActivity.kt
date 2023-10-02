package com.example.safehome.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivitySignUpBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var firstName: String? = null
    private var lastName: String? = null
    private var mobileNumber: String? = null
    private var emailId: String? = null
    private lateinit var apiInterface: APIInterface
    private lateinit var signUpCall: Call<MobileSignUp>
    private lateinit var customProgressDialog: CustomProgressDialog
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progress dialog
        customProgressDialog = CustomProgressDialog()

        binding.firstNameEt.setBackgroundResource(android.R.color.transparent)
        binding.lastNameEt.setBackgroundResource(android.R.color.transparent)
        binding.mobileNumberEt.setBackgroundResource(android.R.color.transparent)
        binding.emailAddressEt.setBackgroundResource(android.R.color.transparent)

        binding.firstNameHeader.text =
            Html.fromHtml(getString(R.string.first_name_hint) + "" + "<font color='white'>*</font>")
        binding.lastNameHeader.text =
            Html.fromHtml(getString(R.string.last_name_hint) + "" + "<font color='white'>*</font>")
        binding.mobileNumberHeader.text =
            Html.fromHtml(getString(R.string.mobile_number_hint) + "" + "<font color='white'>*</font>")

        binding.signInTxt.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        setupApiService()
        buttonClickEvents()
    }

    private fun setupApiService() {
        apiInterface = APIClient.getClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buttonClickEvents() {
        // here handle button click events
        binding.submitBtn.setOnClickListener {
            getSignUpData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSignUpData() {
        // here get sign up user data
        firstName = binding.firstNameEt.text.toString().trim()
        binding.firstNameEt.addTextChangedListener(textWatcher);

        lastName = binding.lastNameEt.text.toString().trim()
        binding.lastNameEt.addTextChangedListener(textWatcher);

        mobileNumber = binding.mobileNumberEt.text.toString().trim()
        binding.mobileNumberEt.addTextChangedListener(textWatcher);

        emailId = binding.emailAddressEt.text.toString().trim()
        binding.emailAddressEt.addTextChangedListener(textWatcher);

        emailId = binding.emailAddressEt.text.toString().trim()

        if (doUserValidation()) {
            signUpServiceCall()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun signUpServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here create signup input object
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("firstName", firstName)
            addProperty("lastName", lastName)
            addProperty("mobileNo", mobileNumber)
            addProperty("email", emailId)
        }

        signUpCall = apiInterface.mobileSignUp(jsonObject)
        signUpCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if(response.body()?.statusCode == 1) {
                        if (response.body()?.data?.userId != null) {
                            // Toast.makeText(this@SignUpActivity,response.body()!!.data.userId.toString(),Toast.LENGTH_LONG).show()
                            Utils.saveStringPref(
                                this@SignUpActivity,
                                "User_Id",
                                response.body()!!.data.userId.toString()
                            )
                        }
                        // here save user signup data
                        navigationToOtpScreen(response.body()!!)
                    }else{
                         Toast.makeText(this@SignUpActivity,response.body()?.message,Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@SignUpActivity, t.message.toString())
            }
        })
    }

    private fun navigationToOtpScreen(body: MobileSignUp) {

        if (body.statusCode == 1) {
            // here ave user data
            firstName?.let { Utils.saveStringPref(this, "First_Name", it) }
            lastName?.let { Utils.saveStringPref(this, "Last_Name", it) }
            mobileNumber?.let { Utils.saveStringPref(this, "Mobile_Number", it) }
            emailId?.let { Utils.saveStringPref(this, "Email_Id", it) }

            if (body.data != null) {

                if (body.data.otp != null) {
                    Utils.showToast(this@SignUpActivity, body.data.otp)
                    Utils.saveStringPref(this, "User_OTP", body.data.otp)
                }
            }

            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }

            // here nav logic
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("SCREEN", "COMES_FROM_SIGN_UP")
            startActivity(intent)

        } else if (body.statusCode == 2) {
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

    private fun doUserValidation(): Boolean {
        if (firstName.isNullOrEmpty()) {
            binding.firstNameErrorCase.visibility = View.VISIBLE
        }

        if (lastName.isNullOrEmpty()) {
            binding.lastNameErrorCase.visibility = View.VISIBLE
        }

        if (mobileNumber.isNullOrEmpty()) {
            binding.mobileNumberErrorCase.visibility = View.VISIBLE
            binding.mobileNumberErrorCase.text = this.getString(R.string.please_enter_mobile_number)
        }

        var validEmail: String = binding.emailAddressEt!!.text.trim().toString()
        if(validEmail.length>0) {
            if (!validEmail.matches(emailRegex.toRegex())) {
                binding.emailErrorCase.visibility = View.VISIBLE
                binding.emailErrorCase.text = this.getString(R.string.please_enter_valid_email)
            }
        }

        if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
            // here check valid number or not
            if (mobileNumber!!.length == 10) {
            } else {
                binding.mobileNumberErrorCase.visibility = View.VISIBLE
                binding.mobileNumberErrorCase.text =
                    this.getString(R.string.please_enter_valid_mobile_number)
            }
        }

        if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isNotEmpty() && mobileNumber?.length == 10)) {
            if (validEmail.length > 0) {
                if (validEmail.matches(emailRegex.toRegex())) {
                    return true
                }
                return false
            } else {
                return true
            }
        } else {
            return false
        }

        return true
    }

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (binding.emailAddressEt.text.toString().matches(emailRegex.toRegex())) {
                binding.emailErrorCase.visibility = View.GONE
            }
        }
        override fun afterTextChanged(s: Editable) {
            if (binding.firstNameEt.text.hashCode() === s.hashCode()) {
                binding.firstNameErrorCase.visibility = View.GONE
            } else if (binding.lastNameEt.text.hashCode() === s.hashCode()) {
                binding.lastNameErrorCase.visibility = View.GONE
            } else if (binding.mobileNumberEt.text.length == 10) {
                binding.mobileNumberErrorCase.visibility = View.GONE
            }
        }
    }

}