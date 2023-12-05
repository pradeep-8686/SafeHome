package com.example.safehome.alert

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityAlertBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    private var alertDeleteDialog: Dialog? = null
    private lateinit var alertAdapter: AlertAdapter
    private var getAlertList: ArrayList<AlertResponse.Data.Forum> = ArrayList()
    private lateinit var getAllAlertsDetailCall: Call<AlertResponse>
    private lateinit var deleteAlertCall: Call<AlertDeleteResponse>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        clickActions()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        getAllAlertsDetailsCall()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllAlertsDetailsCall() {
        customProgressDialog.progressDialogShow(
            this@AlertActivity,
            this.getString(R.string.loading)
        )
        getAllAlertsDetailCall = apiInterface.getAllAlertDetails("bearer " + Auth_Token, "1", "50")
        getAllAlertsDetailCall.enqueue(object : Callback<AlertResponse> {
            override fun onResponse(
                call: Call<AlertResponse>,
                response: Response<AlertResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (getAlertList.isNotEmpty()) {
                                        getAlertList.clear()
                                    }
                                    getAlertList = response.body()!!.data.forum
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                                populateData(getAlertList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@AlertActivity,
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

            override fun onFailure(call: Call<AlertResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@AlertActivity, t.message.toString())
            }

        })
    }

    private fun clickActions() {


        binding.ivAddAlert.setOnClickListener {
            val intent = Intent(this, CreateAlertActivity::class.java)
            startActivity(intent)
        }

    }

    private fun populateData(alertResponseModelList: ArrayList<AlertResponse.Data.Forum>) {
        //    upcomingList.clear()
        if (alertResponseModelList.size == 0) {
            binding.emptyTxt.visibility = View.VISIBLE
        } else {
            binding.emptyTxt.visibility = View.GONE
            binding.alertRecyclerView.layoutManager = LinearLayoutManager(this)
            alertAdapter =
                AlertAdapter(this, alertResponseModelList, User_Id!!)
            binding.alertRecyclerView.adapter = alertAdapter
            alertAdapter.setCallback(this@AlertActivity)
            alertAdapter.notifyDataSetChanged()
        }
    }

    fun editAlert(alertItem: AlertResponse.Data.Forum) {
//        val intent = Intent(this, UpdateAlertActivity::class.java)
//        startActivity(intent)

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
            this@AlertActivity,
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
                                        this@AlertActivity,
                                        response.body()!!.message.toString()
                                    )
                                    getAlertList.remove(alertItem)
                                    alertAdapter.notifyDataSetChanged()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@AlertActivity,
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
                Utils.showToast(this@AlertActivity, t.message.toString())
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("ScreenFrom", ScreenFrom)
        startActivity(intent)
        finish()
    }

}