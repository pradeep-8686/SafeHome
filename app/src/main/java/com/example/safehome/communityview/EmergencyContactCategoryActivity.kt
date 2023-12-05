package com.example.safehome.communityview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.CommunityBlockAdapter
import com.example.safehome.adapter.EmergencyContactAdapter
import com.example.safehome.adapter.EmergencyContactCategoryAdapter
import com.example.safehome.adapter.ResidentsAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityEmergencyContactCategoryBinding
import com.example.safehome.databinding.ActivityEmergencyInCommunityBinding
import com.example.safehome.databinding.ActivityResidentsBinding
import com.example.safehome.model.CommunityBlock
import com.example.safehome.model.DailyHelpHistoryModel
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.EmergencyContactCategory
import com.example.safehome.model.Residents
import com.example.safehome.policies.PoliciesAdapter
import com.example.safehome.policies.PoliciesModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class EmergencyContactCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergencyContactCategoryBinding
    private var blockList: ArrayList<CommunityBlock> = ArrayList()
    private lateinit var emergencyContactAdapter: EmergencyContactCategoryAdapter
    private var blockPopupWindow: PopupWindow? = null
    private var selectedBlock: String = ""

    private var emergrncyContactList: ArrayList<EmergencyContactCategory.Data> = ArrayList()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getEmergentContactAPI: Call<EmergencyContactCategory>
    var Auth_Token: String? = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyContactCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()

        clickEvents()
        getAllEmergencyContactAPI()

    }

    private fun initData() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@EmergencyContactCategoryActivity)
        Auth_Token = Utils.getStringPref(this@EmergencyContactCategoryActivity, "Token", "")
    }
    private fun clickEvents() {
        binding.backBtnClick.setOnClickListener {
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (emergrncyContactList.isNotEmpty()) {
                    filter(p0.toString())
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })



    }

    fun filter(text: String) {

        val myDuesList = ArrayList<EmergencyContactCategory.Data>()

        val courseAry: ArrayList<EmergencyContactCategory.Data> = emergrncyContactList

        for (eachCourse in courseAry) {
            if (!eachCourse.contactTypeName .isNullOrBlank() && eachCourse.contactTypeName .lowercase(Locale.getDefault()).contains(text.lowercase(
                    Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        emergencyContactAdapter.filterList(myDuesList);
    }


    private fun emergencyContactAdapter() {

        if (emergrncyContactList.isNotEmpty()){

            binding.emergencyContactsRecyclerView.visibility = View.VISIBLE
            binding.emptyTxt.visibility = View.GONE
            binding.emergencyContactsRecyclerView.layoutManager = LinearLayoutManager(this)
            emergencyContactAdapter = EmergencyContactCategoryAdapter(this, emergrncyContactList)
            binding.emergencyContactsRecyclerView.adapter = emergencyContactAdapter
            emergencyContactAdapter.setCallback(this)
        }else{
            binding.emergencyContactsRecyclerView.visibility = View.GONE
            binding.emptyTxt.visibility = View.VISIBLE
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllEmergencyContactAPI() {
        Log.e("Token", Auth_Token.toString())
        customProgressDialog.progressDialogShow(this@EmergencyContactCategoryActivity, this.getString(R.string.loading))
        getEmergentContactAPI = apiInterface.getEmergentContactCategoryAPI("bearer " + Auth_Token,)
        getEmergentContactAPI.enqueue(object : Callback<EmergencyContactCategory> {
            override fun onResponse(
                call: Call<EmergencyContactCategory>,
                response: Response<EmergencyContactCategory>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (emergrncyContactList.isNotEmpty()) {
                                        emergrncyContactList.clear()
                                    }
                                    emergrncyContactList =
                                        response.body()!!.data as ArrayList<EmergencyContactCategory.Data>
                                    emergencyContactAdapter()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@EmergencyContactCategoryActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@EmergencyContactCategoryActivity, response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<EmergencyContactCategory>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@EmergencyContactCategoryActivity, t.message.toString())
            }
        })
    }

    fun clickAction(contact: EmergencyContactCategory.Data) {
        val intent = Intent(this, EmergencyContactActivity::class.java)
        intent.putExtra("contactTypeId", contact.contactTypeId)
        startActivity(intent)
    }

}