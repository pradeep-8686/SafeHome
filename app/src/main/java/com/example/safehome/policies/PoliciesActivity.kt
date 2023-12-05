package com.example.safehome.policies

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityPoliciesBinding
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.Notice
import com.example.safehome.model.NoticesListModel
import com.example.safehome.notice.NoticeDetailsActivity
import com.example.safehome.notice.NoticesAdapter
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PoliciesActivity : BaseActivity() {

    private lateinit var binding : ActivityPoliciesBinding
    private var policiesList: ArrayList<PoliciesModel.Data.CommunityPolicy> = ArrayList()
    private lateinit var policiesAdapter: PoliciesAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getPoliciesCall: Call<PoliciesModel>
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoliciesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        clickEvents()
        getAllPoliciesAPI()
    }

    private fun clickEvents() {
        binding.backBtnClick.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

    }

    private fun initData() {
        ScreenFrom = intent.getStringExtra("ScreenFrom")
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@PoliciesActivity)
        Auth_Token = Utils.getStringPref(this@PoliciesActivity, "Token", "")
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllPoliciesAPI() {
        Log.e("Token", Auth_Token.toString())
        customProgressDialog.progressDialogShow(this@PoliciesActivity, this.getString(R.string.loading))

        getPoliciesCall = apiInterface.getPoliciesAPI("bearer " + Auth_Token,)
        getPoliciesCall.enqueue(object : Callback<PoliciesModel> {
            override fun onResponse(
                call: Call<PoliciesModel>,
                response: Response<PoliciesModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (policiesList.isNotEmpty()) {
                                        policiesList.clear()
                                    }
                                    policiesList =
                                        response.body()!!.data.communityPolicies as ArrayList<PoliciesModel.Data.CommunityPolicy>
                                    populateData()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@PoliciesActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@PoliciesActivity, response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<PoliciesModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@PoliciesActivity, t.message.toString())
            }
        })
    }

    private fun populateData() {


        if (policiesList.isNotEmpty()){

            binding.policiesRecyclerview.visibility = View.VISIBLE
            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.policiesRecyclerview.layoutManager = LinearLayoutManager(this@PoliciesActivity)

            policiesAdapter = PoliciesAdapter(this@PoliciesActivity, policiesList)
            binding.policiesRecyclerview.adapter = policiesAdapter
            policiesAdapter.setCallback(this@PoliciesActivity)
        }else{
            binding.policiesRecyclerview.visibility = View.GONE
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        }
    }
    fun noticesItemClick(policiesModel : PoliciesModel.Data.CommunityPolicy) {
//        noticeAlertDialog(noticesListModel)
        val intent = Intent(this, PoliciesDetailsActivity::class.java)
        intent.putExtra("policiesModel", policiesModel)
        startActivity(intent)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("ScreenFrom", ScreenFrom)
        startActivity(intent)
        finish()
    }

}