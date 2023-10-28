package com.example.safehome.facilitiesview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityFacilitiesBinding
import com.example.safehome.maintenance.MyDuesFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class FacilitiesActivity : BaseActivity() {

    private lateinit var binding: ActivityFacilitiesBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    var from: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacilitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")
        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        from = intent.getStringExtra("from")
        if (from!= null && from!!.isNotEmpty()) {
            if (from!! == "bookingsPage") {
                binding.bookingsBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
                binding.listBtn.setBackgroundResource(0)
                binding.historyBtn.setBackgroundResource(0)
                replaceFragment(R.id.vehicle_fragment_container, BookingsFragment())
            }
        }
        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        //load vehicle fragment
        replaceFragment(R.id.vehicle_fragment_container, ListFragment())

        buttonClickEvents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.listBtn.setOnClickListener {
            binding.listBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)

            binding.bookingsBtn.setBackgroundResource(0)
            binding.historyBtn.setBackgroundResource(0)
            replaceFragment(R.id.vehicle_fragment_container, ListFragment())
        }

        binding.bookingsBtn.setOnClickListener {
            binding.bookingsBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.listBtn.setBackgroundResource(0)
            binding.historyBtn.setBackgroundResource(0)

            replaceFragment(R.id.vehicle_fragment_container, BookingsFragment())
        }
        binding.historyBtn.setOnClickListener {
            binding.historyBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.listBtn.setBackgroundResource(0)
            binding.bookingsBtn.setBackgroundResource(0)

            replaceFragment(R.id.vehicle_fragment_container, FacilitiesHistoryFragment())
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}