package com.example.safehome.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivitySignInBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private var mobileNumber: String? = null
    private lateinit var apiInterface: APIInterface
    private lateinit var signInCall: Call<MobileSignUp>
    private lateinit var customProgressDialog: CustomProgressDialog

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progress bar
        customProgressDialog = CustomProgressDialog()

        binding.mobileNumberHeader.text =
            Html.fromHtml(getString(R.string.enter_mobile_number) + " " + "<font color='white'>*</font>")
        binding.mobileNumberEt.setBackgroundResource(android.R.color.transparent)

        setupApiService()
        buttonClickEvents()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buttonClickEvents() {
        // here handle button click events
        binding.signupTxt.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.submitBtn.setOnClickListener {
            if (doSignInValidation()) {
                signInServiceCall()
            }
        }
    }

    private fun doSignInValidation(): Boolean {
        mobileNumber = binding.mobileNumberEt.text.toString().trim()
        binding.mobileNumberEt.addTextChangedListener(textWatcher);

        if (mobileNumber.isNullOrEmpty()) {
            binding.mobileNumberErrorCase.visibility = View.VISIBLE
            binding.mobileNumberErrorCase.text = this.getString(R.string.please_enter_mobile_number)
            //  Utils.showToast(this, this.getString(R.string.please_enter_mobile_number))
            return false
        }

        if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
            // here check valid number or not
            return if (mobileNumber!!.length == 10) {
                true
            } else {
                binding.mobileNumberErrorCase.visibility = View.VISIBLE
                binding.mobileNumberErrorCase.text =
                    this.getString(R.string.please_enter_valid_mobile_number)
                //  Utils.showToast(this, this.getString(R.string.please_enter_valid_mobile_number))
                false
            }
        }
        return true
    }

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (binding.mobileNumberEt.text.length == 10) {
                binding.mobileNumberErrorCase.visibility = View.GONE
            }
        }
    }


    private fun setupApiService() {
        apiInterface = APIClient.getClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun signInServiceCall() {

        mobileNumber?.let { Utils.saveStringPref(this, "Mobile_Number", it) }
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here create signup input object
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("mobileNo", mobileNumber)
        }

        signInCall = apiInterface.mobileLogin(jsonObject)
        signInCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data

                    if (response.body()!!.statusCode != null) {
                        if (response.body()!!.statusCode == 2) {
                            navigationToOtpScreen(response.body()!!)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@SignInActivity, t.message.toString())
            }
        })
    }

    private fun navigationToOtpScreen(body: MobileSignUp) {
        body?.data?.otp?.let {
            Utils.saveStringPref(this, "User_OTP", it)
            Utils.showToast(this@SignInActivity, it)
            Log.d("OTP", it)
        }

        // toast message
        if (body.message.isNotEmpty()) {
            Utils.showToast(this, body.message)
        }

        body?.message?.let {
            if (it.equals("OTP Sent Successfully to Registered Email.")) {
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("SCREEN", "COMES_FROM_SIGN_IN")
                startActivity(intent)
                finish()
            }
        }
    }

}