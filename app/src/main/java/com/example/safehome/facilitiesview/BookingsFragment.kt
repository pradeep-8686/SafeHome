package com.example.safehome.facilitiesview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.FaciBookingsAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentBookingsBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.DeleteFacilityModel
import com.example.safehome.model.FaciBookings
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BookingsFragment : Fragment() {
    private lateinit var cash: RadioButton
    private lateinit var chequeRBtn: RadioButton
    private lateinit var UpiRBtn: RadioButton
    private lateinit var eftRBtn: RadioButton
    private lateinit var thirdPartyNameRBtn: RadioButton
    private lateinit var rdGroup: RadioGroup
    private lateinit var rdGroupTransactionStatus: RadioGroup
    private lateinit var transactionNoEditTxt: EditText
    private lateinit var transactionAmountEditTxt: EditText

    private var fileName: String?= null
    private val REQUEST_CODE: Int = 1000
    private lateinit var uploadDocumentName: TextView
    private lateinit var updatePaymentForBookFacilityCall: Call<AddServiceBookingList>
    private var paid_on_date_txt: TextView?= null
    private var paymentModeOther: Dialog? =null
    private var payUsingOther: Dialog?= null
    private lateinit var binding: FragmentBookingsBinding
    private var bookingList: ArrayList<FaciBookings.Data.Facilility> = ArrayList()
    private lateinit var faciBookingsAdapter: FaciBookingsAdapter
    private var deleteMemberDialog: Dialog? = null
    private var escalateApprovalDialog: Dialog? = null
    var cal = Calendar.getInstance()

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var residentId: String? = ""
    var Auth_Token: String? = ""

    private lateinit var facilitiesModel : Call<FaciBookings>
    private lateinit var deleteFacilitiesModel : Call<DeleteFacilityModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookingsBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        residentId = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")


        getAllBookedFacilities()
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllBookedFacilities() {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        facilitiesModel = apiInterface.getAllBookedFacilities(
            "Bearer " + Auth_Token
        )
        facilitiesModel.enqueue(object : Callback<FaciBookings> {
            override fun onResponse(
                call: Call<FaciBookings>,
                response: Response<FaciBookings>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.facililities.isNotEmpty()) {
                        if (bookingList.isNotEmpty()) {
                            bookingList.clear()
                        }
                        val facilitiesModel = response.body() as FaciBookings
                        bookingList = facilitiesModel.data.facililities as ArrayList<FaciBookings.Data.Facilility>

                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<FaciBookings>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }
    private fun populateData() {
        if (bookingList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.fbookingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            faciBookingsAdapter = FaciBookingsAdapter(requireContext(), bookingList)
            binding.fbookingsRecyclerView.adapter = faciBookingsAdapter
            faciBookingsAdapter.setCallback(this@BookingsFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    fun cancelBooking(bookFacilityId : Int) {
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
            deleteTenantServiceCall(bookFacilityId)
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }
        deleteMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteTenantServiceCall(bookFacilityId: Int) {

        var customProgressDialog = CustomProgressDialog()

        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        deleteFacilitiesModel = apiInterface.deleteFacility("bearer "+Auth_Token, bookFacilityId)
        deleteFacilitiesModel.enqueue(object : Callback<DeleteFacilityModel>{
            override fun onResponse(
                call: Call<DeleteFacilityModel>,
                response: Response<DeleteFacilityModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

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
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<DeleteFacilityModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickOnPaymentStatus(paymentStatus: FaciBookings.Data.Facilility) {
        if (paymentStatus!= null){
            if (paymentStatus.paymentStatusName == "UnPaid"){
                showMakePaymentDialog(paymentStatus)
            }else{
                showEscalateApprovalDialog()
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showEscalateApprovalDialog() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.facilities_escalate_approval_dialog, null)
        escalateApprovalDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        escalateApprovalDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        escalateApprovalDialog!!.setContentView(view)
        escalateApprovalDialog!!.setCanceledOnTouchOutside(true)
        escalateApprovalDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(escalateApprovalDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        escalateApprovalDialog!!.window!!.attributes = lp

        val textOk = view.findViewById<TextView>(R.id.escalate_dialog_Ok_txt)
        val closeImg = view.findViewById<ImageView>(R.id.escalated_dialog_close)
        closeImg.setOnClickListener {
            if (escalateApprovalDialog!!.isShowing){
                escalateApprovalDialog!!.dismiss()
            }
        }
        textOk.setOnClickListener {

        }

        escalateApprovalDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showMakePaymentDialog(paymentStatus: FaciBookings.Data.Facilility) {
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
        third_party_name_radio_option.text = "Safe Home app"
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
                selectedBookingPayNow(paymentStatus)
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
        payUsingOther!!.show()    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedBookingPayNow(paymentStatus: FaciBookings.Data.Facilility) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.payment_mode_other_dialog, null)
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
        transactionAmountEditTxt = view.findViewById<EditText>(R.id.et_amount)
        transactionNoEditTxt = view.findViewById<EditText>(R.id.et_transaction_number)
        val cancel_btn = view.findViewById<TextView>(R.id.cancel_btn)
        val comments_et: EditText = view.findViewById(R.id.comments_et)
        comments_et.setBackgroundResource(android.R.color.transparent)
        val saveBtn = view.findViewById<TextView>(R.id.save_btn)
        rdGroup = view.findViewById<RadioGroup>(R.id.rdGroup)
        rdGroupTransactionStatus = view.findViewById<RadioGroup>(R.id.rdGroupTransactionStatus)
        // thirdPartyNameRBtn = view.findViewById<RadioButton>(R.id.third_party_name)
        eftRBtn = view.findViewById<RadioButton>(R.id.eft)
        UpiRBtn = view.findViewById<RadioButton>(R.id.Upi)
        chequeRBtn = view.findViewById<RadioButton>(R.id.cheque)
        cash = view.findViewById<RadioButton>(R.id.cash)

        val documentLayout = view.findViewById<RelativeLayout>(R.id.upload_doc_rl)
        uploadDocumentName = view.findViewById<TextView>(R.id.upload_doc_text)
        paid_on_date_txt?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
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

        saveBtn.setOnClickListener {
            if (checkPaymentValidations()) {
                if (paymentModeOther!!.isShowing) {
                    paymentModeOther!!.dismiss()
                }
                updatePaymentForBookFacilityNetworkCall(paymentStatus)
            } else {

            }

        }

        documentLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*" // This sets the MIME type to all file types, you can restrict it to specific types if needed
            startActivityForResult(intent, REQUEST_CODE)
        }
        paymentModeOther!!.show()
    }

    private fun checkPaymentValidations(): Boolean {
        val selectedId = rdGroup.checkedRadioButtonId
        val selectedTransactionStatusId = rdGroupTransactionStatus.checkedRadioButtonId
        if(transactionAmountEditTxt.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Amount Paid field is required", Toast.LENGTH_SHORT).show()
            return false
        }else if (paid_on_date_txt!!.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Paid On field is required", Toast.LENGTH_SHORT).show()
            return false
        } else if (selectedId == -1){
            Toast.makeText(requireContext(), "Payment Mode field is required", Toast.LENGTH_SHORT).show()
            return false
        }else if (transactionNoEditTxt!!.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Transaction Number field is required", Toast.LENGTH_SHORT).show()
            return false
        }else if (selectedTransactionStatusId == -1){
            Toast.makeText(requireContext(), "Transaction Status field is required", Toast.LENGTH_SHORT).show()
            return false
        }else if (uploadDocumentName!!.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Document Type is required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri = data?.data
            // Handle the selected file URI here
            val cursor = requireContext().contentResolver.query(selectedFileUri!!, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                it.moveToFirst()
                fileName = it.getString(nameIndex)
                // Now `fileName` contains the name of the selected file.
                uploadDocumentName.text = fileName.toString()

            }
            //   selectedDocumentUri(selectedFileUri)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updatePaymentForBookFacilityNetworkCall(paymentStatus: FaciBookings.Data.Facilility) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        updatePaymentForBookFacilityCall = apiInterface.updatePaymentForBookFacility(Auth_Token!!, paymentStatus.bookFacilityId,
            100, "UPI", "", "Success", "", "", "")
        updatePaymentForBookFacilityCall.enqueue(object: Callback<AddServiceBookingList> {
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
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

    fun editBookingRequest(faciBookings: FaciBookings.Data.Facilility) {
        try {
            val fIntent = Intent(requireContext(), ListBookNowActivity::class.java)
            fIntent.putExtra("faciBookings", faciBookings)

            fIntent.putExtra("bookType", faciBookings.name)
            fIntent.putExtra("facilityId", faciBookings.facilityId)
            fIntent.putExtra("bookFacilityId", faciBookings.bookFacilityId)
            fIntent.putExtra("from", "bookings")
            fIntent.putExtra("bookByHour", faciBookings.residentsChargeByDay)
            fIntent.putExtra("bookByTime",  faciBookings.residentsChargeByHour)

            fIntent.putExtra("cgstBookByDay", faciBookings.cgstpercentageForResidentsChargeByDay)
            fIntent.putExtra("cgstBookByHour", faciBookings.cgstpercentageForResidentsChargeByHour)

            fIntent.putExtra("sgstBookByDay", faciBookings.sgstpercentageForResidentsChargeByDay)
            fIntent.putExtra("sgstBookByHour", faciBookings.sgstpercentageForResidentsChargeByHour)
            fIntent.putExtra("chargeable", faciBookings.chargeable)

            fIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            requireContext().startActivity(fIntent)
        } catch (e: Exception) {
        }
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
            paid_on_date_txt!!.text = sdf.format(cal.time)
        }
    }

}