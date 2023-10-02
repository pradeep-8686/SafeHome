package com.example.safehome.complaints

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.dailyhelp.DailyHelpBookingsFragment
import com.example.safehome.dailyhelp.DailyHelpListFragment
import com.example.safehome.dailyhelp.DailyHelpPaymentHistoryFragment
import com.example.safehome.databinding.ActivityComplaintsBinding
import com.example.safehome.databinding.ActivityDailyHelpBinding
import com.example.safehome.eventsview.EventsHistoryFragment
import com.example.safehome.eventsview.UpcomingFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class ComplaintsActivity  : BaseActivity() {

        private lateinit var binding: ActivityComplaintsBinding
        private lateinit var customProgressDialog: CustomProgressDialog
        private lateinit var apiInterface: APIInterface
        var User_Id: String? = ""
        var Auth_Token: String? = ""
    var ScreenFrom: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")
        // progressbar
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

        //load vehicle fragment
        replaceFragment(R.id.vehicle_fragment_container, PersonalFragment())

        buttonClickEvents()

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.personalBtn.setOnClickListener {
            binding.personalBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.communityBtn.setBackgroundResource(0)
            replaceFragment(R.id.vehicle_fragment_container, PersonalFragment())
        }

        binding.communityBtn.setOnClickListener {
            binding.communityBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.personalBtn.setBackgroundResource(0)

            replaceFragment(R.id.vehicle_fragment_container, CommunityFragment())
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}