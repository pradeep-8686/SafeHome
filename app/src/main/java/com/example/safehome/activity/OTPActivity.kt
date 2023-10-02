package com.example.safehome.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityOtpactivityBinding
import com.example.safehome.model.Data
import com.example.safehome.model.LoginDetialsData
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.Vehicle
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.NullPointerException


class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpactivityBinding
    private var otp: String? = null
    private var mobileNumber: String? = null
    private lateinit var apiInterface: APIInterface
    private lateinit var signUpCall: Call<MobileSignUp>
    private lateinit var LoginDataCall: Call<LoginDetialsData>
    private var loginDetailList: ArrayList<LoginDetialsData> = ArrayList()
    private var enteredOtp: String? = null
    private var code1: String? = null
    private var code2: String? = null
    private var code3: String? = null
    private var code4: String? = null
    private var code5: String? = null
    private var code6: String? = null
    private var screenName: String? = null
    private lateinit var customProgressDialog: CustomProgressDialog
    var RESEND_TEXT_TIME: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progress bar
        customProgressDialog = CustomProgressDialog()

        screenName = intent.getStringExtra("SCREEN")
        setupApiService()
        getUserData()

        addTextWatcher(binding.otpEditText1)
        addTextWatcher(binding.otpEditText2)
        addTextWatcher(binding.otpEditText3)
        addTextWatcher(binding.otpEditText4)
        addTextWatcher(binding.otpEditText5)
        addTextWatcher(binding.otpEditText6)

        if (screenName.equals("COMES_FROM_SIGN_UP")) {
            binding.otpIndicateTextImg.visibility = View.VISIBLE
        } else {
            binding.otpIndicateTextImg.visibility = View.VISIBLE
        }

        binding.verifyBtn.setOnClickListener {
            verifyOtp()
        }

        binding.resendTxt.setOnClickListener {
            if (RESEND_TEXT_TIME.equals("FINISHED")) {
                RESEND_TEXT_TIME = "START"
                binding.resendTxt.visibility = View.VISIBLE
                binding.timeTxt.visibility = View.VISIBLE
                clearText()

                binding.resendBtn.background = getDrawable(R.drawable.verify_new_bg)
                //  binding.resendBtn.isEnabled = false

                if (screenName.equals("COMES_FROM_SIGN_UP")) {
                    resendSignUpServiceCall()
                } else {
                    resendLogInServiceCall()
                }

                remainingTime()
            }
        }

        checkforEnableVerifyBtn()
        remainingTime()
        //  resendButtonEnable()
        RESEND_TEXT_TIME?.let {
            //   binding.resendBtn.background = getDrawable(R.drawable.verify_new_bg)
        }.run {
            binding.resendBtn.background = getDrawable(R.drawable.verify_new_bg)
        }
    }

    private fun clearText() {
        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")
        binding.invalidOtpTxt.visibility = View.GONE
    }

    private fun resendButtonEnable() {
        RESEND_TEXT_TIME?.let {
            binding.resendBtn.isEnabled = it.equals("FINISHED")
            binding.resendBtn.isEnabled = true
        }.run {
            // disable the resend button
            remainingTime()
            binding.resendBtn.background = getDrawable(R.drawable.verify_new_bg)
            binding.resendBtn.isEnabled = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifyOtp() {
        // verify otp
        code1 = binding.otpEditText1.text.toString().trim()
        code2 = binding.otpEditText2.text.toString().trim()
        code3 = binding.otpEditText3.text.toString().trim()
        code4 = binding.otpEditText4.text.toString().trim()
        code5 = binding.otpEditText5.text.toString().trim()
        code6 = binding.otpEditText6.text.toString().trim()

        enteredOtp = code1 + code2 + code3 + code4 + code5 + code6

        if (enteredOtp?.isNotEmpty() == true || !enteredOtp.equals("")) {
            if (doOtpValidation()) {
                if (screenName.equals("COMES_FROM_SIGN_UP")) {
                    verifySignUpOtpServiceCall()
                } else {
                    verifyLogInOtpServiceCall()
                }
            } else {
                Utils.showToast(this@OTPActivity, this.getString(R.string.please_enter_valid_OTP))
            }
        } else {
            Utils.showToast(this@OTPActivity, this.getString(R.string.please_enter_OTP))
        }
    }

    private fun doOtpValidation(): Boolean {

        if (code1.isNullOrEmpty()) {
            return false
        }

        if (code2.isNullOrEmpty()) {
            return false
        }

        if (code3.isNullOrEmpty()) {
            return false
        }

        if (code4.isNullOrEmpty()) {
            return false
        }
        if (code5.isNullOrEmpty()) {
            return false
        }
        if (code6.isNullOrEmpty()) {
            return false
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifySignUpOtpServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here create verify otp input object
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("mobileNo", mobileNumber)
            addProperty("otp", enteredOtp)
        }

        signUpCall = apiInterface.verifySignUpOTP(jsonObject)
        signUpCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    try {
                        if (response.body()!!.statusCode != null) {
                            when (response.body()!!.statusCode) {
                                1 -> {
                                    navigationToPersonalInfoScreen()
                                }

                                2 -> {
                                    // invalid otp
                                    binding.invalidOtpTxt.visibility = View.VISIBLE
                                    response.body()?.message?.let {
                                        binding.invalidOtpTxt.visibility = View.VISIBLE
                                        binding.invalidOtpTxt.text = it.toString()
                                    }?.run {
                                        // Utils.showToast(this@OTPActivity, "Invalid OTP, Please try again")
                                    }

                                }

                                else -> {
                                    // default condition
//                                    Utils.showToast(
//                                        this@OTPActivity,
//                                        "Invalid OTP, Please try again"
//                                    )
                                    binding.invalidOtpTxt.visibility = View.VISIBLE
                                }
                            }
                        }
                    } catch (ex: NullPointerException) {
                        ex.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@OTPActivity, t.message.toString())
            }
        })
    }

    private fun navigationToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun navigationToPersonalInfoScreen() {
        // here nav logic
        val intent = Intent(this, PersonalInfoActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun setupApiService() {
        // here setup retrofit service
        apiInterface = APIClient.getClient(this)
    }

    private fun getUserData() {
        // here ave user data
        mobileNumber = Utils.getStringPref(this, "Mobile_Number", "")
        otp = Utils.getStringPref(this, "User_OTP", "")
    }

    private fun remainingTime() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val value: String = (millisUntilFinished / 1000).toString()
                if (value.length == 1) {
                    binding.timeTxt.text = "in " + "00:0" + millisUntilFinished / 1000
                    if (millisUntilFinished / 1000 == 0L) {
                        RESEND_TEXT_TIME = "FINISHED"
                        //  binding.resendBtn.isEnabled = true
                        binding.resendBtn.background = getDrawable(R.drawable.verify_new_red_bg)
                        binding.invalidOtpTxt.visibility = View.GONE
                    }
                } else {
                    binding.timeTxt.text = "in " + "00:" + millisUntilFinished / 1000
                }
            }

            override fun onFinish() {
                binding.timeTxt.text = "00:00"
            }
        }.start()

    }

    private fun addTextWatcher(one: EditText) {
        one.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                checkforEnableVerifyBtn()
                when (one.id) {
                    R.id.otp_edit_text1 -> if (one.length() == 1) {
                        binding.otpEditText2.requestFocus()
                    }

                    R.id.otp_edit_text2 -> if (one.length() == 1) {
                        binding.otpEditText3.requestFocus()
                    } else if (one.length() == 0) {
                        binding.otpEditText1.requestFocus()
                    }

                    R.id.otp_edit_text3 -> if (one.length() == 1) {
                        binding.otpEditText4.requestFocus()
                    } else if (one.length() == 0) {
                        binding.otpEditText2.requestFocus()
                    }

                    R.id.otp_edit_text4 -> if (one.length() == 1) {
                        binding.otpEditText5.requestFocus()
                    } else if (one.length() == 0) {
                        binding.otpEditText3.requestFocus()
                    }

                    R.id.otp_edit_text5 -> if (one.length() == 1) {
                        binding.otpEditText6.requestFocus()
                    } else if (one.length() == 0) {
                        binding.otpEditText4.requestFocus()
                    }

                    R.id.otp_edit_text6 -> if (one.length() == 1) {
                        val inputManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.hideSoftInputFromWindow(
                            this@OTPActivity.currentFocus!!.windowToken,
                            InputMethodManager.HIDE_NOT_ALWAYS
                        )
                    } else if (one.length() == 0) {
                        binding.otpEditText5.requestFocus()
                    }
                }
            }
        })
    }

    private fun checkforEnableVerifyBtn() {
        if (binding.otpEditText1.length() == 1 &&
            binding.otpEditText2.length() == 1 &&
            binding.otpEditText3.length() == 1 &&
            binding.otpEditText4.length() == 1 &&
            binding.otpEditText5.length() == 1 &&
            binding.otpEditText6.length() == 1
        ) {
            binding.verifyBtn.background = getDrawable(R.drawable.verify_new_red_bg)
        } else {
            binding.verifyBtn.background = getDrawable(R.drawable.verify_new_bg)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun resendSignUpServiceCall() {

        // progress bar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("mobileNo", mobileNumber)

        }
        // here resend otp service call
        signUpCall = apiInterface.resendSingUpOTP(jsonObject)

        signUpCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()

                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    if (response.body()!!.data.otp.isNotEmpty()) {
                        otp = response.body()!!.data.otp
                        Utils.showToast(this@OTPActivity, otp!!)
                    }
                    if (response.body()!!.message.isNotEmpty()) {
                        Utils.showToast(this@OTPActivity, response.body()!!.message)
                    }

                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                // here failure conditions
                Utils.showToast(this@OTPActivity, t.message.toString())

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifyLogInOtpServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here create verify otp input object
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("mobileNo", mobileNumber)
            addProperty("otp", enteredOtp)
        }

        signUpCall = apiInterface.verifyLoginOTP(jsonObject)
        signUpCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                if (response.isSuccessful && response.body() != null) {

                    try {

                        if (response.body()!!.statusCode != null) {
                            when (response.body()!!.statusCode) {
                                1 -> {
                                    if (response.body()?.data?.userId != null) {
                                        Log.d("USER_ID_", response.body()?.data?.userId.toString())
                                        Utils.saveStringPref(
                                            this@OTPActivity,
                                            "User_Id",
                                            response.body()?.data?.userId.toString()
                                        )
                                        Utils.saveStringPref(
                                            this@OTPActivity,
                                            "Token",
                                            response.body()?.data?.accessToken.toString()
                                        )
                                        Utils.savebooleanPref(this@OTPActivity, "IsLoggedIn", true)
                                    }
                                    getLoginUserDetails(
                                        response.body()?.data?.userId,
                                        response.body()?.data?.accessToken.toString()
                                    )
                                    // navigationToHomeScreen()
                                }

                                2 -> {
                                    // invalid otp

                                    binding.invalidOtpTxt.visibility = View.VISIBLE
                                    response.body()?.message?.let {
                                        // Utils.showToast(this@OTPActivity, it)

                                        binding.invalidOtpTxt.text = it.toString().replace(".", "")
                                    }?.run {
                                        // Utils.showToast(this@OTPActivity, "Invalid OTP, Please try again")
                                    }
                                    binding.invalidOtpTxt.visibility = View.VISIBLE
                                }

                                else -> {
                                    // default condition
                                    Utils.showToast(
                                        this@OTPActivity,
                                        "Invalid OTP, Please try again"
                                    )
                                    binding.invalidOtpTxt.visibility = View.VISIBLE
                                }
                            }
                        }
                    } catch (ex: NullPointerException) {
                        ex.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@OTPActivity, t.message.toString())

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLoginUserDetails(userId: Int?, Token: String) {
        userId?.let {
            customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

            LoginDataCall = apiInterface.getLoginUserDetails("bearer " + Token, userId.toInt())
            LoginDataCall.enqueue(object : Callback<LoginDetialsData> {
                override fun onResponse(
                    call: Call<LoginDetialsData>,
                    response: Response<LoginDetialsData>
                ) {

                    customProgressDialog.progressDialogDismiss()
                    if (response.isSuccessful && response.body() != null) {
                        try {
                            if (response.body()!!.statusCode != null) {
                                when (response.body()!!.statusCode) {
                                    1 -> {
                                        var Firstname: String? = null
                                        Log.e("UserName", response.body()?.data?.get(0)!!.firstName)
                                        if (response.body()?.data?.get(0)!!.firstName != null) {
                                            //  saveUserInfoData(response.body()?.data?.get(0)!!)
                                            try {
                                                response.body()?.data?.get(0)!!.firstName.toString()
                                                    ?.let {
                                                        Firstname = it
                                                    }
                                            } catch (ex: Exception) {
                                            }
                                            try {
                                                response.body()?.data?.get(0)!!.lastName.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "userName",
                                                            Firstname + " " + it
                                                        )
                                                    }
                                            } catch (ex: Exception) {
                                            }

                                            try {
                                                response.body()?.data?.get(0)!!.residentId.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "residentId",
                                                            it
                                                        )
                                                    }
                                            } catch (ex: Exception) {
                                            }

                                            try {
                                                response.body()?.data?.get(0)!!.email.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "userEmail",
                                                            it
                                                        )
                                                    }

                                            } catch (ex: Exception) {
                                            }
                                            try {
                                                response.body()?.data?.get(0)!!.mobileNo.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "mobileNo",
                                                            it
                                                        )
                                                    }
                                            } catch (ex: Exception) {
                                            }
                                            try {
                                                response.body()?.data?.get(0)!!.communityName.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "communityName",
                                                            it
                                                        )
                                                    }
                                            } catch (ex: Exception) {
                                            }
                                            try {
                                                response.body()?.data?.get(0)!!.block.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "block",
                                                            it
                                                        )
                                                    }
                                            } catch (ex: Exception) {
                                            }
                                            try {
                                                response.body()?.data?.get(0)!!.flatNo.toString()
                                                    ?.let {
                                                        Utils.saveStringPref(
                                                            this@OTPActivity,
                                                            "flatNo",
                                                            it
                                                        )
                                                    }
                                            } catch (ex: Exception) {
                                            }

                                            Utils.savebooleanPref(
                                                this@OTPActivity,
                                                "IsLoggedIn",
                                                true
                                            )
                                        }

                                        if(response.body()?.data?.get(0)!!.residentId.toInt()>0){
                                            navigationToHomeScreen()
                                        }
                                    }

                                    2 -> {
                                        // invalid otp
                                        response.body()?.message?.let {
                                            Utils.showToast(this@OTPActivity, it)
                                        }?.run {
                                            // Utils.showToast(this@OTPActivity, "Invalid OTP, Please try again")
                                        }
                                    }

                                    else -> {
                                        // default condition
                                        Utils.showToast(
                                            this@OTPActivity,
                                            "Invalid OTP, Please try again"
                                        )
                                    }
                                }
                            }
                        } catch (ex: NullPointerException) {
                            ex.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginDetialsData>, t: Throwable) {
                    customProgressDialog.progressDialogDismiss()
                    // here failure conditions
                    Utils.showToast(this@OTPActivity, t.message.toString())
                }
            })
        }
    }

    private fun saveUserInfoData(get: Data) {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun resendLogInServiceCall() {

        // progress bar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("mobileNo", mobileNumber)

        }
        // here resend otp service call
        signUpCall = apiInterface.resendLoginOTP(jsonObject)

        signUpCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()

                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    if (response.body()!!.data.otp.isNotEmpty()) {
                        otp = response.body()!!.data.otp
                        Utils.showToast(this@OTPActivity, otp!!)
                    }
                    if (response.body()!!.message.isNotEmpty()) {
                        Utils.showToast(this@OTPActivity, response.body()!!.message)
                    }
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                // here failure conditions
                Utils.showToast(this@OTPActivity, t.message.toString())
            }
        })
    }


}