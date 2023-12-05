package com.example.safehome.visitors.guest.selectguest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.databinding.ActivitySelectGuestBinding
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.guest.GuestActivity

class SelectGuestActivity : BaseActivity() {

    private var from: String?= null
    private lateinit var binding : ActivitySelectGuestBinding

    private var User_Id: String? = ""
    private var Auth_Token: String? = ""
    private var ScreenFrom: String? = ""
    private var approvalStatus : ArrayList<ApprovalStatusModel.Data> ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ScreenFrom = intent.getStringExtra("ScreenFrom")
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        from  = intent.getStringExtra("from")
        if ( intent.getSerializableExtra("approvalStatus") != null){

            approvalStatus = intent.getSerializableExtra("approvalStatus") as ArrayList<ApprovalStatusModel.Data>
        }
        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, GuestActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        replaceFragment(R.id.fragment_container, ContactsFragment(approvalStatus, from))

        buttonClickEvents()

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.contactsBtn.setOnClickListener {
            binding.contactsBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            binding.recentsBtn.setBackgroundResource(0)
         /*   val bundle = Bundle()
            bundle.putString("from", from)
            val contactFragment = ContactsFragment(approvalStatus, from)   */
            replaceFragment(R.id.fragment_container, ContactsFragment(approvalStatus, from))
        }

        binding.recentsBtn.setOnClickListener {
            binding.recentsBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.contactsBtn.setBackgroundResource(0)

            replaceFragment(R.id.fragment_container, RecentFragment(approvalStatus))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, GuestActivity::class.java)
        intent.putExtra("ScreenFrom", ScreenFrom)
        intent.putExtra("from", from)
        startActivity(intent)
        finish()
    }
}