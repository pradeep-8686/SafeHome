package com.example.safehome.notice

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityNoticeDetailsBinding
import com.example.safehome.model.Notice
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeDetailsActivity : AppCompatActivity() {

    private lateinit var noticeBinding: ActivityNoticeDetailsBinding
    var noticeData: Notice? = null
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var updateReadStatusServicesCall: Call<JsonObject>
    private var Auth_Token: String? = null
    private var residentId: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noticeBinding = ActivityNoticeDetailsBinding.inflate(layoutInflater)
        setContentView(noticeBinding.root)

        noticeData = intent.getSerializableExtra("NoticeData") as? Notice

        inIt()

        readStatusSendToNotice(noticeData?.readStatusId)
        noticeBinding.backBtnClick.setOnClickListener { onBackPressed() }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun readStatusSendToNotice(readStatusId: Int?) {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        updateReadStatusServicesCall =
            apiInterface.updateNoticeRead("bearer " + Auth_Token, readStatusId!! )
        updateReadStatusServicesCall.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.has("statusCode") != null) {
                        when (response.body()!!.has("statusCode").toString() ) {
                            "1" -> {
                                if (response.body()!!.has("message") != null && response.body()!!.has("message").toString().isNotEmpty()) {
//                                        Utils.showToast(
//                                            this@NoticeDetailsActivity,
//                                            response.body()!!.has("message").toString()
//                                        )
                                }
                            }

                            else -> {
                                if (response.body()!!.has("message").toString() != null && response.body()!!.has("message").toString().isNotEmpty()) {
//                                        Utils.showToast(
//                                            this@NoticeDetailsActivity,
//                                            response.body()!!.has("message").toString().toString()
//                                        )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@NoticeDetailsActivity, t.message.toString())
            }

        })
    }

    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        Auth_Token = Utils.getStringPref(this, "Token", "")
        residentId = Utils.getStringPref(this, "residentId", "")!!

        noticeBinding.noticeTypeTxt.text = noticeData?.noticeTypeName
        noticeBinding.noticesDescriptionTxt.text = noticeData?.description
        noticeBinding.noticePostedTimeTxt.text = noticeData?.createdOn
        noticeBinding.postedby.text = noticeData?.postedBy
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}