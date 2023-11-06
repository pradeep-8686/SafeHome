package com.example.safehome.visitors.guest.selectguest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.databinding.ActivitySelectGuestBinding
import com.example.safehome.visitors.VisitorActivity
import com.example.safehome.visitors.guest.AllowFrequentlyFragment
import com.example.safehome.visitors.guest.AllowOnceFragment
import com.example.safehome.visitors.guest.GuestActivity

class SelectGuestActivity : BaseActivity() {

    private lateinit var binding : ActivitySelectGuestBinding

    private var User_Id: String? = ""
    private var Auth_Token: String? = ""
    private var ScreenFrom: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ScreenFrom = intent.getStringExtra("ScreenFrom")
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, GuestActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        replaceFragment(R.id.fragment_container, ContactsFragment())

        buttonClickEvents()

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.contactsBtn.setOnClickListener {
            binding.contactsBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            binding.recentsBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, ContactsFragment())
        }

        binding.recentsBtn.setOnClickListener {
            binding.recentsBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.contactsBtn.setBackgroundResource(0)

            replaceFragment(R.id.fragment_container, RecentFragment())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, GuestActivity::class.java)
        intent.putExtra("ScreenFrom", ScreenFrom)
        startActivity(intent)
        finish()
    }
}