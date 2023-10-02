package com.example.safehome.dailyhelp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentDailyHelpBookingBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.DailyHelpBookingListModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DailyHelpBookingsFragment : Fragment() {
    private var time: String?= null
    private lateinit var updateBookingStaffServiceCall: Call<AddServiceBookingList>
    private lateinit var deleteStaffBookingCall: Call<AddServiceBookingList>
    private var residentId: String?= null
    private lateinit var binding: FragmentDailyHelpBookingBinding
    private var dailyHelpMembersList: ArrayList<DailyHelpBookingListModel.Data> = ArrayList()
    private lateinit var dailyHelpBookingListAdapter: DailyHelpBookingListAdapter
    private var paymentModeOther: Dialog? = null
    private var payUsingOther: Dialog? = null
    private var bookingOtherDialog: Dialog? = null
    private var paid_on_date_txt: TextView? = null
    var cal = Calendar.getInstance()
    private var deleteMemberDialog: Dialog? = null
    var start_date_txt: TextView? = null
    var end_date_txt: TextView? = null
    var start_time_text: TextView? = null
    var end_time_text: TextView? = null
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private lateinit var radioButton: RadioButton
    private lateinit var customProgressDialog: CustomProgressDialog

    private lateinit var apiInterface: APIInterface
    private lateinit var allDailyHelpModel : Call<DailyHelpBookingListModel>


    var User_Id: String? = ""
    var Auth_Token: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyHelpBookingBinding.inflate(inflater, container, false)


        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")
        residentId = Utils.getStringPref(requireContext(), "residentId", "").toString()


        addDailyHelpMemberListData()
        populateData()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addDailyHelpMemberListData() {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        allDailyHelpModel = apiInterface.getDailyHelpBookingListList(
            "Bearer " + Auth_Token, residentId!!
        )
        allDailyHelpModel.enqueue(object : Callback<DailyHelpBookingListModel> {
            override fun onResponse(
                call: Call<DailyHelpBookingListModel>,
                response: Response<DailyHelpBookingListModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (dailyHelpMembersList.isNotEmpty()) {
                            dailyHelpMembersList.clear()
                        }
                        val  dailyHelpRoles = response.body() as DailyHelpBookingListModel
                        dailyHelpMembersList = dailyHelpRoles.data as ArrayList<DailyHelpBookingListModel.Data>

                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<DailyHelpBookingListModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun populateData() {
        if (dailyHelpMembersList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.dailyHelpBookingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            dailyHelpBookingListAdapter =
                DailyHelpBookingListAdapter(requireContext(), dailyHelpMembersList)
            binding.dailyHelpBookingRecyclerView.adapter = dailyHelpBookingListAdapter
            dailyHelpBookingListAdapter.setCallback(this@DailyHelpBookingsFragment)
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedBookingItem(dailyHelpMemberList: DailyHelpBookingListModel.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.booking_in_daily_help_dialog, null)
        bookingOtherDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        bookingOtherDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookingOtherDialog!!.setContentView(view)
        bookingOtherDialog!!.setCanceledOnTouchOutside(true)
        bookingOtherDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(bookingOtherDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        bookingOtherDialog!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)

        val member_Role = view.findViewById<TextView>(R.id.member_Role)
        val dailyHelpMemberListname_text = view.findViewById<TextView>(R.id.name_text)

        member_Role.setText(dailyHelpMemberList.staffTypeName)
        dailyHelpMemberListname_text.setText(dailyHelpMemberList.staffName)
/*

        val start_date_txt = view.findViewById<TextView>(R.id.start_date_txt)
        val end_date_txt = view.findViewById<TextView>(R.id.end_date_txt)
        val start_time_text = view.findViewById<TextView>(R.id.start_time_text)
        val end_time_text = view.findViewById<TextView>(R.id.end_time_text)
*/

        val delete_btn = view.findViewById<TextView>(R.id.delete_btn)
        val edit_btn = view.findViewById<TextView>(R.id.edit_btn)

        delete_btn.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
            cancelBooking(dailyHelpMemberList.staffBookingId)
        }

        edit_btn.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
            updateBookingDialog(dailyHelpMemberList)
        }

        close.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
        }
        bookingOtherDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateBookingDialog(dailyHelpMemberList: DailyHelpBookingListModel.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.booking_in_daily_help_update_dialog, null)
        bookingOtherDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        bookingOtherDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookingOtherDialog!!.setContentView(view)
        bookingOtherDialog!!.setCanceledOnTouchOutside(true)
        bookingOtherDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(bookingOtherDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        bookingOtherDialog!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)

        val member_Role = view.findViewById<TextView>(R.id.member_Role)
        val dailyHelpMemberListname_text = view.findViewById<TextView>(R.id.name_text)

        member_Role.setText(dailyHelpMemberList.staffTypeName)
        dailyHelpMemberListname_text.setText(dailyHelpMemberList.staffName)

        start_date_txt = view.findViewById<TextView>(R.id.start_date_txt)
        end_date_txt = view.findViewById<TextView>(R.id.end_date_txt)
        start_time_text = view.findViewById<TextView>(R.id.start_time_text)
        end_time_text = view.findViewById<TextView>(R.id.end_time_text)

        timeSet()

        val update_btn = view.findViewById<TextView>(R.id.update_btn)

        update_btn.setOnClickListener {
            updateStaffBookingServiceCall(dailyHelpMemberList.staffId, dailyHelpMemberList.staffBookingId)
            /*if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }*/
        }

        close.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
        }
        bookingOtherDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateStaffBookingServiceCall(staffId: Int, staffBookingId: Int) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        var startDate = start_date_txt!!.text.toString()
        startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        var endDate = end_date_txt!!.text.toString()
        endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        val jsonObject = JsonObject()
        jsonObject.addProperty("staffBookingId", staffBookingId)
        jsonObject.addProperty("StaffId", staffId)
        jsonObject.addProperty("FromDate", startDate)
        jsonObject.addProperty("ToDate", endDate)
        jsonObject.addProperty("FromTime", start_time_text!!.text.toString().replace(":", "-"))
        jsonObject.addProperty("ToTime", end_time_text!!.text.toString().replace(":", "-"))
        jsonObject.addProperty("BookFor", "individual resident")
        jsonObject.addProperty("ResidentId", residentId!!.toInt())
        Log.e("jsonObject1", ""+jsonObject)
        updateBookingStaffServiceCall = apiInterface.updateStaffBooking("bearer "+Auth_Token, jsonObject)
        updateBookingStaffServiceCall.enqueue(object: Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (bookingOtherDialog!!.isShowing) {
                        bookingOtherDialog!!.dismiss()
                    }

                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{
                    if (bookingOtherDialog!!.isShowing) {
                        bookingOtherDialog!!.dismiss()
                    }

                    customProgressDialog.progressDialogDismiss()
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                if (bookingOtherDialog!!.isShowing) {
                    bookingOtherDialog!!.dismiss()
                }
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

    private fun timeSet() {
        start_date_txt?.setOnClickListener {
            val startDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH)
            )

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        end_date_txt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH)
            )
            endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
        }

        start_time_text?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    //setTime("StartTime", selectedHour, selectedMinute)
                    start_time_text!!.text = String.format("%02d:%02d %s", selectedHour, selectedMinute,
                        if (selectedHour < 12) "AM" else "PM"
                    )

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        end_time_text?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
             //       setTime("EndTime", selectedHour, selectedMinute)
                    end_time_text!!.text = String.format("%02d:%02d %s", selectedHour, selectedMinute,
                        if (selectedHour < 12) "AM" else "PM"
                    )

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }
    }

    fun selectedBookingPayUsing(dailyHelpMemberList: DailyHelpBookingListModel.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.pay_using_in_daily_help_dialog, null)
        payUsingOther = Dialog(requireContext(), R.style.CustomAlertDialog)
        payUsingOther!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payUsingOther!!.setContentView(view)
        payUsingOther!!.setCanceledOnTouchOutside(true)
        payUsingOther!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(payUsingOther!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        payUsingOther!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val continue_btn = view.findViewById<TextView>(R.id.continue_btn)

        val radioGroup = view.findViewById<RadioGroup>(R.id.rdGroup) as RadioGroup
        val third_party_name_radio_option = view.findViewById<RadioButton>(R.id.third_party_name) as RadioButton
        val other_radio_Option = view.findViewById<RadioButton>(R.id.other_rd_option) as RadioButton

        // get selected radio button from radioGroup
        val selectedId: Int = radioGroup.getCheckedRadioButtonId()
        // find the radiobutton by returned id
//        radioButton = view.findViewById<RadioButton>(selectedId) as RadioButton

        continue_btn.setOnClickListener {
            if (radioGroup?.getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireContext(), "Please select option", Toast.LENGTH_LONG).show()
            } else {
                if (payUsingOther!!.isShowing) {
                    payUsingOther!!.dismiss()
                }
//                Toast.makeText(requireContext(),radioButton?.text.toString(),Toast.LENGTH_LONG).show()
//                if (radioButton?.text.toString().equals("Other")) {
                    selectedBookingPayNow(dailyHelpMemberList)
//                } else {
//                    if (payUsingOther!!.isShowing) {
//                        payUsingOther!!.dismiss()
//                    }
//                }
            }
        }

        close.setOnClickListener {
            if (payUsingOther!!.isShowing) {
                payUsingOther!!.dismiss()
            }
        }
        payUsingOther!!.show()
    }

    // create an OnDateSetListener
    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            paid_on_date_txt!!.text = sdf.format(cal.getTime())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    fun cancelBooking(staffBookingId: Int) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        deleteMemberDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        deleteMemberDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteMemberDialog!!.setContentView(view)
        deleteMemberDialog!!.setCanceledOnTouchOutside(true)
        deleteMemberDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(deleteMemberDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        deleteMemberDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.setText("Are you sure you want to cancel this Booking?")

        close.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
             deleteStaffServiceCall(staffBookingId)
        }

        no.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }
        deleteMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteStaffServiceCall(staffBookingId: Int) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        deleteStaffBookingCall = apiInterface.deleteBookStaff("bearer "+Auth_Token, staffBookingId)
        deleteStaffBookingCall.enqueue(object: Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    if (deleteMemberDialog!!.isShowing) {
                        deleteMemberDialog!!.dismiss()
                    }
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{
                    customProgressDialog.progressDialogDismiss()
                    if (deleteMemberDialog!!.isShowing) {
                        deleteMemberDialog!!.dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                if (deleteMemberDialog!!.isShowing) {
                    deleteMemberDialog!!.dismiss()
                }
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
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
            start_date_txt!!.text = sdf.format(cal_startDate.getTime())
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
            end_date_txt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    private fun setTime(Time: String, selectedHour: Int, selectedMinute: Int) {
        try {
//            var hour_str: String? = null
//            if (selectedHour < 10) {
//                hour_str = "0" + selectedHour
//            } else {
//                hour_str = selectedHour.toString()
//            }
//
//            var minute_str: String? = null
//            if (selectedMinute < 10) {
//                minute_str = "0" + selectedMinute
//            } else {
//                minute_str = selectedMinute.toString()
//            }
//            if (Time.equals("StartTime")) {
//                start_time_text?.setText("$hour_str:$minute_str")
//            } else {
//                end_time_text?.setText("$hour_str:$minute_str")
//            }

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
               start_time_text!!.text = time
            } else {
                end_time_text!!.text = time
            }

        } catch (ex: Exception) {
        }
    }

    fun selectedBookingPayNow(dailyHelpMemberList: DailyHelpBookingListModel.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.payment_mode_booking_in_daily_help_dialog, null)
        paymentModeOther = Dialog(requireContext(), R.style.CustomAlertDialog)
        paymentModeOther!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        paymentModeOther!!.setContentView(view)
        paymentModeOther!!.setCanceledOnTouchOutside(true)
        paymentModeOther!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(paymentModeOther!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        paymentModeOther!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        paid_on_date_txt = view.findViewById<TextView>(R.id.paid_on_date_txt)
        val cancel_btn = view.findViewById<TextView>(R.id.cancel_btn)

        val comments_et: EditText = view.findViewById(R.id.comments_et)
        comments_et.setBackgroundResource(android.R.color.transparent)

        paid_on_date_txt?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        close.setOnClickListener {
            if (paymentModeOther!!.isShowing) {
                paymentModeOther!!.dismiss()
            }
        }

        cancel_btn.setOnClickListener {
            if (paymentModeOther!!.isShowing) {
                paymentModeOther!!.dismiss()
            }
        }
        paymentModeOther!!.show()
    }

}