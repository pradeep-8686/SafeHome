package com.example.safehome.visitors.cab

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.databinding.ActivityCabBinding
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.GetAllVisitorDetailsModel
import com.example.safehome.visitors.VisitorActivity
import com.example.safehome.visitors.VisitorsListModel
import com.example.safehome.visitors.guest.AllowFrequentlyFragment
import com.example.safehome.visitors.guest.AllowOnceFragment

class CabActivity : BaseActivity() {

    private lateinit var binding: ActivityCabBinding
    private var User_Id: String? = ""
    private var Auth_Token: String? = ""
    private var ScreenFrom: String? = ""
    private var visitorListItem : GetAllVisitorDetailsModel.Data.Event ?= null

    //    private lateinit var visitorsListModel: VisitorsListModel
    private var visitorTypeId: Int? = null

    private var approvalStatus : ArrayList<ApprovalStatusModel.Data> ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras?.getInt("VisitorTypeId") != null){
            visitorTypeId = intent.extras?.getInt("VisitorTypeId")
        }
        if (intent.extras?.getSerializable("visitorListItem") != null){
            visitorListItem = intent.extras?.getSerializable("visitorListItem") as GetAllVisitorDetailsModel.Data.Event
        }
        if ( intent.getSerializableExtra("approvalStatus") != null){

            approvalStatus = intent.getSerializableExtra("approvalStatus") as ArrayList<ApprovalStatusModel.Data>
        }
        ScreenFrom = intent.getStringExtra("ScreenFrom")
//        visitorsListModel = intent.getSerializableExtra("visitorListItem") as VisitorsListModel
        // progressbar
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, VisitorActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        //load vehicle fragment
        replaceFragment(R.id.fragment_container, CabAllowOnceFragment(visitorTypeId, visitorListItem, approvalStatus))

        buttonClickEvents()


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {

        //here click events to load fragments
        binding.allowOnceBtn.setOnClickListener {
            binding.allowOnceBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            binding.allowFrequentlyBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, CabAllowOnceFragment( visitorTypeId, visitorListItem, approvalStatus))
        }

        binding.allowFrequentlyBtn.setOnClickListener {
            binding.allowFrequentlyBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            binding.allowOnceBtn.setBackgroundResource(0)

            replaceFragment(R.id.fragment_container, CabAllowFrequentlyFragment(visitorTypeId, visitorListItem, approvalStatus))
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