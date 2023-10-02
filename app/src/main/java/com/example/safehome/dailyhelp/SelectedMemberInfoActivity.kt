package com.example.safehome.dailyhelp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivitySelectedMemberInfoBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.DailyHelpStaffModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SelectedMemberInfoActivity : AppCompatActivity() {
    private var time: String?= null
    private lateinit var binding: ActivitySelectedMemberInfoBinding
    private var bookingConfirmDialog: Dialog? = null
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private var residentId: String?= null
    private var Auth_token: String?= null
    private lateinit var apiInterface: APIInterface
    private lateinit var bookingStaffServiceCall: Call<AddServiceBookingList>
    private lateinit var customProgressDialog: CustomProgressDialog
    var dailyHelpMemberList: DailyHelpStaffModel.Data? = null
    private var MemberRoleName : String?= null
    private var MemeberRole: String? = null
    private var availableOn : String?= null
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedMemberInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            dailyHelpMemberList = intent.getSerializableExtra("dailyHelpStaffModel") as DailyHelpStaffModel.Data?
            MemberRoleName = intent.getStringExtra("MemberRoleName")
            MemeberRole =  intent.getStringExtra("MemeberRole")
            availableOn = intent.getStringExtra("availableOn")
        } catch (ex: Exception) {
        }

        binding.memberName.setText(dailyHelpMemberList?.staffName)
        binding.memberMobileNumber.setText(dailyHelpMemberList?.mobileNo)
        if (availableOn != null && availableOn!!.isEmpty()){
            binding.availableOnTxt.text = "Weekdays"
        }else {
            binding.availableOnTxt.text = availableOn
        }
        var sortList = ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>()
        for (model in dailyHelpMemberList!!.staffworkingDetails){
            if (model.residentdetais != null){
                sortList.add(model)
            }
        }

        if (dailyHelpMemberList!!.staffworkingDetails != null && dailyHelpMemberList!!.staffworkingDetails.isNotEmpty()) {
            binding.worksInText.text = "Works In: "+sortList.joinToString(", ") { it1 ->"${it1.residentdetais!!.block} ${it1.residentdetais!!.flatNo}" }
        }

        inIt()
        binding.tittleTxt.text = MemberRoleName

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        binding.RatingBar.rating = 3.5F
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@SelectedMemberInfoActivity)
        Auth_token = Utils.getStringPref(this@SelectedMemberInfoActivity, "Token", "")!!
        residentId = Utils.getStringPref(this@SelectedMemberInfoActivity, "residentId", "")!!

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, DailyHelpMemberListActivity::class.java)
            intent.putExtra("MemberRoleName", MemberRoleName)
            intent.putExtra("MemberRole",MemeberRole)
            startActivity(intent)
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            val intent = Intent(this, DailyHelpActivity::class.java)
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

        binding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(this,
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH))
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(binding.startDateTxt!!.text.toString(), "dd/MM/yyyy")
                endDateDialog.datePicker.minDate = dateInMillis
            }catch (e: Exception){
                endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
            }
            // endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
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
                    )                },
                hour,
                minute,
                false
            ) //Yes 12 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        binding.endTime?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this,
                { timePicker, selectedHour, selectedMinute ->
                 //   setTime("EndTime", selectedHour, selectedMinute)
                    binding.endTime.text = String.format("%02d:%02d %s", selectedHour, selectedMinute,
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
             bookingAddStaffNetworkingCall()
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
    private fun bookingAddStaffNetworkingCall() {
        customProgressDialog.progressDialogShow(this@SelectedMemberInfoActivity, this.getString(R.string.loading))
        var startDate = binding.startDateTxt.text.toString()
        startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        var endDate = binding.endDateTxt.text.toString()
        endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        var startTime : String = binding.startTimeText.text.toString().replace(":", "-")
        var endTime : String = binding.endTime.text.toString().replace(":", "-")
        val jsonObject = JsonObject()
        jsonObject.addProperty("StaffId", dailyHelpMemberList!!.staffId)
        jsonObject.addProperty("FromDate", startDate)
        jsonObject.addProperty("ToDate", endDate)
        jsonObject.addProperty("FromTime", startTime)
        jsonObject.addProperty("ToTime", endTime)
        jsonObject.addProperty("BookFor", "individual resident")
        jsonObject.addProperty("ResidentId", residentId!!.toInt())
        Log.e("jsonObject", ""+jsonObject)
        bookingStaffServiceCall = apiInterface.addStaffBooking("bearer "+Auth_token, jsonObject)
        bookingStaffServiceCall.enqueue(object: Callback<AddServiceBookingList> {
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
                                    Utils.showToast(this@SelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@SelectedMemberInfoActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                        moveToDailyHelpActivity()
                    }
                }else{
                    Utils.showToast(this@SelectedMemberInfoActivity, response.body()!!.message.toString())
                    customProgressDialog.progressDialogDismiss()
                    moveToDailyHelpActivity()
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                moveToDailyHelpActivity()
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@SelectedMemberInfoActivity, t.message.toString())
            }

        })
    }

    fun moveToDailyHelpActivity(){
        val intent = Intent(this@SelectedMemberInfoActivity, DailyHelpActivity::class.java)
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

    // create an OnDateSetListener
    val dateSetListener__book_now_end_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis- 1000
            view.setMinDate(disablePastDates)
            cal_endDate.set(Calendar.YEAR, year)
            cal_endDate.set(Calendar.MONTH, monthOfYear)
            cal_endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.endDateTxt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    private fun setTime(Time: String, selectedHour: Int, selectedMinute: Int) {
        try {
           /* var hour_str: String? = null
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
            } else {
                binding.endTime?.setText("$hour_str:$minute_str")
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
            } else {
                binding.endTime?.text = time
            }

        } catch (ex: Exception) {
        }
    }


}