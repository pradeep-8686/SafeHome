package com.example.safehome.visitors.guest

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityGuestBinding
import com.example.safehome.databinding.ActivityVisitorBinding
import com.example.safehome.meetings.MeetingsCompletedFragment
import com.example.safehome.meetings.MeetingsUpcomingFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.GetAllVisitorDetailsModel
import com.example.safehome.visitors.VisitorActivity
import com.example.safehome.visitors.guest.selectguest.ContactsModel

class GuestActivity : BaseActivity() {

    private lateinit var visitorBinding: ActivityGuestBinding
    private var User_Id: String? = ""
    private var residentId: String? = ""
    private var Auth_Token: String? = ""
    private var ScreenFrom: String? = ""
    private var selectedContact : ContactsModel ?= null
    private var visitorTypeId : Int ?= null
    private var visitorListItem : GetAllVisitorDetailsModel.Data.Event ?= null
    private var approvalStatus : ArrayList<ApprovalStatusModel.Data> ? =null
    private var from: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        visitorBinding = ActivityGuestBinding.inflate(layoutInflater)
        setContentView(visitorBinding.root)

        if ( intent.getStringExtra("ScreenFrom") != null)
        {

            ScreenFrom = intent.getStringExtra("ScreenFrom")
        }
        if (intent.getStringExtra("from")!= null){
            from = intent.getStringExtra("from")!!
        }
        if ( intent.getSerializableExtra("approvalStatus") != null){

            approvalStatus = intent.getSerializableExtra("approvalStatus") as ArrayList<ApprovalStatusModel.Data>
        }

        // progressbar
        User_Id = Utils.getStringPref(this, "User_Id", "")
        residentId = Utils.getStringPref(this, "residentId", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        Log.e("residentId" , residentId!!)
        Log.e("residentId" , User_Id!!)
        Log.e("residentId" , Auth_Token!!)

        if (intent.extras?.getSerializable("selectedContact") != null){
            selectedContact = intent.extras?.getSerializable("selectedContact") as ContactsModel

        }
        if (intent.extras?.getInt("VisitorTypeId") != null){

            visitorTypeId = intent.extras?.getInt("VisitorTypeId")
        }
        if (intent.extras?.getSerializable("visitorListItem") != null){
            visitorListItem = intent.extras?.getSerializable("visitorListItem") as GetAllVisitorDetailsModel.Data.Event
        }
        visitorBinding.backBtnClick.setOnClickListener {
            val intent = Intent(this, VisitorActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }
        if (!from.isNullOrEmpty()){
            if(from == "AllowFrequentlyFragment"){
                visitorBinding.allowFrequentlyBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
                visitorBinding.allowOnceBtn.setBackgroundResource(0)
                replaceFragment(R.id.fragment_container, AllowFrequentlyFragment(selectedContact, visitorTypeId, visitorListItem, approvalStatus))
            }else{
                visitorBinding.allowOnceBtn.background =
                    getDrawable(R.drawable.rectangler_vrify_bg)
                visitorBinding.allowFrequentlyBtn.setBackgroundResource(0)
                replaceFragment(R.id.fragment_container, AllowOnceFragment(selectedContact, visitorTypeId, visitorListItem, approvalStatus))
            }
        }else{
            visitorBinding.allowOnceBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.allowFrequentlyBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, AllowOnceFragment(selectedContact, visitorTypeId, visitorListItem, approvalStatus))
        }
        //load vehicle fragment
        buttonClickEvents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        visitorBinding.allowOnceBtn.setOnClickListener {
            visitorBinding.allowOnceBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.allowFrequentlyBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, AllowOnceFragment(selectedContact, visitorTypeId, visitorListItem, approvalStatus))
        }

        visitorBinding.allowFrequentlyBtn.setOnClickListener {
            visitorBinding.allowFrequentlyBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.allowOnceBtn.setBackgroundResource(0)

            replaceFragment(R.id.fragment_container, AllowFrequentlyFragment(selectedContact, visitorTypeId, visitorListItem, approvalStatus))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, VisitorActivity::class.java)
        intent.putExtra("ScreenFrom", ScreenFrom)
        startActivity(intent)
        finish()
    }
}