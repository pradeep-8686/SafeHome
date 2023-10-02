package com.example.safehome.maintenance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.Utils.Companion.formatDateMonthYear
import com.example.safehome.adapter.MyDuesAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentMyDuesBinding
import com.example.safehome.model.MyDuesMaintenanceDetails
import com.example.safehome.model.TotalDuesMaintenanceList
import com.example.safehome.model.UpdateMaintenanceModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MyDuesFragment : Fragment() {

    private lateinit var  uploadDocumentName: TextView
    private val REQUEST_CODE: Int = 100
    private lateinit var Auth_Token: String
    private lateinit var residentId: String
    private lateinit var due_type_tv: TextView
    private lateinit var invoice_number_et: EditText
    private lateinit var invoice_period_et: EditText
    private lateinit var invoice_date_et: TextView
    private lateinit var due_date_et: TextView
    private lateinit var total_invoice_amount_et: EditText
    private lateinit var binding: FragmentMyDuesBinding
    private var myDyesList: ArrayList<MyDuesMaintenanceDetails> = ArrayList()
    private lateinit var myDuesAdapter: MyDuesAdapter
    private var editInvoiceDialog: Dialog? = null
    private var payConfirmDialog: Dialog? = null
    private var paymentModeOther: Dialog? = null
    private var payUsingDialog: Dialog? = null
    private var paid_on_date_txt: TextView? = null
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var getDuesMaintenanceDetails: Call<TotalDuesMaintenanceList>
    private lateinit var updateMaintenanceDetails: Call<UpdateMaintenanceModel>
    private lateinit var apiInterface: APIInterface
    private var imageMultipartBody: MultipartBody.Part? = null
    var totalDues: Double? = 0.0

    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyDuesBinding.inflate(inflater, container, false)

        inIt()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        binding.totalDueText.setText("Dues :" + getString(R.string.Rs) + " 0")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")!!
        residentId = Utils.getStringPref(requireContext(), "residentId", "")!!
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())

        getDueMaintenanceDetailsServiceCall()

        //   addMyDuesData()
        // populateData()
    }

    /*
        private fun createMultipartBody(myDuesFragment: MyDuesFragment, selectedImageUri: Any): MultipartBody.Part? {

        }
    */

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDueMaintenanceDetailsServiceCall() {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        getDuesMaintenanceDetails =
            apiInterface.getDueMaintenanceDetailsByResident("bearer " + Auth_Token, residentId.toInt())
        getDuesMaintenanceDetails.enqueue(object : Callback<TotalDuesMaintenanceList> {
            override fun onResponse(
                call: Call<TotalDuesMaintenanceList>,
                response: Response<TotalDuesMaintenanceList>
            ) {
                customProgressDialog.progressDialogDismiss()

                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (myDyesList.isNotEmpty()) {
                                        myDyesList.clear()
                                    }
                                    myDyesList = response.body()!!.data as ArrayList<MyDuesMaintenanceDetails>
                                    Log.e("myDyesList", myDyesList.size.toString())

                                    if (myDyesList.isNotEmpty()) {
                                        for (i in 0 until myDyesList.size) {
                                            totalDues =
                                                totalDues!!.plus(myDyesList[i].invoiceAmount.toDouble())
                                        }
                                        binding.totalDueText.text =
                                            "Dues:"+"${context!!.getString(R.string.rupee)}${totalDues}/-"
                                    }

                                }
                                populateData(myDyesList)

                            }

                            2 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {

                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message
                                    )
                                }
                            }

                            3 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {

                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message
                                    )
                                }
                            }
                        }
                    }
                }

            }

            override fun onFailure(call: Call<TotalDuesMaintenanceList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

    }

    private fun populateData(myDyesList: ArrayList<MyDuesMaintenanceDetails>) {
        if (myDyesList.size == 0) {
            binding.emptyMaintenanceTxt.visibility = View.VISIBLE
        } else {
            binding.emptyMaintenanceTxt.visibility = View.GONE

            binding.myDuesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            myDuesAdapter = MyDuesAdapter(requireContext(), myDyesList)
            binding.myDuesRecyclerView.adapter = myDuesAdapter
            myDuesAdapter.setCallback(this@MyDuesFragment)
        }
    }

    private fun addMyDuesData() {
        /*  myDyesList.add(
              MyDuesMaintenanceDetails(
                  "Common Area Maintainance", "#6653", "Apr 2023",
                  "15 May, 2021", 500, R.drawable.common_area_maintainance_icon, "Unpaid"
              )
          )
          myDyesList.add(
              MyDuesMaintenanceDetails(
                  "Electricity Bill", "#6654", " Apr 2023",
                  "15 May, 2021", 600, R.drawable.electricity_bill_icon1,"Unpaid"
              )
          )
          myDyesList.add(
              MyDuesMaintenanceDetails(
                  "Water Bill", "#6655", " Apr 2023",
                  "15 May, 2021", 700, R.drawable.water_bill_icon,"Unpaid"
              )
          )*/
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedInvoice(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
        editInvoicePopup(myDuesMaintenanceDetails)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun editInvoicePopup(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_invoice_edit_pop_up_dialog, null)
        editInvoiceDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        editInvoiceDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        editInvoiceDialog!!.setContentView(view)
        editInvoiceDialog!!.setCanceledOnTouchOutside(true)
        editInvoiceDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(editInvoiceDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.95).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        editInvoiceDialog!!.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)

        due_type_tv = view.findViewById<TextView>(R.id.due_type_tv)
        due_type_tv.setBackgroundResource(android.R.color.transparent)

        invoice_number_et = view.findViewById<EditText>(R.id.invoice_number_et)
        invoice_number_et.setBackgroundResource(android.R.color.transparent)

        invoice_period_et = view.findViewById<EditText>(R.id.invoice_period_et)
        invoice_period_et.setBackgroundResource(android.R.color.transparent)

        invoice_date_et = view.findViewById<TextView>(R.id.invoice_date_txt)
        invoice_date_et.setBackgroundResource(android.R.color.transparent)

        due_date_et = view.findViewById<TextView>(R.id.due_date_txt)
        due_date_et.setBackgroundResource(android.R.color.transparent)

        total_invoice_amount_et = view.findViewById<EditText>(R.id.total_invoice_amount_et)
        total_invoice_amount_et.setBackgroundResource(android.R.color.transparent)

        if (myDuesMaintenanceDetails.maintenanceType.isNotEmpty()) {
            due_type_tv.text = myDuesMaintenanceDetails.maintenanceType
        }
        if (myDuesMaintenanceDetails.invoiceNumber.isNotEmpty()) {
            invoice_number_et.setText(myDuesMaintenanceDetails.invoiceNumber)
        }
        if (myDuesMaintenanceDetails.invoiceFromDate.isNotEmpty() && myDuesMaintenanceDetails.invoiceToDate.isNotEmpty()) {
            val invoiceFromDates = myDuesMaintenanceDetails.invoiceFromDate.split("T")
            val invoiceFromDate = invoiceFromDates[0]
            val invoiceToDates = myDuesMaintenanceDetails.invoiceToDate.split("T")
            val invoiceToDate = invoiceToDates[0]
            val monthYear = Utils.formatDateToMonth(invoiceToDate)
            invoice_period_et.setText(monthYear)
            invoice_date_et.text = formatDateMonthYear(invoiceFromDate)
                due_date_et.text = formatDateMonthYear(invoiceToDate)

        }
        if (myDuesMaintenanceDetails.invoiceAmount != null) {
            total_invoice_amount_et.setText(myDuesMaintenanceDetails.invoiceAmount.toString())
        }

        close.setOnClickListener {
            if (editInvoiceDialog!!.isShowing) {
                editInvoiceDialog!!.dismiss()
            }
        }
        editInvoiceDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedPaid(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
        if (myDuesMaintenanceDetails != null) {
             payUsingPopup(myDuesMaintenanceDetails)
            //paymentModeOthers(myDuesMaintenanceDetails)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun payUsingPopup(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.pay_using_in_daily_help_dialog, null)
        payUsingDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        payUsingDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payUsingDialog!!.setContentView(view)
        payUsingDialog!!.setCanceledOnTouchOutside(true)
        payUsingDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(payUsingDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        payUsingDialog!!.window!!.attributes = lp

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
                if (payUsingDialog!!.isShowing) {
                    payUsingDialog!!.dismiss()
                }
                paymentModeOthers(myDuesMaintenanceDetails)
            }
        }

        close.setOnClickListener {
            if (payUsingDialog!!.isShowing) {
                payUsingDialog!!.dismiss()
            }
        }


        payUsingDialog!!.show()
    }


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun paymentModeOthers(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
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
        val cancel_btn = view.findViewById<TextView>(R.id.cancel_btn)
        val comments_et: EditText = view.findViewById(R.id.comments_et)
        comments_et.setBackgroundResource(android.R.color.transparent)
        val saveBtn = view.findViewById<TextView>(R.id.save_btn)
        val documentLayout = view.findViewById<RelativeLayout>(R.id.upload_doc_rl)
        uploadDocumentName = view.findViewById<TextView>(R.id.upload_doc_text)
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
        saveBtn.setOnClickListener {
            updateMaintenancePaymentNetworkCall(myDuesMaintenanceDetails)
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
        documentLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*" // This sets the MIME type to all file types, you can restrict it to specific types if needed
            startActivityForResult(intent, REQUEST_CODE)
        }
        paymentModeOther!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri = data?.data
            // Handle the selected file URI here
            val cursor = requireContext().contentResolver.query(selectedFileUri!!, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                it.moveToFirst()
                val fileName = it.getString(nameIndex)
                // Now `fileName` contains the name of the selected file.
                uploadDocumentName.text = fileName.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateMaintenancePaymentNetworkCall(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
        val parsedValue: Double = myDuesMaintenanceDetails.invoiceAmount.toDouble()

        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        updateMaintenanceDetails = apiInterface.UpdateMaintenancePayment(
            "bearer " + Auth_Token,
            myDuesMaintenanceDetails.maintenanceType,
            myDuesMaintenanceDetails.transactionId.toInt(),
            residentId.toInt(),
            parsedValue.toInt(),
            "UPI",
            "Success",
            "",
            ""
        )

        updateMaintenanceDetails.enqueue(object : Callback<UpdateMaintenanceModel> {
            override fun onResponse(
                call: Call<UpdateMaintenanceModel>,
                response: Response<UpdateMaintenanceModel>
            ) {

                customProgressDialog.progressDialogDismiss()

                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                   /* Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message
                                    )
                             */
                                }
                            }

                            2 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {

                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message
                                    )
                                }
                            }

                            3 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {

                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message
                                    )
                                }

                            }

                        }
                    }


                }
            }


            override fun onFailure(call: Call<UpdateMaintenanceModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())

            }

        })

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
    private fun payConfirmPopup(myDuesMaintenanceDetails: MyDuesMaintenanceDetails) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.pay_total_amount_confirm_popup, null)
        payConfirmDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        payConfirmDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payConfirmDialog!!.setContentView(view)
        payConfirmDialog!!.setCanceledOnTouchOutside(true)
        payConfirmDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(payConfirmDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        payConfirmDialog!!.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)

        close.setOnClickListener {
            if (payConfirmDialog!!.isShowing) {
                payConfirmDialog!!.dismiss()
            }
        }

        payConfirmDialog!!.show()
    }
}