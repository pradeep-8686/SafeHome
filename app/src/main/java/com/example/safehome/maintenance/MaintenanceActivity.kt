package com.example.safehome.maintenance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityMaintenanceBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class MaintenanceActivity : BaseActivity() {

    private lateinit var binding: ActivityMaintenanceBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintenanceBinding.inflate(layoutInflater)
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

        //load fragment
        replaceFragment(R.id.vehicle_fragment_container, MyDuesFragment())

        buttonClickEvents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.vehiclesBtn.setOnClickListener {
            binding.vehiclesBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.allottedSlotsBtn.setBackgroundResource(0)
            replaceFragment(R.id.vehicle_fragment_container, MyDuesFragment())
        }

        binding.allottedSlotsBtn.setOnClickListener {
            binding.vehiclesBtn.setBackgroundResource(0)
            binding.allottedSlotsBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            replaceFragment(R.id.vehicle_fragment_container, HistoryFragment())
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        if(supportFragmentManager.backStackEntryCount>0){
//            supportFragmentManager.popBackStackImmediate()
//        }else
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}