package com.example.safehome.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AddVehicleAdapter
import com.example.safehome.adapter.VehicleModelAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentVehiclesBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.Vehicle
import com.example.safehome.model.VehicleDetails
import com.example.safehome.model.VehicleModel
import com.example.safehome.model.VehicleModelResp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.residentview.MyVehicleActivity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VehiclesFragment : Fragment() {

    private lateinit var binding: FragmentVehiclesBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var vehicleCall: Call<VehicleDetails>
    private lateinit var deleteVehicleCall: Call<MobileSignUp>
    private lateinit var updateVehicleCall: Call<MobileSignUp>
    private var vehiclesList: ArrayList<Vehicle> = ArrayList()
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private var vehicleDialog: Dialog? = null
    private var deleteVehicleDialog: Dialog? = null
    private var editVehicleDialog: Dialog? = null
    private var vehicleId = 0
    private var selectedVehicleModel: String? = null
    private var selectedVehicleType: String? = null
    private var vehicleModelPopupWindow: PopupWindow? = null
    private lateinit var vehicle_model_tv: TextView
    private lateinit var cl_vehicle_model: ConstraintLayout
    private lateinit var vehicle_number_et: EditText
    private lateinit var vehicleModelErrorCase: TextView
    private lateinit var vehicleNumberErrorCase: TextView
    private lateinit var profile_picture: ImageView
    private lateinit var mobileNumberErrorCase: TextView
    private var vehicleModel: String? = null
    private var vehicleNumber: String? = null
    private lateinit var vehicleModelCall: Call<VehicleModel>
    private var VehicleModelList: ArrayList<VehicleModelResp> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVehiclesBinding.inflate(inflater, container, false)

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

        getVehiclesServiceCall()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getVehiclesServiceCall() {
        vehiclesList.clear()
        //progressbar
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        // here sign up service call
        vehicleCall = apiInterface.getAllVehiclesByUserId(
            "bearer " + Auth_Token,
            User_Id!!.toInt()
        )
        vehicleCall.enqueue(object : Callback<VehicleDetails> {
            override fun onResponse(
                call: Call<VehicleDetails>,
                response: Response<VehicleDetails>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (vehiclesList.isNotEmpty()) {
                            vehiclesList.clear()
                        }
                        vehiclesList = response.body()!!.data as ArrayList<Vehicle>
                        if (vehiclesList.size > 0) {
                            setupRecyclerView(vehiclesList)
                        }
                    } else {
                        // membersList.add(FamilyDetails(type = "new"))
                        setupRecyclerView(vehiclesList)
                    }
                } else {
                    // membersList.add(FamilyDetails(type = "new"))
                    setupRecyclerView(vehiclesList)
                }
            }

            override fun onFailure(call: Call<VehicleDetails>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

    }

    private fun setupRecyclerView(vehiclesList: ArrayList<Vehicle>) {

        vehiclesList.add(Vehicle(type = "new"))

        binding.addVehicleRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            val addVehicleAdapter =
                AddVehicleAdapter(requireContext(), vehiclesList)

            binding.addVehicleRecyclerView.adapter = addVehicleAdapter
            addVehicleAdapter.setCallback(this@VehiclesFragment)
        }

    }

    fun selectAddMember(vehicle: Vehicle) {
        val intent = Intent(requireContext(), MyVehicleActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun longPressMember(vehicle: Vehicle) {
        // here edit or delete pop up functionality

        if (vehicle != null) {
            selectedVehiclePopup(vehicle)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun selectedVehiclePopup(vehicle: Vehicle) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.vehicle_dialog_popup, null)
        vehicleDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        vehicleDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        vehicleDialog!!.setContentView(view)
        vehicleDialog!!.setCanceledOnTouchOutside(true)
        vehicleDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(vehicleDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        vehicleDialog!!.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close)
        val vehicle_type: TextView = view.findViewById(R.id.vehicle_type)
        val vehicle_model: TextView = view.findViewById(R.id.vehicle_model)
        val vehicle_number: TextView = view.findViewById(R.id.vehicle_number)
        val vehicle_picture: ImageView = view.findViewById(R.id.vehicle_picture)

        val edit: TextView = view.findViewById(R.id.edit_btn)
        val delete: TextView = view.findViewById(R.id.delete_btn)


        if (vehicle.vehicleType != null) {
            if (vehicle.vehicleType.isNotEmpty()) {
                vehicle_type.text = vehicle.vehicleType
            }
        }

        try {
            if (vehicle.vehicleType.toString().split("-")[0].equals("2")) {
                vehicle_picture.setImageResource(R.drawable.bike_with_circle)
            } else {
                vehicle_picture.setImageResource(R.drawable.car_with_bg1)
            }
        }catch (ex: Exception){
            vehicle_picture.setImageResource(R.drawable.car_with_bg1)
        }

        if (vehicle.vehicleModel != null) {
            if (vehicle.vehicleModel.isNotEmpty()) {
                vehicle_model.text = vehicle.vehicleModel
            }
        }

        if (vehicle.vehicleNumber != null) {
            if (vehicle.vehicleNumber.isNotEmpty()) {
                vehicle_number.text = vehicle.vehicleNumber
            }
        }

        close.setOnClickListener {
            if (vehicleDialog!!.isShowing) {
                vehicleDialog!!.dismiss()
            }
        }

        //edit button click
        edit.setOnClickListener {
            if (vehicleDialog!!.isShowing) {
                vehicleDialog!!.dismiss()
            }
            editVehiclePopup(vehicle)
        }

        //delete button click
        delete.setOnClickListener {
            if (vehicleDialog!!.isShowing) {
                vehicleDialog!!.dismiss()
            }
            deleteVehiclePopup(vehicle)
        }
        vehicleDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun editVehiclePopup(vehicle: Vehicle) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.edit_vehicle_dialog_popup, null)
        editVehicleDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        editVehicleDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        editVehicleDialog!!.setContentView(view)
        editVehicleDialog!!.setCanceledOnTouchOutside(true)
        editVehicleDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(editVehicleDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        editVehicleDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        cl_vehicle_model = view.findViewById<ConstraintLayout>(R.id.cl_vehicle_model)
        vehicle_model_tv = view.findViewById<EditText>(R.id.select_vehicle_model_txt)
        vehicle_model_tv.setBackgroundResource(android.R.color.transparent)

        vehicle_number_et = view.findViewById<EditText>(R.id.vehicle_number_et)
        vehicle_number_et.setBackgroundResource(android.R.color.transparent)

        val updateTv = view.findViewById<TextView>(R.id.update) as TextView

        vehicleModelErrorCase = view.findViewById(R.id.vehicle_model_error_case) as TextView
        vehicleNumberErrorCase = view.findViewById(R.id.vehicle_number_error_case) as TextView
        profile_picture = view.findViewById(R.id.profile_picture) as ImageView

        try {
            if (vehicle.vehicleType.toString().split("-")[0].equals("2")) {
                profile_picture.setImageResource(R.drawable.bike_with_circle)
            } else {
                profile_picture.setImageResource(R.drawable.car_with_bg1)
            }
        }catch (ex: Exception){
            profile_picture.setImageResource(R.drawable.car_with_bg1)
        }

        if (vehicle.vehicleModel != null) {
            if (vehicle.vehicleModel.isNotEmpty()) {
                vehicleModel = vehicle.vehicleModel
                vehicle_model_tv.text = vehicle.vehicleModel
            }
        }
        if (vehicle.vehicleNumber != null) {
            if (vehicle.vehicleNumber.isNotEmpty()) {
                vehicleNumber = vehicle.vehicleNumber
                vehicle_number_et.setText(vehicleNumber)
            }
        }

        var selectedVehicleType: String = ""
        if (vehicle.vehicleType.isNotEmpty()) {
            selectedVehicleType = vehicle.vehicleType
            var vehicleType = "0"
            if(selectedVehicleType.split("-")[0].equals("2")){
                vehicleType = "1"
            } else {
                vehicleType = "2"
            }
            // here vehicle model api call
            getAllVehicleModelServiceCall(vehicleType)
        }

        selectedVehicleModel = vehicle.vehicleModel
        cl_vehicle_model.setOnClickListener {
            if (vehicleModelPopupWindow != null) {
                if (vehicleModelPopupWindow!!.isShowing) {
                    vehicleModelPopupWindow!!.dismiss()
                } else {
                    vehicleModelDropDown()
                }
            } else {
                vehicleModelDropDown()
            }
        }

        close.setOnClickListener {
            if (editVehicleDialog!!.isShowing) {
                editVehicleDialog!!.dismiss()
            }
        }
        updateTv.setOnClickListener {
            verifyData(vehicle)
        }

        editVehicleDialog!!.show()
    }


    private fun vehicleModelDropDown() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        vehicleModelPopupWindow = PopupWindow(
            view,
            vehicle_model_tv.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (VehicleModelList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val vehicleModelAdapter = VehicleModelAdapter(requireContext(), VehicleModelList)

            dropDownRecyclerView.adapter = vehicleModelAdapter
            vehicleModelAdapter.setFragmentCallback(this@VehiclesFragment)
        }
        vehicleModelPopupWindow!!.elevation = 10F
        vehicleModelPopupWindow!!.showAsDropDown(vehicle_model_tv, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectVehicleModel(s: String) {
        // binding.selectVehilceModelErrorCase.visibility = View.GONE
        if (vehicleModelPopupWindow != null) {
            if (vehicleModelPopupWindow!!.isShowing) {
                vehicleModelPopupWindow!!.dismiss()
            }
        }
        if (s.isNotEmpty()) {
            selectedVehicleModel = s
            vehicle_model_tv.text = s
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun deleteVehiclePopup(vehicle: Vehicle) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_vehicle_dialog_popup, null)
        deleteVehicleDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        deleteVehicleDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteVehicleDialog!!.setContentView(view)
        deleteVehicleDialog!!.setCanceledOnTouchOutside(true)
        deleteVehicleDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(deleteVehicleDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        deleteVehicleDialog!!.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)

        close.setOnClickListener {

            if (deleteVehicleDialog!!.isShowing) {
                deleteVehicleDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            deleteVehicleServiceCall(vehicle)
        }

        no.setOnClickListener {
            if (deleteVehicleDialog!!.isShowing) {
                deleteVehicleDialog!!.dismiss()
            }
        }
        deleteVehicleDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteVehicleServiceCall(vehicle: Vehicle) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        if (vehicle.vehicleId != null) {
            vehicleId = vehicle.vehicleId
        }
        deleteVehicleCall = apiInterface.deleteVehicle("bearer " + Auth_Token, vehicleId)
        deleteVehicleCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    deleteVehicleResponseStatus(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteVehicleResponseStatus(body: MobileSignUp) {
        if (body.statusCode == 1) {
            vehiclesList.clear()
            if (deleteVehicleDialog!!.isShowing) {
                deleteVehicleDialog?.dismiss()
            }
            if (body.message.isNotEmpty()) {
                Utils.showToast(requireContext(), body.message)
            }
            getVehiclesServiceCall()

        } else if (body.statusCode == 2) {

            // already registered
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(requireContext(), body.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifyData(vehicle: Vehicle) {
        // here get sign up user data
        vehicleModel = vehicle_model_tv.text.toString().trim()
        vehicle_model_tv.addTextChangedListener(textWatcher);

        vehicleNumber = vehicle_number_et.text.toString().trim()
        vehicle_number_et.addTextChangedListener(textWatcher)

        if (doUserValidation()) {
            updateVehicleServiceCall(vehicle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateVehicleServiceCall(vehicle: Vehicle) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        if (vehicle.vehicleId != null) {
            vehicleId = vehicle.vehicleId
        }
        if (vehicle.vehicleType != null) {
            if (vehicle.vehicleType.isNotEmpty()) {
                selectedVehicleType = vehicle.vehicleType
            }
        }
        // here update dynamically
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("ResidentId", User_Id)
            addProperty("VehicleType", selectedVehicleType)
            addProperty("VehicleModel", selectedVehicleModel)
            addProperty("VehicleNumber", vehicleNumber)
            addProperty("VehicleId", vehicleId)
        }

        updateVehicleCall = apiInterface.updateMyVehicle("bearer " + Auth_Token, jsonObject)
        updateVehicleCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    updateResponseStatus(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateResponseStatus(body: MobileSignUp) {
        if (body.statusCode == 1) {

            if (editVehicleDialog!!.isShowing) {
                editVehicleDialog?.dismiss()
            }
            if (body.message.isNotEmpty()) {
                Utils.showToast(requireContext(), body.message)
            }
            getVehiclesServiceCall()

        } else if (body.statusCode == 2) {

            // already registered
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(requireContext(), body.message)
            }
        }
    }

    private fun doUserValidation(): Boolean {

        if (vehicleModel.isNullOrEmpty()) {
            vehicleModelErrorCase.visibility = View.VISIBLE
        }

        if (vehicleNumber.isNullOrEmpty()) {
            vehicleNumberErrorCase.visibility = View.VISIBLE
        }

        if (vehicleModel!!.isNotEmpty() && vehicleNumber?.isNotEmpty()!!) {
            return true
        } else {
            return false
        }

        return true
    }


    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (vehicle_model_tv.text.hashCode() === s.hashCode()) {
                vehicleModelErrorCase.visibility = View.GONE
            } else if (vehicle_number_et.text.hashCode() === s.hashCode()) {
                vehicleNumberErrorCase.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllVehicleModelServiceCall(vehicleType: String) {
        //progressbar
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

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
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

}