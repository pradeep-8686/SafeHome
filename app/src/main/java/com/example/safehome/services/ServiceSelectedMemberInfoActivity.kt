package com.example.safehome.services

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.dailyhelp.DailyHelpActivity
import com.example.safehome.databinding.ActivityServiceSelectedMemberInfoBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.ServiceDataList
import com.example.safehome.model.ServiceProvidedTypesList
import com.example.safehome.model.ServicesBookingsList
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ServiceSelectedMemberInfoActivity : AppCompatActivity() {
    private var from: String? = null
    private var serviceTypeId: String?= null
    private lateinit var serviceProvidedByTypeCall: Call<ServiceProvidedTypesList>
    private var sevicesProvidedTypesList: ArrayList<ServiceProvidedTypesList.Data> = ArrayList()
    private var selectProvidedTypesPopWindow: PopupWindow? = null
    private var availabilityOn: String?= null
    private var time: String?= null
    private var ServiceSelectedSpinner: String?= null
    private var residentId: String?= null
    private var Auth_token: String?= null
    private lateinit var apiInterface: APIInterface
    private lateinit var bookServiceCall: Call<AddServiceBookingList>
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var binding: ActivityServiceSelectedMemberInfoBinding
    var servicesdataMemberList: ServiceDataList.Data? = null
    var servicesBookingsList: ServicesBookingsList.Data? =null
    private var bookingConfirmDialog: Dialog? = null
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    var serviceTypeName: String? = null
    private lateinit var updatebookServiceCall: Call<AddServiceBookingList>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceSelectedMemberInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceTypeName = intent.getStringExtra("serviceTypeName").toString()
         ServiceSelectedSpinner = intent.getStringExtra("ServiceSelectedSpinner").toString()
        availabilityOn = intent.getStringExtra("availabilityOn")
        serviceTypeId = intent.getStringExtra("serviceTypeId")
        try {
            servicesdataMemberList = intent.getSerializableExtra("servicesMemberList") as ServiceDataList.Data?
            from = intent.getStringExtra("from")
            servicesBookingsList = intent.getSerializableExtra("servicesBookingsList") as ServicesBookingsList.Data?
        } catch (ex: Exception) {
        }


        try {
            if(from!= null){
                if (from == "update") {
                    if (servicesBookingsList!!.serviceTypeName.isNotEmpty()) {
                        binding.tittleTxt.text = servicesBookingsList!!.serviceTypeName
                    }
                    if (servicesBookingsList!!.personName.isNotEmpty()){
                        binding.memberName.text = servicesBookingsList!!.personName
                    }
                    serviceTypeId = servicesBookingsList!!.serviceTypeId.toString()
                }else{
                    binding.memberName.text = servicesdataMemberList!!.personName
                    binding.tittleTxt.text = serviceTypeName
                }
            }

        }catch (e: Exception){
            binding.memberName.text = servicesdataMemberList?.personName
            binding.tittleTxt.text = serviceTypeName
        }

        binding.memberMobileNumber.text = servicesdataMemberList?.mobileNo
      //  binding.serviceFromSelectedSpinner.setText(ServiceSelectedSpinner)
        if(availabilityOn!= null) {
            if (availabilityOn!!.isNotEmpty()) {
                binding.availabileOnText.text = availabilityOn
            } else {
                binding.availabileOnText.text = "Weekdays"
            }
        }
       // binding.selectServiveProvideTv.setText(ServiceSelectedSpinner)
        inIt()

        binding.clServiceProvider.setOnClickListener {
            if (selectProvidedTypesPopWindow!= null) {
                if (selectProvidedTypesPopWindow!!.isShowing) {
                    selectProvidedTypesPopWindow!!.dismiss()
                } else {
                    selectProvideTypesNetworkCall()
                }
            } else {
                selectProvideTypesNetworkCall()
            }
        }
        binding.cancelBtn.setOnClickListener {
            val intent = Intent(this, ServicesMemberListActivity::class.java)
            intent.putExtra("serviceTypeId", serviceTypeId!!.toInt())
            intent.putExtra("serviceTypeName", serviceTypeName)
            startActivity(intent)
            finish()

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun selectProvideTypesNetworkCall() {
        customProgressDialog.progressDialogShow(this@ServiceSelectedMemberInfoActivity, this.getString(R.string.loading))
        serviceProvidedByTypeCall = apiInterface.getServiceProvidedByServiceType("bearer "+Auth_token, serviceTypeId!!.toInt())
        serviceProvidedByTypeCall.enqueue(object : Callback<ServiceProvidedTypesList>{
            override fun onResponse(call: Call<ServiceProvidedTypesList>, response: Response<ServiceProvidedTypesList>) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                   // Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                    sevicesProvidedTypesList = response.body()!!.data as ArrayList<ServiceProvidedTypesList.Data>
                                    selectProvideTypesPopupWindow()
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                     }
            }

            override fun onFailure(call: Call<ServiceProvidedTypesList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ServiceSelectedMemberInfoActivity, t.message.toString())
                 }

        })    }

    private fun selectProvideTypesPopupWindow() {
            val layoutInflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

            selectProvidedTypesPopWindow = PopupWindow(
                view,
                binding.selectServiveProvideTv.measuredWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            if (sevicesProvidedTypesList.isNotEmpty()) {
                val linearLayoutManager = LinearLayoutManager(this)
                val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
                dropDownRecyclerView.layoutManager = linearLayoutManager
                val servicesProvidedTypesListAdapter = ServicesProvidedTypesListAdapter(this, sevicesProvidedTypesList)
                dropDownRecyclerView.adapter = servicesProvidedTypesListAdapter
                servicesProvidedTypesListAdapter.setCallbackServiceType(this@ServiceSelectedMemberInfoActivity)
            }
            selectProvidedTypesPopWindow!!.elevation = 10F
            selectProvidedTypesPopWindow!!.showAsDropDown(binding.selectServiveProvideTv, 0, 0, Gravity.CENTER)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        binding.RatingBar.rating = 3.5F

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@ServiceSelectedMemberInfoActivity)
        Auth_token = Utils.getStringPref(this@ServiceSelectedMemberInfoActivity, "Token", "")!!
        residentId = Utils.getStringPref(this@ServiceSelectedMemberInfoActivity, "residentId", "")!!

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, ServicesActivity::class.java)
            intent.putExtra("serviceTypeId", serviceTypeId)
            intent.putExtra("serviceTypeName", serviceTypeName)
            startActivity(intent)
            finish()
        }
        binding.bookBtn.setOnClickListener {
            Booking()
        }

        binding.startDateTxt?.setOnClickListener {
            val startDateDialog =  DatePickerDialog(this,
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH))

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }


        binding.startTimeText?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this,
                { timePicker, selectedHour, selectedMinute ->
                    binding.startTimeText.text = String.format("%02d:%02d %s", selectedHour, selectedMinute,
                        if (selectedHour < 12) "AM" else "PM"
                    )
                },
                hour,
                minute,
                false
            ) //Yes 12 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }


    }

//    @RequiresApi(Build.VERSION_CODES.Q)
//    @SuppressLint("MissingInflatedId")
@RequiresApi(Build.VERSION_CODES.Q)
fun Booking() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        bookingConfirmDialog = Dialog(this, R.style.CustomAlertDialog)
        bookingConfirmDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookingConfirmDialog!!.setContentView(view)
        bookingConfirmDialog!!.setCanceledOnTouchOutside(true)
        bookingConfirmDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(bookingConfirmDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        bookingConfirmDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.setText("Are you sure you want to book him/her to the flat?")

        close.setOnClickListener {
            if (bookingConfirmDialog!!.isShowing) {
                bookingConfirmDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            // deleteTenantServiceCall(tenant)
            if (from.equals("update")){
                updateServiceBookingListServiceCall(servicesBookingsList!!.serviceId, servicesBookingsList!!.serviceBookingId)
            }else {
                addServiceBookingNetworkCall()
            }
         //   addServiceBookingNetworkCall()

            if (bookingConfirmDialog!!.isShowing) {
                bookingConfirmDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (bookingConfirmDialog!!.isShowing) {
                bookingConfirmDialog!!.dismiss()
            }
        }
        bookingConfirmDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addServiceBookingNetworkCall() {
        customProgressDialog.progressDialogShow(this@ServiceSelectedMemberInfoActivity, this.getString(R.string.loading))
        var date = binding.startDateTxt.text.toString()
        if(!date.contains("DD/MM/YYYY")){
            date = Utils.changeDateFormatToMMDDYYYY(date)
        }
        var time : String = binding.startTimeText.text.toString().replace(":", "-")
        val jsonObject = JsonObject()
        jsonObject.addProperty("ServiceId", servicesdataMemberList!!.serviceId)
        jsonObject.addProperty("Date", date)
        jsonObject.addProperty("Time", time)
        jsonObject.addProperty("BookFor", "individual resident")
        jsonObject.addProperty("ResidentId", residentId!!.toInt())

        bookServiceCall = apiInterface.AddServiceBooking("bearer "+Auth_token, jsonObject)
        bookServiceCall.enqueue(object: Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                        moveToServiesActivity()
                    }
                }else{
               //     Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                    customProgressDialog.progressDialogDismiss()
                    moveToServiesActivity()
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                moveToServiesActivity()
                Utils.showToast(this@ServiceSelectedMemberInfoActivity, t.message.toString())
            }

        })
    }

    fun moveToServiesActivity(){
        val intent = Intent(this@ServiceSelectedMemberInfoActivity, ServicesActivity::class.java)
        intent.putExtra("from", "bookings")
        startActivity(intent)

    }
    // create an OnDateSetListener
    val dateSetListener_book_now_start_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal_startDate.set(Calendar.YEAR, year)
            cal_startDate.set(Calendar.MONTH, monthOfYear)
            cal_startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis
            view.setMinDate(disablePastDates)

            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.startDateTxt!!.text = sdf.format(cal_startDate.getTime())
        }
    }


    private fun setTime(Time: String, selectedHour: Int, selectedMinute: Int) {
        try {
          /*  var hour_str: String? = null
            if (selectedHour < 10) {
                hour_str = "0" + selectedHour
            } else {
                hour_str = selectedHour.toString()
            }

            var minute_str: String? = null
            if (selectedMinute < 10) {
                minute_str = "0" + selectedMinute
            } else {
                minute_str = selectedMinute.toString()
            }

            if (Time.equals("StartTime")) {
                binding.startTimeText?.setText("$hour_str:$minute_str")
            }*/

            var hour: Int = selectedHour
            if(selectedHour in 0..11){
                time = "$selectedHour:$selectedMinute AM"
            } else {
                if(selectedHour == 12){
                    time = "$selectedHour:$selectedMinute PM"
                } else{
                    hour = hour!!- 12;
                    time = "$hour:$selectedMinute PM";                }
            }

            if (Time == "StartTime") {
                binding.startTimeText?.text = time
            } 

        } catch (ex: Exception) {
        }
    }

    fun setServiceProvidedType(data: ServiceProvidedTypesList.Data) {
        if (selectProvidedTypesPopWindow != null) {
            if (selectProvidedTypesPopWindow!!.isShowing) {
                selectProvidedTypesPopWindow!!.dismiss()
            }
        }

        if (data!= null){
         binding.selectServiveProvideTv.text = data.serviceProvideName
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateServiceBookingListServiceCall(serviceId: Int, serviceBookingId: Int) {
        customProgressDialog.progressDialogShow(this@ServiceSelectedMemberInfoActivity, this.getString(R.string.loading))
        var date = binding.startDateTxt!!.text.toString()
        if(!date.contains("DD/MM/YYYY")){
            date = Utils.changeDateFormatToMMDDYYYY(date)
        }
        var time : String = binding.startTimeText!!.text.toString().replace(":", "-")
        val jsonObject = JsonObject()
        jsonObject.addProperty("ServiceBookingId",serviceBookingId)
        jsonObject.addProperty("ServiceId", serviceId)
        jsonObject.addProperty("Date", date)
        jsonObject.addProperty("Time", time)
        jsonObject.addProperty("BookFor", "individual resident")
        jsonObject.addProperty("ResidentId", residentId!!.toInt())
        updatebookServiceCall = apiInterface.updateServiceBooking("bearer "+Auth_token, jsonObject)
        updatebookServiceCall.enqueue(object: Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                    moveToServiesActivity()

                }else{
                    customProgressDialog.progressDialogDismiss()
                    moveToServiesActivity()

                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                moveToServiesActivity()
                Utils.showToast(this@ServiceSelectedMemberInfoActivity, t.message.toString())
            }

        })
    }


}