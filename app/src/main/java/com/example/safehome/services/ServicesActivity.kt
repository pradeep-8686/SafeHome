package com.example.safehome.services

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityServicesBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class ServicesActivity : BaseActivity() {
    private var from: String?= null
    private lateinit var binding: ActivityServicesBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }
        //load fragment
        from = intent.getStringExtra("from")
        if (from!= null){
            if (from.equals("bookings")){
                binding.paymentHistoryBtn.setBackgroundResource(0)
                binding.dailyHelpListFragBtn.setBackgroundResource(0)
                binding.dailyHelpBookingsFragBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
                replaceFragment(R.id.daily_help_fragment_container, ServicesBookingsFragment())
            }
        }else {
            replaceFragment(R.id.daily_help_fragment_container, ServicesListFragment())
        }
        buttonClickEvents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {
        //here click events to load fragments
        binding.dailyHelpListFragBtn.setOnClickListener {
            binding.dailyHelpListFragBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.dailyHelpBookingsFragBtn.setBackgroundResource(0)
            binding.paymentHistoryBtn.setBackgroundResource(0)
            replaceFragment(R.id.daily_help_fragment_container, ServicesListFragment())
        }

        binding.dailyHelpBookingsFragBtn.setOnClickListener {
            binding.paymentHistoryBtn.setBackgroundResource(0)
            binding.dailyHelpListFragBtn.setBackgroundResource(0)
            binding.dailyHelpBookingsFragBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            replaceFragment(R.id.daily_help_fragment_container, ServicesBookingsFragment())
        }

        binding.paymentHistoryBtn.setOnClickListener {
            binding.dailyHelpListFragBtn.setBackgroundResource(0)
            binding.dailyHelpBookingsFragBtn.setBackgroundResource(0)
            binding.paymentHistoryBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            replaceFragment(R.id.daily_help_fragment_container, ServicesPaymentHistoryFragment())
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}