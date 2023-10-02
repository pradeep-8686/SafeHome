package com.example.safehome.meetings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityComplaintsBinding
import com.example.safehome.databinding.ActivityMeetingMinutesBinding
import com.example.safehome.databinding.ActivityRaiseComplaintBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class MeetingMinutesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeetingMinutesBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingMinutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            finish()
        }

        binding.okBtn.setOnClickListener {
            finish()
        }


    }
}