package com.example.safehome.residentview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAddVehicleBinding
import com.example.safehome.fragments.AllottedVehiclesFragment
import com.example.safehome.fragments.VehiclesFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class AddVehicleActivity : BaseActivity() {

    private lateinit var binding: ActivityAddVehicleBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "residentId", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        //load vehicle fragment
        replaceFragment(R.id.vehicle_fragment_container, VehiclesFragment())

        buttonClickEvents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.vehiclesBtn.setOnClickListener {
            binding.vehiclesBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.allottedSlotsBtn.setBackgroundResource(0)
            replaceFragment(R.id.vehicle_fragment_container, VehiclesFragment())
        }

        binding.allottedSlotsBtn.setOnClickListener {
            binding.vehiclesBtn.setBackgroundResource(0)
            binding.allottedSlotsBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            replaceFragment(R.id.vehicle_fragment_container, AllottedVehiclesFragment())
        }
    }

}