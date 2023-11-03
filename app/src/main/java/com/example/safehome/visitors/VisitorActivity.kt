package com.example.safehome.visitors

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityVisitorBinding
import com.example.safehome.meetings.MeetingsCompletedFragment
import com.example.safehome.meetings.MeetingsUpcomingFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class VisitorActivity : BaseActivity() {

    private lateinit var visitorBinding: ActivityVisitorBinding
    private var User_Id: String? = ""
    private var Auth_Token: String? = ""
    private var ScreenFrom: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        visitorBinding = ActivityVisitorBinding.inflate(layoutInflater)
        setContentView(visitorBinding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")
        // progressbar
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        visitorBinding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        //load vehicle fragment
        replaceFragment(R.id.fragment_container, InviteVisitorsFragment())

        buttonClickEvents()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        visitorBinding.inviteVisitorsBtn.setOnClickListener {
            visitorBinding.inviteVisitorsBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.visitorListBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, InviteVisitorsFragment())
        }

        visitorBinding.visitorListBtn.setOnClickListener {
            visitorBinding.visitorListBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.inviteVisitorsBtn.setBackgroundResource(0)

            replaceFragment(R.id.fragment_container, VisitorsListFragment())
        }
    }

    override fun onBackPressed() {
         val intent = Intent(this, HomeActivity::class.java)
         startActivity(intent)
         finish()
    }

}