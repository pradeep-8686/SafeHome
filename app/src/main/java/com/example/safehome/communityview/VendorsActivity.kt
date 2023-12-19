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
import com.example.safehome.adapter.VendorAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityVendorsBinding
import com.example.safehome.model.VendorTypeModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class VendorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendorsBinding
    private lateinit var vendorAdapter: VendorAdapter
    private var payUsingDialog: Dialog? = null

    private var vendorsList: ArrayList<VendorTypeModel.Data> = ArrayList()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getVendorsAPI: Call<VendorTypeModel>
    var Auth_Token: String? = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()

        clickEvents()
        getAllVendorsAPI()

    }

    private fun initData() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@VendorsActivity)
        Auth_Token = Utils.getStringPref(this@VendorsActivity, "Token", "")


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

    fun filter(text: String) {

        val myDuesList = ArrayList<VendorTypeModel.Data>()

        val courseAry: ArrayList<VendorTypeModel.Data> = vendorsList

        for (eachCourse in courseAry) {
            if (!eachCourse.name .isNullOrBlank() && eachCourse.name .lowercase(Locale.getDefault()).contains(text.lowercase(
                    Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        vendorAdapter.filterList(myDuesList);
    }


    private fun setVendorsAdapter() {

        if (vendorsList.isNotEmpty()){

            binding.vendorRecyclerView.visibility = View.VISIBLE
            binding.emptyTxt.visibility = View.GONE
            binding.vendorRecyclerView.layoutManager = LinearLayoutManager(this)
            vendorAdapter = VendorAdapter(this, vendorsList)
            binding.vendorRecyclerView.adapter = vendorAdapter
            vendorAdapter.setCallback(this)
        }else{
            binding.vendorRecyclerView.visibility = View.GONE
            binding.emptyTxt.visibility = View.VISIBLE
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun contactPopup(contact: VendorTypeModel.Data) {
        val layoutInflater: LayoutInflater =
           getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.emergency_contact_dialog, null)
        payUsingDialog = Dialog(this, R.style.CustomAlertDialog)
        payUsingDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payUsingDialog!!.setContentView(view)
        payUsingDialog!!.setCanceledOnTouchOutside(true)
        payUsingDialog!!.setCancelable(true)


        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val tvPrimaryNumber = view.findViewById<TextView>(R.id.tvPrimaryNumber)
        val tvSecondaryNumber = view.findViewById<TextView>(R.id.tvSecondaryNumber)
        val primaryCall = view.findViewById<ImageView>(R.id.primaryCall)
        val secondaryCall = view.findViewById<ImageView>(R.id.secondaryCall)
        val primaryMessage = view.findViewById<ImageView>(R.id.primaryMsg)
        val secondaryMessage = view.findViewById<ImageView>(R.id.secondaryMsg)

        /*if (contact.mobileNumber != null){

            tvPrimaryNumber.text = contact.mobileNumber
        }
        if (contact.secondaryNumber != null){

            tvSecondaryNumber.text = contact.secondaryNumber
        }
        primaryCall.setOnClickListener{
            makeCall(contact.mobileNumber)
        }
        secondaryCall.setOnClickListener{
            makeCall(contact.secondaryNumber)
        }
        primaryMessage.setOnClickListener {
            sendMessage(contact.mobileNumber)
        }
        secondaryMessage.setOnClickListener {
            sendMessage(contact.secondaryNumber)
        }*/



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
        customProgressDialog.progressDialogShow(this@VendorsActivity, this.getString(R.string.loading))

        getVendorsAPI = apiInterface.getVendorAPI("bearer " + Auth_Token)
        getVendorsAPI.enqueue(object : Callback<VendorTypeModel> {
            override fun onResponse(
                call: Call<VendorTypeModel>,
                response: Response<VendorTypeModel>
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
                                        response.body()!!.data as ArrayList<VendorTypeModel.Data>
                                    setVendorsAdapter()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@VendorsActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@VendorsActivity, response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<VendorTypeModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@VendorsActivity, t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onItemClick(vendorItem: VendorTypeModel.Data) {

        val intent = Intent(this, VendorsDetailsActivity::class.java)
        intent.putExtra("vendorItem", vendorItem)
        startActivity(intent)

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun makeCall(mobileNumber : String){
         try {
          val phoneNumber = Uri.parse("tel:${mobileNumber}")
          val dialIntent = Intent(Intent.ACTION_DIAL, phoneNumber)

          startActivity(dialIntent)
      } catch (error: Exception) {
          Utils.showToast(this@VendorsActivity, error.message.toString())
          println(error.message.toString())
      }
    }

    private fun sendMessage(mobileNumber :String){
         try {
           val phoneNumber = Uri.parse("smsto:${mobileNumber}")
           val smsIntent = Intent(Intent.ACTION_SENDTO, phoneNumber)

           startActivity(smsIntent)
       } catch (error: Exception) {
           Utils.showToast(this@VendorsActivity, error.message.toString())
           println(error.message.toString())
       }
    }
}