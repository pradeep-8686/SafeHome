package com.example.safehome.residentview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.GridLayoutManager
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AddFamilyMemberAdapter
import com.example.safehome.adapter.AddFlatAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAddFlatBinding
import com.example.safehome.databinding.ActivityAddFlatMainBinding
import com.example.safehome.databinding.ActivityHomeBinding
import com.example.safehome.databinding.ActivityPersonalInfoBinding
import com.example.safehome.model.AllCommunities
import com.example.safehome.model.Block
import com.example.safehome.model.Blocks
import com.example.safehome.model.CommunityCities
import com.example.safehome.model.CommunityDetails
import com.example.safehome.model.CommunityStates
import com.example.safehome.model.FamilyDetails
import com.example.safehome.model.Flat
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.Tenant
import com.example.safehome.model.TenantDetails
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call

class AddFlatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFlatMainBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var addFlatCall: Call<TenantDetails>
    private lateinit var updaFlatCall: Call<MobileSignUp>
    private var flatList: ArrayList<Flat> = ArrayList()
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFlatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        })

        setupRecyclerView(flatList)
    }

    private fun setupRecyclerView(membersList: ArrayList<Flat>) {

        membersList.add(Flat(type = "new"))

        binding.addFlatRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            val communityCitiesAdapter =
                AddFlatAdapter(this@AddFlatActivity, membersList)

            binding.addFlatRecyclerView.adapter = communityCitiesAdapter
            communityCitiesAdapter.setCallback(this@AddFlatActivity)
        }

    }

    fun selectAddFlat(flat: Flat) {
        val intent = Intent(this, FlatActivity::class.java)
        startActivity(intent)
    }

    fun createNewFlatDetails(flat: Flat) {

    }
}