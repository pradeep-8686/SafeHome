package com.example.safehome.communityview

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AssociationMemberAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAssociationMembersBinding
import com.example.safehome.model.AssociationMember
import com.example.safehome.model.GetAllAssociationMembersModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AssociationMembersActivity : AppCompatActivity() {

    private var blockID: String?= null
    private var flatId: String?= null
    private var userName: String?= null
    private var associationMembersList: ArrayList<GetAllAssociationMembersModel.Data.AssociationMember> = ArrayList()
    private lateinit var getAssociationMembersCall: Call<GetAllAssociationMembersModel>
    private lateinit var binding: ActivityAssociationMembersBinding
    private var memberList: ArrayList<AssociationMember> = ArrayList()
    private lateinit var associationMemberAdapter: AssociationMemberAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAssociationMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inIt()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        ScreenFrom = intent.getStringExtra("ScreenFrom")
        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        userName = Utils.getStringPref(this@AssociationMembersActivity, "userName", "")
        flatId =  Utils.getStringPref(this, "flatId", "")
        blockID = Utils.getStringPref(this, "blockID", "")
        Log.e("IDDS", "$flatId : $blockID")
        clickEvents()
      //  addMemberData()
        getAssociationMembersServiceCall()
        //populateMemberData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAssociationMembersServiceCall() {
        customProgressDialog.progressDialogShow(this@AssociationMembersActivity, this.getString(R.string.loading))
        getAssociationMembersCall = apiInterface.getAllAssociatonMembers("bearer "+Auth_Token,1, 10)
        getAssociationMembersCall.enqueue(object: Callback<GetAllAssociationMembersModel> {
            override fun onResponse(
                call: Call<GetAllAssociationMembersModel>,
                response: Response<GetAllAssociationMembersModel>
            ) {
                if (response.isSuccessful && response.body()!= null){

                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (associationMembersList.isNotEmpty()){
                                    associationMembersList.clear()
                                }
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@AssociationMembersActivity, response.body()!!.message.toString())
                                    associationMembersList = response.body()!!.data.associationMembers as ArrayList<GetAllAssociationMembersModel.Data.AssociationMember>
                                    Log.e("associationMembersList", ""+associationMembersList.size)
                                    populateMemberData(associationMembersList)
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@AssociationMembersActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{

                    customProgressDialog.progressDialogDismiss()
                }
            }

            override fun onFailure(call: Call<GetAllAssociationMembersModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@AssociationMembersActivity, t.message.toString())

            }

        })
    }

    private fun clickEvents() {

        binding.backBtnClick.setOnClickListener {
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun populateMemberData(associationMembersList: ArrayList<GetAllAssociationMembersModel.Data.AssociationMember>) {

        if (associationMembersList.size == 0) {
            binding.emptyAssociateMembersTxt.visibility = View.VISIBLE
        } else {
            binding.emptyAssociateMembersTxt.visibility = View.GONE
            binding.associationMemberRecyclerView.layoutManager = LinearLayoutManager(this)
            associationMemberAdapter = AssociationMemberAdapter(this, associationMembersList)
            binding.associationMemberRecyclerView.adapter = associationMemberAdapter
            associationMemberAdapter.setCallback(this@AssociationMembersActivity)
            associationMemberAdapter.notifyDataSetChanged()
        }

    }

    fun makeCall(associationMember: GetAllAssociationMembersModel.Data.AssociationMember) {
        try {
            val phoneNumber = Uri.parse("tel:${associationMember.mobileNo}")
            val dialIntent = Intent(Intent.ACTION_DIAL, phoneNumber)

            startActivity(dialIntent)
        } catch (error: Exception) {
            Utils.showToast(this@AssociationMembersActivity, error.message.toString())
            println(error.message.toString())
        }
    }

    fun sendMsg(associationMember: GetAllAssociationMembersModel.Data.AssociationMember) {
        try {
            val phoneNumber = Uri.parse("smsto:${associationMember.mobileNo}")
            val smsIntent = Intent(Intent.ACTION_SENDTO, phoneNumber)

            startActivity(smsIntent)
        } catch (error: Exception) {
            Utils.showToast(this@AssociationMembersActivity, error.message.toString())
            println(error.message.toString())
        }
    }

    /*private fun addMemberData() {
        memberList.add(AssociationMember("Admin", "Tejaswini Pasham", "D-101"))
        memberList.add(AssociationMember("President", "Tejaswini Pasham", "D-101"))
        memberList.add(AssociationMember("Treasurer", "Tejaswini Pasham", "D-101"))
    }*/
}