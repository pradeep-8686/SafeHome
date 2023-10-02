package com.example.safehome.facilitiesview

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.BookNowDialogBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.FaciBookings
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListBookNowActivity: BaseActivity() {
    private var residentId: String?= null
    private var Auth_token: String? = null
    private var facilityId: Int?= null
    private lateinit var addBookingServiceCall: Call<AddServiceBookingList>
    private lateinit var customProgressDialog: CustomProgressDialog
    private var timeCount = 1
    private var dateCount = 1
    private var bookingType: String?= null
    private var bookingTotalCharge: String? =null
    private lateinit var bookNowDialogBinding: BookNowDialogBinding
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private var confirmationDialog: Dialog? = null
    private lateinit var apiInterface: APIInterface
    private var bookByHour : Double = 0.0
    private var bookByDay : Double = 0.0
    private var time: String?= null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookNowDialogBinding = BookNowDialogBinding.inflate(layoutInflater)
        setContentView(bookNowDialogBinding.root)
        if (intent!= null){
            bookingType = intent.getStringExtra("bookType")
            facilityId = intent.getIntExtra("facilityId", 0)
            bookByDay = intent.getDoubleExtra("bookByDay", 1.00)
            bookByHour = intent.getDoubleExtra("bookByHour", 1.00)
            Log.e("facilityId",""+facilityId)
            bookNowDialogBinding.totalChargeEt.text = bookByHour.toString()

        }

        bookNowDialogBinding.purposeEt.setBackgroundResource(android.R.color.transparent)
        bookNowDialogBinding.commentsEt.setBackgroundResource(android.R.color.transparent)

        inIt()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@ListBookNowActivity)
        Auth_token = Utils.getStringPref(this@ListBookNowActivity, "Token", "")!!
        residentId = Utils.getStringPref(this@ListBookNowActivity, "residentId", "")!!
        bookNowDialogBinding.sendRequestTv.setOnClickListener {
          //  confirmationPopup()
            addBookFacilityNetworkCall()
        }
        bookNowDialogBinding.bookNowTitleTxt.text = bookingType
        bookNowDialogBinding.startDateTxt?.setOnClickListener {
            val startDateDialog =  DatePickerDialog(this@ListBookNowActivity,
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH))

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        bookNowDialogBinding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(this@ListBookNowActivity,
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH))
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(bookNowDialogBinding.startDateTxt!!.text.toString(), "dd/MM/yyyy")
                endDateDialog.datePicker.minDate = dateInMillis
            }catch (e: Exception){
                endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
            }
            // endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
        }

        bookNowDialogBinding.startTimeText?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@ListBookNowActivity,
                { timePicker, selectedHour, selectedMinute ->
                    setTime("StartTime", selectedHour, selectedMinute)
                },
                hour,
                minute,
                false
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        bookNowDialogBinding.endTime?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@ListBookNowActivity,
                { timePicker, selectedHour, selectedMinute ->
                    setTime("EndTime", selectedHour, selectedMinute)
                },
                hour,
                minute,
                false
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

         bookNowDialogBinding.plusDateImg.setOnClickListener {
             dateCount++
             bookNowDialogBinding.numberOfDaysTxt.text = dateCount.toString()
             if (dateCount > 1){
                 bookNowDialogBinding.llHours.visibility = View.GONE
                 val totalCharge = bookByDay * dateCount
                 bookNowDialogBinding.totalChargeEt.text = totalCharge.toString()
             }else{
                 bookNowDialogBinding.llHours.visibility = View.VISIBLE

                 if (dateCount == 0){
                     bookNowDialogBinding.totalChargeEt.text = "0"
                 }else{
                     bookNowDialogBinding.totalChargeEt.text = bookByDay.toString()
                 }
             }
         }

         bookNowDialogBinding.minusDateImg.setOnClickListener {
            dateCount--
             if(dateCount >= 0){
                 bookNowDialogBinding.numberOfDaysTxt.text = dateCount.toString()
                 if (dateCount > 1){
                     bookNowDialogBinding.llHours.visibility = View.GONE
                     val totalCharge = bookByDay * dateCount
                     bookNowDialogBinding.totalChargeEt.text = totalCharge.toString()
                 }else{
                     bookNowDialogBinding.llHours.visibility = View.VISIBLE
                     if (dateCount == 0){
                         bookNowDialogBinding.totalChargeEt.text = "0"
                     }else{
                         bookNowDialogBinding.totalChargeEt.text = bookByDay.toString()
                     }
                 }
             }else{
                 dateCount = 0
                 bookNowDialogBinding.llHours.visibility = View.VISIBLE
                 bookNowDialogBinding.totalChargeEt.text = "0"
             }
        }

        bookNowDialogBinding.plusTimeImg.setOnClickListener {
            timeCount++
            val totalCharge = bookByHour.toInt() * timeCount
            bookNowDialogBinding.totalChargeEt.text = totalCharge.toString()
           bookNowDialogBinding.numberOfHoursTxt.text = timeCount.toString()
        }

        bookNowDialogBinding.minusTimeImg.setOnClickListener {
            timeCount--
            if (timeCount >= 0) {
                val totalCharge = bookByHour.toInt() * timeCount
                bookNowDialogBinding.totalChargeEt.text = totalCharge.toString()
                bookNowDialogBinding.numberOfHoursTxt.text = timeCount.toString()
            }else{
                timeCount = 0
                bookNowDialogBinding.totalChargeEt.text = "0"
            }
        }

        bookNowDialogBinding.booknowBackBtnClick.setOnClickListener {
            val intent = Intent(this, FacilitiesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun confirmationPopup() {
        val layoutInflater: LayoutInflater =
              getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.confirmation_popup, null)
        confirmationDialog = Dialog(this@ListBookNowActivity, R.style.CustomAlertDialog)
        confirmationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmationDialog!!.setContentView(view)
        confirmationDialog!!.setCanceledOnTouchOutside(true)
        confirmationDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(confirmationDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        confirmationDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val text_msg: TextView = view.findViewById<TextView>(R.id.text_msg)
        val ok_btn: TextView = view.findViewById(R.id.ok_btn)

        text_msg.setText("Your Booking request has been sent successfully.")

        ok_btn.setOnClickListener {
            if (confirmationDialog!!.isShowing) {
                confirmationDialog!!.dismiss()
            }
            moveToFacilityActivity()
         //   addBookFacilityNetworkCall()

            /*  if (bookNowDialog != null) {
                  if (bookNowDialog!!.isShowing) {
                      bookNowDialog!!.dismiss()
                  }
              }*/
        }

        close.setOnClickListener {
            if (confirmationDialog!!.isShowing) {
                confirmationDialog!!.dismiss()
            }
        }

        confirmationDialog!!.show()
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addBookFacilityNetworkCall() {
        customProgressDialog.progressDialogShow(this@ListBookNowActivity, this.getString(R.string.loading))
        var startDate = bookNowDialogBinding.startDateTxt.text.toString()
       /* if(startDate!= null && startDate.isNotEmpty()){
            startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        }
       */ var endDate = bookNowDialogBinding.endDateTxt.text.toString()
        /*if(endDate!= null && endDate.isNotEmpty()){
            endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        }
*/
        var startTime = bookNowDialogBinding.startTimeText.text.toString().replace(":", "-")
        var endTime = bookNowDialogBinding.endTime.text.toString().replace(":", "-")
        addBookingServiceCall = apiInterface.BookFacility("bearer "+Auth_token, residentId!!.toInt(), facilityId!!, "function",
            bookNowDialogBinding.numberOfDaysTxt.text.toString().toInt(),
            startDate, endDate,
            bookNowDialogBinding.numberOfHoursTxt.text.toString().toInt(),
            startTime, endTime, "Comments")
           addBookingServiceCall.enqueue(object: Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    moveToFacilityActivity()
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ListBookNowActivity, response.body()!!.message.toString())
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ListBookNowActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{
                    customProgressDialog.progressDialogDismiss()
                    moveToFacilityActivity()
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                moveToFacilityActivity()
                Utils.showToast(this@ListBookNowActivity, t.message.toString())
            }

        })
    }

    private fun moveToFacilityActivity() {
        val intent = Intent(this@ListBookNowActivity, FacilitiesActivity::class.java)
        startActivity(intent)
        finish()
    }

    /*private fun setTime(Time: String, selectedHour: Int, selectedMinute: Int) {
        try {
            var hour_str: String? = null
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
                bookNowDialogBinding.startTimeText?.setText("$hour_str:$minute_str")
            } else {
                bookNowDialogBinding.endTime?.setText("$hour_str:$minute_str")
            }
        } catch (ex: Exception) {
        }
    }
*/
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
            bookNowDialogBinding.startDateTxt!!.text = sdf.format(cal_startDate.getTime())
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
            bookNowDialogBinding.endDateTxt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, FacilitiesActivity::class.java)
        startActivity(intent)
        finish()
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
                bookNowDialogBinding.startTimeText?.text = time
            }else{
                bookNowDialogBinding.endTime?.text = time

            }

        } catch (ex: Exception) {
        }
    }
}