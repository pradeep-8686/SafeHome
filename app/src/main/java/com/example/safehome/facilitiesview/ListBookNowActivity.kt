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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListBookNowActivity : BaseActivity() {
    private var residentId: String? = null
    private var Auth_token: String? = null
    private var facilityId: Int? = null
    private lateinit var addBookingServiceCall: Call<AddServiceBookingList>
    private lateinit var updateBookingServiceCall: Call<AddServiceBookingList>
    private lateinit var customProgressDialog: CustomProgressDialog
    private var timeCount = 1
    private var dateCount = 1
    private var bookingType: String? = null
    private var bookingTotalCharge: String? = null
    private lateinit var bookNowDialogBinding: BookNowDialogBinding
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private var confirmationDialog: Dialog? = null
    private lateinit var apiInterface: APIInterface
    private var bookByHour: Double = 0.0
    private var bookByDay: Double = 0.0
    private var cgstBookByDay: Double = 0.0
    private var cgstBookByHour: Double = 0.0
    private var sgstBookByDay: Double = 0.0
    private var sgstBookByHour: Double = 0.0
    private var totalDayTax: Double = 0.0
    private var totalHourTax: Double = 0.0
    private var time: String? = null
    private var bookFacilityId: String? = null
    private var from: String? = null
    private var chargeable: String? = "Yes"

    private val decimalFormat = DecimalFormat("#.00")

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookNowDialogBinding = BookNowDialogBinding.inflate(layoutInflater)
        setContentView(bookNowDialogBinding.root)

        if (intent != null) {
            bookingType = intent.getStringExtra("bookType")
            facilityId = intent.getIntExtra("facilityId", 0)
            bookByDay = intent.getDoubleExtra("bookByDay", 0.00)
            bookByHour = intent.getDoubleExtra("bookByHour", 0.00)
            Log.e("facilityId", "" + facilityId)
            from = intent.getStringExtra("from")
            if (from!! == "bookings") {
                bookFacilityId = intent.getStringExtra("bookFacilityId")
            }
            chargeable = intent.getStringExtra("chargeable")

            cgstBookByDay = intent.getDoubleExtra("cgstBookByDay", 0.00)
            cgstBookByHour = intent.getDoubleExtra("cgstBookByHour", 0.00)

            sgstBookByDay = intent.getDoubleExtra("sgstBookByDay", 0.00)
            sgstBookByHour = intent.getDoubleExtra("sgstBookByHour", 0.00)

            if (chargeable.equals("Yes", true)) {
//                calculateTax()
                bookNowDialogBinding.totalChargeEt.text = calculateByHour(bookByHour)
            }
        }

        getIntentData()
        bookNowDialogBinding.purposeEt.setBackgroundResource(android.R.color.transparent)
        bookNowDialogBinding.commentsEt.setBackgroundResource(android.R.color.transparent)

        inIt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIntentData() {
        if (intent.extras?.getSerializable("faciBookings") != null) {
            val faciBookings =
                intent.extras?.getSerializable("faciBookings") as FaciBookings.Data.Facilility

            if (faciBookings.purpose != null) {

                bookNowDialogBinding.purposeEt.setText(faciBookings.purpose)
            }
            if (faciBookings.comments != null) {

                bookNowDialogBinding.commentsEt.setText(faciBookings.comments)
            }
            if (faciBookings.noOfDays != null) {
                bookNowDialogBinding.numberOfDaysTxt.text = faciBookings.noOfDays.toString()
                dateCount = faciBookings.noOfDays
                if(dateCount > 1){
                    bookNowDialogBinding.timeLayout.visibility = View.GONE
                    bookNowDialogBinding.noOfHoursLayout.visibility = View.GONE
                }else{
                    bookNowDialogBinding.timeLayout.visibility = View.VISIBLE
                    bookNowDialogBinding.noOfHoursLayout.visibility = View.VISIBLE
                }


            }
            if (faciBookings.noOfHours != null) {
                bookNowDialogBinding.numberOfHoursTxt.text = faciBookings.noOfHours.toString()
                timeCount = faciBookings.noOfHours

            }
            if (faciBookings.startDate != null) {
                bookNowDialogBinding.startDateTxt.text =
                    Utils.formatDateMonthYear(faciBookings.startDate)
            }
            if (faciBookings.endDate != null) {
                bookNowDialogBinding.endDateTxt.text =
                    Utils.formatDateMonthYear(faciBookings.endDate)
            }
            if (faciBookings.startTime != null) {
                bookNowDialogBinding.startTimeText.text = faciBookings.startTime
            }
            if (faciBookings.endTime != null) {
                bookNowDialogBinding.endTime.text = faciBookings.endTime
            }
            if (faciBookings.totalAmount != null) {
                bookNowDialogBinding.totalChargeEt.text = decimalFormat.format(faciBookings.totalAmount).toString()

            }


        }
    }

    private fun calculateTax() {

        totalDayTax = cgstBookByDay + sgstBookByDay
        totalHourTax = cgstBookByHour + sgstBookByHour
        val cgst = (cgstBookByHour * bookByHour) / 100.0
        val sgst = (sgstBookByHour * bookByHour) / 100.0

        Log.d(
            "Calculations",
            "Total : $bookByHour \ncgst : $cgstBookByHour \nsgst : $sgstBookByHour"
        )
        Log.d("Calculations", "$cgstBookByHour % $bookByHour")

        bookNowDialogBinding.totalChargeEt.text = (bookByHour + cgst + sgst).toString()

    }

    private fun calculateByHour(hour: Double): String {
        val cgst = (cgstBookByHour * hour) / 100.0
        val sgst = (sgstBookByHour * hour) / 100.0

        Log.d("Calculations", "Total : $hour \ncgst : $cgst \nsgst : $sgst")
        val formattedValue = decimalFormat.format(hour + cgst + sgst)

        return formattedValue.toString()
    }

    private fun calculateByDay(day: Double): String {
        val cgst = (cgstBookByDay * day) / 100.0
        val sgst = (cgstBookByHour * day) / 100.0
        val formattedValue = decimalFormat.format(day + cgst + sgst)


        return formattedValue.toString()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@ListBookNowActivity)
        Auth_token = Utils.getStringPref(this@ListBookNowActivity, "Token", "")!!
        residentId = Utils.getStringPref(this@ListBookNowActivity, "residentId", "")!!
        bookNowDialogBinding.sendRequestTv.setOnClickListener {
            //  confirmationPopup()

            if (validations()) {
                addBookFacilityNetworkCall()
            }

        }
        bookNowDialogBinding.bookNowTitleTxt.text = bookingType
        bookNowDialogBinding.startDateTxt?.setOnClickListener {
            val startDateDialog = DatePickerDialog(
                this@ListBookNowActivity,
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH)
            )

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        bookNowDialogBinding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(
                this@ListBookNowActivity,
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH)
            )
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(
                    bookNowDialogBinding.startDateTxt!!.text.toString(),
                    "dd/MM/yyyy"
                )
                endDateDialog.datePicker.minDate = dateInMillis
            } catch (e: Exception) {
                endDateDialog.datePicker.minDate =
                    System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
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
            if (dateCount > 1) {
                bookNowDialogBinding.timeLayout.visibility = View.GONE
                bookNowDialogBinding.noOfHoursLayout.visibility = View.GONE
                val totalCharge = bookByDay * dateCount
                bookNowDialogBinding.totalChargeEt.text = calculateByDay(totalCharge)
            } else {
                bookNowDialogBinding.timeLayout.visibility = View.VISIBLE
                bookNowDialogBinding.noOfHoursLayout.visibility = View.VISIBLE

                /* if (dateCount == 0) {
                     bookNowDialogBinding.totalChargeEt.text = "0"
                 } else {
                     bookNowDialogBinding.totalChargeEt.text =   calculateByHour(bookByDay)
                 }*/
            }
        }

        bookNowDialogBinding.minusDateImg.setOnClickListener {

            dateCount--
            bookNowDialogBinding.numberOfDaysTxt.text = dateCount.toString()
            if (dateCount > 1) {

                bookNowDialogBinding.timeLayout.visibility = View.GONE
                bookNowDialogBinding.noOfHoursLayout.visibility = View.GONE
                val totalCharge = bookByDay * dateCount
                bookNowDialogBinding.totalChargeEt.text = calculateByDay(totalCharge)
            } else {
                dateCount = 1
                bookNowDialogBinding.numberOfDaysTxt.text = dateCount.toString()
                bookNowDialogBinding.timeLayout.visibility = View.VISIBLE
                bookNowDialogBinding.noOfHoursLayout.visibility = View.VISIBLE
                val totalCharge = bookByHour * timeCount
                bookNowDialogBinding.totalChargeEt.text = calculateByHour(totalCharge)
            }

        }

        bookNowDialogBinding.plusTimeImg.setOnClickListener {
            if (timeCount in 0..23) {
                timeCount++
                val totalCharge = bookByHour * timeCount
                bookNowDialogBinding.totalChargeEt.text = calculateByHour(totalCharge)
                bookNowDialogBinding.numberOfHoursTxt.text = timeCount.toString()
            }
        }

        bookNowDialogBinding.minusTimeImg.setOnClickListener {
            if (timeCount in 2..24) {
                timeCount--
                val totalCharge = bookByHour * timeCount
                bookNowDialogBinding.totalChargeEt.text = calculateByHour(totalCharge)
                bookNowDialogBinding.numberOfHoursTxt.text = timeCount.toString()
            } else {
//                timeCount = 0
//                bookNowDialogBinding.totalChargeEt.text = "0"
            }
        }

        bookNowDialogBinding.booknowBackBtnClick.setOnClickListener {
            val intent = Intent(this, FacilitiesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validations(): Boolean {

        if (bookNowDialogBinding.purposeEt.text.toString().isEmpty()) {

            Utils.showToast(this, "Please enter purpose")
            return false
        }
        if (chargeable == "Yes") {
            if (bookNowDialogBinding.numberOfDaysTxt.text.toString() == "0" && bookNowDialogBinding.numberOfHoursTxt.text.toString() == "0") {
                Utils.showToast(this, "Please select number of days or number of hours")
                return false
            }
            /*  if (bookNowDialogBinding.numberOfHoursTxt.text.toString() == "0") {
                  Utils.showToast(this, "Please select number of hours")
                  return false
              }*/
        }

        if (bookNowDialogBinding.startDateTxt.text.toString() == "DD/MM/YYYY") {
            Utils.showToast(this, "Please select start date")
            return false
        }
        if (bookNowDialogBinding.endDateTxt.text.toString() == "DD/MM/YYYY") {
            Utils.showToast(this, "Please select end date")
            return false
        }
        if (bookNowDialogBinding.numberOfDaysTxt.text.toString() == "0" || bookNowDialogBinding.numberOfDaysTxt.text.toString() == "1") {
            if (bookNowDialogBinding.startTimeText.text.toString() == "HH:MM") {
                Utils.showToast(this, "Please select start time")
                return false
            }
            if (bookNowDialogBinding.endTime.text.toString() == "HH:MM") {
                Utils.showToast(this, "Please select end time")
            }
        }


        return true
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
        customProgressDialog.progressDialogShow(
            this@ListBookNowActivity,
            this.getString(R.string.loading)
        )
        var startDate = bookNowDialogBinding.startDateTxt.text.toString()
        if (!startDate.contains("DD/MM/YYYY")) {
            startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        }
        var endDate = bookNowDialogBinding.endDateTxt.text.toString()
        if (!endDate.contains("DD/MM/YYYY")) {
            endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        }
        var startTime = bookNowDialogBinding.startTimeText.text.toString().replace(":", "-")
        var endTime = bookNowDialogBinding.endTime.text.toString().replace(":", "-")
        addBookingServiceCall = apiInterface.BookFacility(
            "bearer " + Auth_token, residentId!!.toInt(), facilityId!!, "function",
            bookNowDialogBinding.numberOfDaysTxt.text.toString().toInt(),
            startDate, endDate,
            bookNowDialogBinding.numberOfHoursTxt.text.toString().toInt(),
            startTime, endTime, "Comments"
        )
        addBookingServiceCall.enqueue(object : Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    confirmationDialog!!.show()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@ListBookNowActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@ListBookNowActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    // Utils.showToast(this@ListBookNowActivity, "your booking is not successful")
                    moveToFacilityActivity()
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                //         moveToFacilityActivity()
                Utils.showToast(this@ListBookNowActivity, t.message.toString())
            }
        })
    }

    private fun moveToFacilityActivity() {
        val intent = Intent(this@ListBookNowActivity, FacilitiesActivity::class.java)
        intent.putExtra("from", "bookingsPage")
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
            val disablePastDates = Calendar.getInstance().timeInMillis - 1000
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
            if (selectedHour in 0..11) {
                time = "$selectedHour:$selectedMinute AM"
            } else {
                if (selectedHour == 12) {
                    time = "$selectedHour:$selectedMinute PM"
                } else {
                    hour = hour!! - 12;
                    time = "$hour:$selectedMinute PM"; }
            }

            if (Time == "StartTime") {
                bookNowDialogBinding.startTimeText?.text = time
            } else {
                bookNowDialogBinding.endTime?.text = time

            }

        } catch (ex: Exception) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateBookFacility() {
        customProgressDialog.progressDialogShow(
            this@ListBookNowActivity,
            this.getString(R.string.loading)
        )
        var startDate = bookNowDialogBinding.startDateTxt.text.toString()
        if (!startDate.contains("DD/MM/YYYY")) {
            startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        }
        var endDate = bookNowDialogBinding.endDateTxt.text.toString()
        if (!endDate.contains("DD/MM/YYYY")) {
            endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        }
        var startTime = bookNowDialogBinding.startTimeText.text.toString().replace(":", "-")
        var endTime = bookNowDialogBinding.endTime.text.toString().replace(":", "-")
        updateBookingServiceCall = apiInterface.updateBookFacility(
            "bearer " + Auth_token,
            bookFacilityId!!.toInt(),
            residentId!!.toInt(),
            facilityId!!,
            "function",
            bookNowDialogBinding.numberOfDaysTxt.text.toString().toInt(),
            startDate,
            endDate,
            bookNowDialogBinding.numberOfHoursTxt.text.toString().toInt(),
            startTime,
            endTime,
            "Comments"
        )
        updateBookingServiceCall.enqueue(object : Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@ListBookNowActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@ListBookNowActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
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
}