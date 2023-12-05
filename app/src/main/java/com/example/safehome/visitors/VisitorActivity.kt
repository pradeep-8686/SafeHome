package com.example.safehome.visitors

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
import com.example.safehome.databinding.ActivityVisitorBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitorActivity : BaseActivity() {

    private lateinit var visitorBinding: ActivityVisitorBinding
    private var User_Id: String? = ""
    private var Auth_Token: String? = ""
    private var ScreenFrom: String? = ""
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getAllApprovalStatusTypes: Call<ApprovalStatusModel>
    private var getAllApprovalStatusList = ArrayList<ApprovalStatusModel.Data>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        visitorBinding = ActivityVisitorBinding.inflate(layoutInflater)
        setContentView(visitorBinding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")
        // progressbar
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)

        visitorBinding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        getAllPollsDetailsCall()
        buttonClickEvents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun buttonClickEvents() {
        //here click events to load fragments
        visitorBinding.inviteVisitorsBtn.setOnClickListener {
            visitorBinding.inviteVisitorsBtn.background =
                getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.visitorListBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, InviteVisitorsFragment(getAllApprovalStatusList))
        }

        visitorBinding.visitorListBtn.setOnClickListener {
            visitorBinding.visitorListBtn.background = getDrawable(R.drawable.rectangler_vrify_bg)
            visitorBinding.inviteVisitorsBtn.setBackgroundResource(0)
            replaceFragment(R.id.fragment_container, VisitorsListFragment(getAllApprovalStatusList))
        }
    }

    override fun onBackPressed() {
         val intent = Intent(this, HomeActivity::class.java)
         startActivity(intent)
         finish()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllPollsDetailsCall() {
        customProgressDialog.progressDialogShow(this@VisitorActivity, this.getString(R.string.loading))
        getAllApprovalStatusTypes = apiInterface.getAllApprovalStatusTypes("bearer "+Auth_Token)
        getAllApprovalStatusTypes.enqueue(object: Callback<ApprovalStatusModel> {
            override fun onResponse(
                call: Call<ApprovalStatusModel>,
                response: Response<ApprovalStatusModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (getAllApprovalStatusList.isNotEmpty()) {
                                        getAllApprovalStatusList.clear()
                                    }
                                    getAllApprovalStatusList = response.body()!!.data as ArrayList<ApprovalStatusModel.Data>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                                replaceFragment(R.id.fragment_container, InviteVisitorsFragment(getAllApprovalStatusList))

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@VisitorActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<ApprovalStatusModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@VisitorActivity, t.message.toString())
            }

        })
    }

}