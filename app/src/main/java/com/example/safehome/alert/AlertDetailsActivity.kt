package com.example.safehome.alert

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAlertBinding
import com.example.safehome.databinding.ActivityAlertDetailsBinding
import com.example.safehome.forums.GetAllForumDetailsModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertDetailsBinding

    private lateinit var alertItem: AlertResponse.Data.Forum
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    private var alertDeleteDialog: Dialog? = null
    private lateinit var deleteAlertCall: Call<AlertDeleteResponse>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        if (intent != null) {
            alertItem = intent.getSerializableExtra("alertItem") as AlertResponse.Data.Forum
        }

        if (!alertItem.sentBy.isNullOrEmpty()) {
            if (alertItem.sentBy == "Resident") {
                binding.tvRaisedBy.text =
                    "By : ${alertItem.createdByName} Block " + alertItem.block + "-" + alertItem.flatNo
            } else {
                binding.tvRaisedBy.text = "By : ${alertItem.sentBy}"
            }
        }

        if (!alertItem.emergencyTypeName.isNullOrEmpty()) {
            binding.tvTopic.text = alertItem.emergencyTypeName
        }


        if (!alertItem.description.isNullOrEmpty()) {
            binding.tvForumDescription.text = "${alertItem.description}"
//           binding.tvForumDescription.setResizableText(holder.tvForumDescription.text.toString(),2,true)
        }

        binding.ivEdit.setOnClickListener {
            editAlert(alertItem)

        }


        binding.ivDelete.setOnClickListener {
            deleteAlert(alertItem)
        }

        Log.d("CreatorLoginId", "${alertItem.createdBy} == $User_Id")
        if (alertItem.createdBy == User_Id?.toInt()) {
            binding.ivEdit.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE

        } else {
            binding.ivEdit.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
        }


        binding.backBtnClick.setOnClickListener {
            onBackPressed()
        }
    }

    fun editAlert(alertItem: AlertResponse.Data.Forum) {

        val intent = Intent(this, CreateAlertActivity::class.java)
        intent.putExtra("alertItem", alertItem)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteAlert(alertItem: AlertResponse.Data.Forum) {

        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        alertDeleteDialog = Dialog(this, R.style.CustomAlertDialog)
        alertDeleteDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDeleteDialog!!.setContentView(view)
        alertDeleteDialog!!.setCanceledOnTouchOutside(true)
        alertDeleteDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDeleteDialog!!.window!!.attributes)


        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.setText("Are you sure you want to delete this Alert?")

        close.setOnClickListener {
            if (alertDeleteDialog!!.isShowing) {
                alertDeleteDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            deleteAlertServiceCall(alertItem)
            if (alertDeleteDialog!!.isShowing) {
                alertDeleteDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (alertDeleteDialog!!.isShowing) {
                alertDeleteDialog!!.dismiss()
            }
        }
        alertDeleteDialog!!.show()

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteAlertServiceCall(alertItem: AlertResponse.Data.Forum) {
        customProgressDialog.progressDialogShow(
            this@AlertDetailsActivity,
            this.getString(R.string.loading)
        )
        deleteAlertCall = apiInterface.deleteAlert("bearer " + Auth_Token, alertItem.alertId)
        deleteAlertCall.enqueue(object : Callback<AlertDeleteResponse> {
            override fun onResponse(
                call: Call<AlertDeleteResponse>,
                response: Response<AlertDeleteResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@AlertDetailsActivity,
                                        response.body()!!.message.toString()
                                    )
                                    onBackPressed()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@AlertDetailsActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<AlertDeleteResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@AlertDetailsActivity, t.message.toString())
            }

        })
    }


}