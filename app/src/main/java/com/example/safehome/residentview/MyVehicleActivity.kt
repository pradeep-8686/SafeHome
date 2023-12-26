package com.example.safehome.residentview

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AddVehicleAdapter
import com.example.safehome.adapter.VehicleModelAdapter
import com.example.safehome.adapter.VehicleTypeAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityMyVehicleBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.Vehicle
import com.example.safehome.model.VehicleDetails
import com.example.safehome.model.VehicleModel
import com.example.safehome.model.VehicleModelResp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyVehicleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyVehicleBinding
    private lateinit var addMyFamilyCall: Call<MobileSignUp>
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private var vehicleNumber: String = ""
    private var vehicleTypeList: ArrayList<String> = ArrayList()
    private var VehicleModelList: ArrayList<VehicleModelResp> = ArrayList()
    private var vehilceTypePopupWindow: PopupWindow? = null
    private var vehicleModelPopupWindow: PopupWindow? = null
    private var selectedVehicleType: String? = null
    private var selectedVehicleModel: String? = null
    private lateinit var vehicleModelCall: Call<VehicleModel>


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "residentId", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.vehicleNumberEt.setBackgroundResource(android.R.color.transparent)

        binding.selectVehicleTypeHeader.text =
            Html.fromHtml(getString(R.string.vehicle_type) + "" + "<font color='white'>*</font>")
        binding.vehicleModelHeader.text =
            Html.fromHtml(getString(R.string.Vehicle_Model) + "" + "<font color='white'>*</font>")
        binding.vehicleNumberHeader.text =
            Html.fromHtml(getString(R.string.vehicle_number_hint) + "" + "<font color='white'>*</font>")

        buttonClickEvents()
        addvehicleTypeList()

        binding.backBtnClick.setOnClickListener {
            finish()
        }
    }

    private fun addvehicleTypeList() {
        vehicleTypeList.add("2 Wheeler")
        vehicleTypeList.add("4 Wheeler")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buttonClickEvents() {
        binding.cancelBtn.setOnClickListener {
            binding.vehicleNumberEt.setText("")
            binding.selectVehilceTxt.setText("")
            binding.selectVehilceModelTxt.setText("")
            selectedVehicleModel = ""
            selectedVehicleType = ""
            binding.selectVehilceModelErrorCase.visibility = View.GONE
            binding.selectVehilceTypeErrorCase.visibility = View.GONE
        }

        binding.selectVehicleTypeCl.setOnClickListener {
            if (vehicleModelPopupWindow != null) {
                if (vehicleModelPopupWindow!!.isShowing) {
                    vehicleModelPopupWindow!!.dismiss()
                }
            }

            if (vehilceTypePopupWindow != null) {
                if (vehilceTypePopupWindow!!.isShowing) {
                    vehilceTypePopupWindow!!.dismiss()
                } else {
                    vehilceTypeDropDown()
                }
            } else {
                vehilceTypeDropDown()
            }
        }

        binding.selectVehicleModelCl.setOnClickListener {
            if (vehilceTypePopupWindow != null) {
                if (vehilceTypePopupWindow!!.isShowing) {
                    vehilceTypePopupWindow!!.dismiss()
                }
            }
            if (vehicleModelPopupWindow != null) {
                if (vehicleModelPopupWindow!!.isShowing) {
                    vehicleModelPopupWindow!!.dismiss()
                } else {
                    vehilceModelDropDown()
                }
            } else {
                vehilceModelDropDown()
            }
        }


        binding.saveBtn.setOnClickListener {
            vehicleNumber = binding.vehicleNumberEt.text.toString().trim()

            if (verifyData()) {
                saveMyVehicleServiceCall()
            }
        }
    }

    private fun vehilceModelDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        vehicleModelPopupWindow = PopupWindow(
            view,
            binding.selectVehilceModelTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (VehicleModelList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val vehicleModelAdapter = VehicleModelAdapter(this, VehicleModelList)

            dropDownRecyclerView.adapter = vehicleModelAdapter
            vehicleModelAdapter.setCallback(this@MyVehicleActivity)
        }
        vehicleModelPopupWindow!!.elevation = 10F
        vehicleModelPopupWindow!!.showAsDropDown(binding.selectVehilceModelTxt, 0, 0, Gravity.CENTER)
    }

    private fun vehilceTypeDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        vehilceTypePopupWindow = PopupWindow(
            view,
            binding.selectVehilceTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
            //(Utils.screenHeight * 0.3).toInt()
        )
        if (vehicleTypeList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val vehicleTypeAdapter = VehicleTypeAdapter(this, vehicleTypeList)

            dropDownRecyclerView.adapter = vehicleTypeAdapter
            vehicleTypeAdapter.setCallback(this@MyVehicleActivity)
        }
        vehilceTypePopupWindow!!.elevation = 10F
        vehilceTypePopupWindow!!.showAsDropDown(binding.selectVehilceTxt, 0, 0, Gravity.CENTER)
    }

    private fun verifyData(): Boolean {
        var vehicle_number = binding.vehicleNumberEt.text.toString().trim()
        binding.vehicleNumberEt.addTextChangedListener(textWatcher);

        if (vehicle_number.isNullOrEmpty()) {
            binding.vehilceNumberErrorCase.visibility = View.VISIBLE
        }

        if (binding.selectVehilceTxt.text.isNullOrEmpty()) {
            binding.selectVehilceTypeErrorCase.visibility = View.VISIBLE
        }

        if (binding.selectVehilceModelTxt.text.isNullOrEmpty()) {
            binding.selectVehilceModelErrorCase.visibility = View.VISIBLE
        }

        if (vehicle_number!!.isNotEmpty()) {
            return true
        } else {
            return false
        }

        return true
    }

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (binding.vehicleNumberEt.text.hashCode() === s.hashCode()) {
                binding.vehilceNumberErrorCase.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveMyVehicleServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("ResidentId", User_Id)
            addProperty("ParkingLotNumber", "110")
            addProperty("VehicleType", selectedVehicleType)
            addProperty("VehicleModel", selectedVehicleModel)
            addProperty("VehicleNumber", vehicleNumber)
            addProperty("StickerIssued", "YES")
        }

        addMyFamilyCall = apiInterface.addMyVehicle(
            "bearer " + Auth_Token,
            jsonObject
        )

        addMyFamilyCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    navigationToAddVehicleScreen(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@MyVehicleActivity, t.message.toString())
            }
        })
    }

    private fun navigationToAddVehicleScreen(body: MobileSignUp) {
        if (body.statusCode == 1) {
            val intent = Intent(this, AddVehicleActivity::class.java)
            startActivity(intent)
            finish()
        } else if (body.statusCode == 2) {

            // already registered
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectVehicleType(s: String) {
        binding.selectVehilceTypeErrorCase.visibility = View.GONE
        if (vehilceTypePopupWindow != null) {
            if (vehilceTypePopupWindow!!.isShowing) {
                vehilceTypePopupWindow!!.dismiss()
            }
        }
        if (s.isNotEmpty()) {
            selectedVehicleType = s
            binding.selectVehilceTxt.text = s
            var vehicleType: String="0"
                try {
                    if (selectedVehicleType!!.split(" ")[0] == "2") {
                        vehicleType = "1"
                    } else if (selectedVehicleType!!.split(" ")[0] == "4") {
                        vehicleType = "2"
                    }
                }catch (ex: Exception){
                    vehicleType = "2"
                }
            getAllVehicleModelServieCall(vehicleType)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectVehicleModel(s: String) {
        binding.selectVehilceModelErrorCase.visibility = View.GONE
        if (vehicleModelPopupWindow != null) {
            if (vehicleModelPopupWindow!!.isShowing) {
                vehicleModelPopupWindow!!.dismiss()
            }
        }
        if (s.isNotEmpty()) {
            selectedVehicleModel = s
            binding.selectVehilceModelTxt.text = s
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllVehicleModelServieCall(vehicleType: String) {
        //progressbar
        customProgressDialog.progressDialogShow(
            this@MyVehicleActivity,
            this.getString(R.string.loading)
        )

        // here sign up service call
        vehicleModelCall = apiInterface.getAllVehicleModels(
            "Bearer " + Auth_Token,
            vehicleType.toInt()
        )
        vehicleModelCall.enqueue(object : Callback<VehicleModel> {
            override fun onResponse(
                call: Call<VehicleModel>,
                response: Response<VehicleModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (VehicleModelList.isNotEmpty()) {
                            VehicleModelList.clear()
                        }
                        VehicleModelList = response.body()!!.data as ArrayList<VehicleModelResp>
                        if (VehicleModelList.size > 0) {
                            // vehilceModelDropDown()
                        }
                    } else {
                        // vehilceModelDropDown()
                    }
                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<VehicleModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@MyVehicleActivity, t.message.toString())
            }
        })
    }

}