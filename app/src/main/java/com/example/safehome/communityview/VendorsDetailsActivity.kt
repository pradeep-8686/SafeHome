package com.example.safehome.communityview

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.VendorDetailsAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityVendorsBinding
import com.example.safehome.databinding.ActivityVendorsDetailsBinding
import com.example.safehome.model.VendorDetailsModel
import com.example.safehome.model.VendorTypeModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class VendorsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendorsDetailsBinding
    private lateinit var vendorAdapter: VendorDetailsAdapter
    private var payUsingDialog: Dialog? = null

    private var vendorsList: ArrayList<VendorDetailsModel.Data> = ArrayList()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getVendorsAPI: Call<VendorDetailsModel>
    var Auth_Token: String? = ""
    private var vendorItem: VendorTypeModel.Data? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        initData()

        clickEvents()
        getAllVendorsAPI()

        binding.callIcon.setOnClickListener{
            if (vendorsList.isNotEmpty()){
                contactPopup(vendorsList[0])
            }
        }

    }

    private fun getIntentData() {

        if (intent.extras?.getSerializable("vendorItem") != null) {
            vendorItem = intent.extras?.getSerializable("vendorItem") as VendorTypeModel.Data

            binding.serviceTxt.text = vendorItem!!.name

        }

    }

    private fun initData() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@VendorsDetailsActivity)
        Auth_Token = Utils.getStringPref(this@VendorsDetailsActivity, "Token", "")


    }

    private fun clickEvents() {
        binding.backBtnClick.setOnClickListener {
            onBackPressed()
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (vendorsList.isNotEmpty()) {
                    filter(p0.toString())
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun filter(text: String) {

        val myDuesList = ArrayList<VendorDetailsModel.Data>()

        val courseAry: ArrayList<VendorDetailsModel.Data> = vendorsList

        for (eachCourse in courseAry) {
            if (!eachCourse.serviceProvided.isNullOrBlank() && eachCourse.serviceProvided.lowercase(
                    Locale.getDefault()
                ).contains(
                    text.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                myDuesList.add(eachCourse)
            }
        }

        vendorAdapter.filterList(myDuesList);
    }


    private fun setVendorsAdapter() {

        if (vendorsList.isNotEmpty()) {

            binding.vendorRecyclerView.visibility = View.VISIBLE
            binding.emptyTxt.visibility = View.GONE
            binding.vendorRecyclerView.layoutManager = LinearLayoutManager(this)
            vendorAdapter = VendorDetailsAdapter(this, vendorsList)
            binding.vendorRecyclerView.adapter = vendorAdapter
        } else {
            binding.vendorRecyclerView.visibility = View.GONE
            binding.emptyTxt.visibility = View.VISIBLE
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun contactPopup(item: VendorDetailsModel.Data) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.service_call_dialog, null)
        payUsingDialog = Dialog(this, R.style.CustomAlertDialog)
        payUsingDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payUsingDialog!!.setContentView(view)
        payUsingDialog!!.setCanceledOnTouchOutside(true)
        payUsingDialog!!.setCancelable(true)


        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val tvPrimaryNumber = view.findViewById<TextView>(R.id.tvPrimaryNumber)
        val primaryCall = view.findViewById<ImageView>(R.id.primaryCall)
        val primaryMessage = view.findViewById<ImageView>(R.id.primaryMsg)


        if (item.mobileNumber != null) {
            tvPrimaryNumber.text = item.mobileNumber
        }
        primaryCall.setOnClickListener {
            makeCall(item.mobileNumber)
        }

        primaryMessage.setOnClickListener {
            sendMessage(item.mobileNumber)
        }




        close.setOnClickListener {
            if (payUsingDialog!!.isShowing) {
                payUsingDialog!!.dismiss()
            }
        }


        payUsingDialog!!.show()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllVendorsAPI() {
        Log.e("Token", Auth_Token.toString())
        customProgressDialog.progressDialogShow(
            this@VendorsDetailsActivity,
            this.getString(R.string.loading)
        )

        getVendorsAPI =
            apiInterface.getVendorDetailsAPI("bearer " + Auth_Token, vendorItem?.vendorTypeId!!)
        getVendorsAPI.enqueue(object : Callback<VendorDetailsModel> {
            override fun onResponse(
                call: Call<VendorDetailsModel>,
                response: Response<VendorDetailsModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (vendorsList.isNotEmpty()) {
                                        vendorsList.clear()
                                    }
                                    vendorsList =
                                        response.body()!!.data as ArrayList<VendorDetailsModel.Data>
                                    setVendorsAdapter()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@VendorsDetailsActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(
                        this@VendorsDetailsActivity,
                        response.body()!!.message.toString()
                    )
                }
            }

            override fun onFailure(call: Call<VendorDetailsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@VendorsDetailsActivity, t.message.toString())
            }
        })
    }

    private fun makeCall(mobileNumber: String) {
        try {
            val phoneNumber = Uri.parse("tel:${mobileNumber}")
            val dialIntent = Intent(Intent.ACTION_DIAL, phoneNumber)

            startActivity(dialIntent)
        } catch (error: Exception) {
            Utils.showToast(this@VendorsDetailsActivity, error.message.toString())
            println(error.message.toString())
        }
    }

    private fun sendMessage(mobileNumber: String) {
        try {
            val phoneNumber = Uri.parse("smsto:${mobileNumber}")
            val smsIntent = Intent(Intent.ACTION_SENDTO, phoneNumber)

            startActivity(smsIntent)
        } catch (error: Exception) {
            Utils.showToast(this@VendorsDetailsActivity, error.message.toString())
            println(error.message.toString())
        }
    }
}