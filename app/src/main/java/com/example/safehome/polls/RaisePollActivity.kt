package com.example.safehome.polls

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
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
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityRaisePollBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.model.GetAllPollDetailsModel
import com.example.safehome.model.PollsKeepDropdownModel
import com.example.safehome.model.PollsPostedDropDownModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RaisePollActivity : AppCompatActivity() {
    private var pollsPostedDropdownList: ArrayList<PollsPostedDropDownModel.Data> = ArrayList()
    private lateinit var pollsPostedDropdownNetworkCall: Call<PollsPostedDropDownModel>
    private var keepsDropDownsList: ArrayList<PollsKeepDropdownModel.Data> = ArrayList()
    private lateinit var getKeepsDropdownCall: Call<PollsKeepDropdownModel>
    private lateinit var binding: ActivityRaisePollBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var addPollServiceCall: Call<AddPollResponse>

    private var attachPhotoWindow: PopupWindow? = null

    private val IMAGE_CHOOSE = 1000;
    private val PERMISSION_CODE = 1001;
    private var availabilityTimePopWindow: PopupWindow? = null
    private var logoutConfirmationDialog: Dialog? = null
    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var selectedItems: String? = ""
    private var selectedPostToList = ArrayList<String>()
    private var question: String = ""
    private var pollFor: String = ""
    private var pollTo: String = ""
    private var imageFile: String = ""
    private var isPublic: Boolean? = null
    private var keepPollPopupWindow: PopupWindow? = null
    private var keepPollList: ArrayList<String> = ArrayList()
    val optionDataList = mutableListOf<String>()
    val optionDataIdList = mutableListOf<String>()

    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private lateinit var pollItem: GetAllPollDetailsModel.Data.Poll
    private var isUpdate: Boolean = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaisePollBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        for (i in 0 until 2) {
            addOptions()
        }

        dataFromUpdatePoll()
        //   setKeepPollData()
        addAssignToList()


        binding.backBtnClick.setOnClickListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
            onBackPressed()
        }
        binding.btnCancel.setOnClickListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
            onBackPressed()

        }

        binding.radioGroup.setOnCheckedChangeListener { view, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                binding.radioButtonYes -> {
                    isPublic = true
                }

                binding.radioButtonNo -> {
                    isPublic = false

                }
            }
        }

        binding.btnCreate.setOnClickListener {


            if (validateData()) {

                if (isUpdate) {
                    updatePollNetworkCall()
                } else {

                    addPollNetworkCall()
                }

            }


            Log.d("OptionData", optionDataList.joinToString())
        }


        binding.tvAttachPhoto.setOnClickListener {

            attachPhotoPopup()
        }
        binding.imgAttachPhoto.setOnClickListener {

            attachPhotoPopup()

        }
        binding.etPollTo.setOnClickListener {
            assignToPopup()
        }

        binding.clTvComplaintType.setOnClickListener {
            keepPoll()
        }

        binding.tvAddOption.setOnClickListener {
            addOptions()
        }

        binding.startDateTxt?.setOnClickListener {
            val startDateDialog = DatePickerDialog(
                this@RaisePollActivity,
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH)
            )

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        binding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(
                this@RaisePollActivity,
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH)
            )
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(
                    binding.startDateTxt!!.text.toString(),
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


        binding.mainLayout.setOnClickListener {

            if (attachPhotoWindow?.isShowing == true) {
                attachPhotoWindow?.dismiss()
            }

            if (keepPollPopupWindow?.isShowing == true) {
                keepPollPopupWindow?.dismiss()
            }

        }

    }

    private fun dataFromUpdatePoll() {
        if (intent.extras != null) {
            if (intent.extras!!.getSerializable("pollItem") != null) {

                isUpdate = true
                binding.tittleTxt.text = "Update a Poll"
                binding.btnCreate.text = "Update"

                pollItem =
                    intent.extras!!.getSerializable("pollItem") as GetAllPollDetailsModel.Data.Poll

                Log.d("pollItem", pollItem.toString())

                if (pollItem.attachPhoto != null) {
                    imageFile = pollItem.attachPhoto//Need to display image
                }

                if (pollItem.question != null) {

                    question = pollItem.question

                    binding.etQuestion.text = Editable.Factory.getInstance().newEditable(question)
                }

                if (pollItem.keepPollForId != null) {
                    pollFor = pollItem.keepPollForId.toString()
                    binding.keepPollForTxt.text = pollItem.keepFor
                }
                if (pollItem.resultTobePublic != null) {
                    isPublic = pollItem.resultTobePublic

                    if (pollItem.resultTobePublic) {
                        binding.radioGroup.check(binding.radioButtonYes.id)
//                        binding.radioButtonYes.isSelected = true
                    } else {
                        binding.radioGroup.check(binding.radioButtonNo.id)

//                        binding.radioButtonNo.isSelected = false

                    }
                }

                if (pollItem.pollOptions != null) {

                    optionDataList.clear()
                    optionDataIdList.clear()
                    binding.itemContainer.removeAllViews()

                    for (model in pollItem.pollOptions) {

                        val option = model.optionName
                        val optionId = model.optionId
                        if (option != null) {

                            optionDataList.add(option)
                            optionDataIdList.add(optionId.toString())
                            addOptions(option)
                        }
                    }

                }
                if (pollItem.postedTo != null) {

                    var selectedPostName = ArrayList<String>()
                    for (model in pollItem.postedTo) {
                        selectedPostToList.add(model.postedToId.toString())
                        selectedPostName.add(model.name)
                    }
                    selectedItems = selectedPostName.joinToString()
                    binding.etPollTo.text = selectedPostName.joinToString()
                }

                if (pollItem.fromDate != null) {
                    binding.startDateTxt.text = pollItem.fromDate
                }
                if (pollItem.fromDate != null) {
                    binding.endDateTxt.text = pollItem.toDate
                }

            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addPollNetworkCall() {
        customProgressDialog.progressDialogShow(
            this@RaisePollActivity,
            this.getString(R.string.loading)
        )
        var startDate = binding.startDateTxt.text.toString()
        if (!startDate.contains("DD/MM/YYYY")) {
            startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        } else {
            startDate = ""
        }
        var endDate = binding.endDateTxt.text.toString()
        if (!endDate.contains("DD/MM/YYYY")) {
            endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        } else {
            endDate = ""
        }


        val pollOptionsMap: MutableMap<String, Any> = HashMap()

//        pollOptionsMap["PollId"] = PollId
        pollOptionsMap["Question"] = question
        pollOptionsMap["KeepPollForId"] = pollFor
        pollOptionsMap["FromDate"] = startDate
        pollOptionsMap["ToDate"] = endDate
        pollOptionsMap["Comments"] = "Submit your poll"
        pollOptionsMap["ResultTobePublic"] = isPublic!!
        pollOptionsMap["AttachPhoto"] = imageFile

        for (i in 0 until optionDataList.size) {
            pollOptionsMap["PollOptions[$i].OptionName"] = optionDataList[i]
        }
        for (i in 0 until selectedPostToList.size) {
            pollOptionsMap["PostedToIds[$i]"] = selectedPostToList[i]
        }

        Log.d(
            "pollOptionsMap", pollOptionsMap.toString()
        )

        addPollServiceCall = apiInterface.addPoll(
            "bearer $Auth_Token", pollOptionsMap
        )
        addPollServiceCall.enqueue(object : Callback<AddPollResponse> {
            override fun onResponse(
                call: Call<AddPollResponse>,
                response: Response<AddPollResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaisePollActivity,
                                        response.body()!!.message.toString()
                                    )
                                  //  moveToPollsActivity()
                                   moveToFacilityActivity()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaisePollActivity,
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

            override fun onFailure(call: Call<AddPollResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()

                //         moveToFacilityActivity()
                Utils.showToast(this@RaisePollActivity, t.message.toString())
            }
        })
    }

    private fun moveToPollsActivity() {
        TODO("Not yet implemented")
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updatePollNetworkCall() {
        customProgressDialog.progressDialogShow(
            this@RaisePollActivity,
            this.getString(R.string.loading)
        )
        var startDate = binding.startDateTxt.text.toString()
        if (!startDate.contains("DD/MM/YYYY")) {
            startDate = Utils.changeDateFormatToMMDDYYYY(startDate)
        } else {
            startDate = ""
        }
        var endDate = binding.endDateTxt.text.toString()
        if (!endDate.contains("DD/MM/YYYY")) {
            endDate = Utils.changeDateFormatToMMDDYYYY(endDate)
        } else {
            endDate = ""
        }


        val pollOptionsMap: MutableMap<String, Any> = HashMap()

        pollOptionsMap["PollId"] = pollItem.pollId
        pollOptionsMap["Question"] = question
        pollOptionsMap["KeepPollForId"] = pollFor
        pollOptionsMap["FromDate"] = startDate
        pollOptionsMap["ToDate"] = endDate
        pollOptionsMap["Comments"] = "Submit your poll"
        pollOptionsMap["ResultTobePublic"] = isPublic!!
        pollOptionsMap["AttachPhoto"] = imageFile

        for (i in 0 until optionDataList.size) {
            pollOptionsMap["PollOptions[$i].OptionName"] = optionDataList[i]
            if (optionDataIdList[i].isNotEmpty()){

                pollOptionsMap["PollOptions[$i].OptionId"] = optionDataIdList[i]
            }

            /* if (optionDataList.size != optionDataIdList.size){

             }else{

                 pollOptionsMap["PollOptions[$i].OptionId"] = optionDataIdList[i]
             }*/
        }
        for (i in 0 until selectedPostToList.size) {
            pollOptionsMap["PostedToIds[$i]"] = selectedPostToList[i]
        }

        Log.d(
            "pollOptionsMap", pollOptionsMap.toString()
        )

        addPollServiceCall = apiInterface.updatePoll(
            "bearer $Auth_Token", pollOptionsMap
        )
        addPollServiceCall.enqueue(object : Callback<AddPollResponse> {
            override fun onResponse(
                call: Call<AddPollResponse>,
                response: Response<AddPollResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaisePollActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaisePollActivity,
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

            override fun onFailure(call: Call<AddPollResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()

                //         moveToFacilityActivity()
                Utils.showToast(this@RaisePollActivity, t.message.toString())
            }
        })
    }

    private fun moveToFacilityActivity() {
        val intent = Intent(this@RaisePollActivity, PollsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateData(): Boolean {

        question = binding.etQuestion.text.toString().trim()
//        pollFor = binding.keepPollForTxt.text.toString().trim()
        pollTo = binding.etPollTo.text.toString().trim()

        if (question.isEmpty()) {
            binding.etQuestion.error = "Question is required"
            return false
        }

        optionDataList.clear()
//        optionDataIdList.clear()
        for (i in 0 until binding.itemContainer.childCount) {
            val childView = binding.itemContainer.getChildAt(i)
            if (childView is ConstraintLayout) {
                val editText = childView.findViewById<EditText>(R.id.et_option)
                val text = editText.text.toString()
                if (text.isEmpty()) {
                    editText.error = "Option ${i + 1} is required"
                    return false
                } else {
                    optionDataList.add(text)
                    if (binding.itemContainer.childCount > optionDataIdList.size) {
                        optionDataIdList.add("")
                    }
                }
            }
        }

        if (pollFor.isEmpty()) {
            Toast.makeText(this, "Keep Poll For field is required", Toast.LENGTH_SHORT).show()
            return false
        }


        if (pollTo.isEmpty()) {
            Toast.makeText(this, "Post To field is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (isPublic == null) {
            Toast.makeText(this, "Do you want results to be public?", Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }

    private fun addOptions(option: String? = "") {

        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.add_option_item, null, false)

        val tv_option: TextView = itemView.findViewById(R.id.tv_option)
        val et_option: EditText = itemView.findViewById(R.id.et_option)

        et_option.text = Editable.Factory.getInstance().newEditable(option)
        // Customize label and hint if needed
        tv_option.text = "Option ${binding.itemContainer.childCount + 1}* "
        binding.itemContainer.addView(itemView)

    }

    private fun attachPhotoPopup() {
        if (attachPhotoWindow != null) {
            if (attachPhotoWindow!!.isShowing) {
                attachPhotoWindow!!.dismiss()
            } else {
                attachPhotoPopupDropDown()
            }
        } else {
            attachPhotoPopupDropDown()
        }
    }

    private fun attachPhotoPopupDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.attach_photo_down_layout, null)

        attachPhotoWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val photo_library_cl = view.findViewById<ConstraintLayout>(R.id.photo_library_cl)
        photo_library_cl.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    chooseImageFromGallery();
                }
            } else {
                chooseImageFromGallery();
            }

        }

        attachPhotoWindow!!.elevation = 10F
        attachPhotoWindow!!.showAsDropDown(binding.imgAttachPhoto, 0, 0, Gravity.CENTER)
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
            //   viewImage.setImageURI(data?.data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun assignToPopup() {
        if (availabilityTimePopWindow != null) {
            if (availabilityTimePopWindow!!.isShowing) {
                availabilityTimePopWindow!!.dismiss()
            } else {
//                    availabilityTimeDropDown()
                //    assignToPopupDialog()
                pollsPostedDropdownCall()
            }
        } else {
//                availabilityTimeDropDown()
            //  assignToPopupDialog()
            pollsPostedDropdownCall()

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun pollsPostedDropdownCall() {
        customProgressDialog.progressDialogShow(
            this@RaisePollActivity,
            this.getString(R.string.loading)
        )
        pollsPostedDropdownNetworkCall = apiInterface.getAllPostedToDropDown("bearer " + Auth_Token)
        pollsPostedDropdownNetworkCall.enqueue(object : Callback<PollsPostedDropDownModel> {
            override fun onResponse(
                call: Call<PollsPostedDropDownModel>,
                response: Response<PollsPostedDropDownModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (pollsPostedDropdownList.isNotEmpty()) {
                                        pollsPostedDropdownList.clear()
                                    }
                                    pollsPostedDropdownList =
                                        response.body()!!.data as ArrayList<PollsPostedDropDownModel.Data>
                                    assignToPopupDialog(pollsPostedDropdownList)
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                                //  populatePollsData(totalPollsList)
                                //   viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaisePollActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<PollsPostedDropDownModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@RaisePollActivity, t.message.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun assignToPopupDialog(pollsPostedDropdownList: ArrayList<PollsPostedDropDownModel.Data>) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.assign_to_layout, null)
        logoutConfirmationDialog = Dialog(this@RaisePollActivity, R.style.CustomAlertDialog)
        logoutConfirmationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutConfirmationDialog!!.setContentView(view)
        logoutConfirmationDialog!!.setCanceledOnTouchOutside(true)
        logoutConfirmationDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(logoutConfirmationDialog!!.window!!.attributes)

//        lp.width = (Utils.screenWidth * 0.9).toInt()
//        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        lp.gravity = Gravity.CENTER
//        logoutConfirmationDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val yes_btn: TextView = view.findViewById(R.id.yes_btn)


        val recyclerView: RecyclerView = view.findViewById(R.id.rv_available_time)
        val availabilityTimeList = availabiltyTimeList

        val adapter = PollsPostedToAdapter(this, pollsPostedDropdownList, selectedItems!!)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        yes_btn.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }

            selectedItems = ""
            selectedPostToList.clear()
            selectedPostToList = adapter.getSelectedItemsId() as ArrayList<String>
            val selectedItemsName = adapter.getSelectedItems().joinToString()
            selectedItems = adapter.getSelectedItems().joinToString()
            binding.etPollTo.text = selectedItemsName
            Log.d("SelectedIds", selectedItems!!)
        }


        close.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }
        }

        logoutConfirmationDialog!!.show()
    }

    private fun addAssignToList() {

        availabiltyTimeList.add(AvailabilityTime("All Members"))
        availabiltyTimeList.add(AvailabilityTime("Owners"))
        availabiltyTimeList.add(AvailabilityTime("Tenants"))
        availabiltyTimeList.add(AvailabilityTime("Admin"))
        availabiltyTimeList.add(AvailabilityTime("President"))
        availabiltyTimeList.add(AvailabilityTime("Teasurer"))
        availabiltyTimeList.add(AvailabilityTime("Security"))
        availabiltyTimeList.add(AvailabilityTime("Association \nMembers"))

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun keepPoll() {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            } else {
                //   keepPollDropDown()
                getKeepPollDropDownsDataCall();

            }
        } else {
            //  keepPollDropDown()
            getKeepPollDropDownsDataCall();

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getKeepPollDropDownsDataCall() {
        customProgressDialog.progressDialogShow(
            this@RaisePollActivity,
            this.getString(R.string.loading)
        )
        getKeepsDropdownCall = apiInterface.getKeepForDropDown("bearer " + Auth_Token)
        getKeepsDropdownCall.enqueue(object : Callback<PollsKeepDropdownModel> {
            override fun onResponse(
                call: Call<PollsKeepDropdownModel>,
                response: Response<PollsKeepDropdownModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (keepsDropDownsList.isNotEmpty()) {
                                        keepsDropDownsList.clear()
                                    }
                                    keepsDropDownsList =
                                        response.body()!!.data as ArrayList<PollsKeepDropdownModel.Data>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    keepPollDropDown(keepsDropDownsList)
                                }
                                //  populatePollsData(totalPollsList)
                                //   viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaisePollActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<PollsKeepDropdownModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@RaisePollActivity, t.message.toString())
            }

        })
    }


    private fun keepPollDropDown(keepsDropDownsList: ArrayList<PollsKeepDropdownModel.Data>) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        keepPollPopupWindow = PopupWindow(
            view,
            binding.keepPollForTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (keepsDropDownsList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@RaisePollActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val keepPollForAdapter = KeepPollForAdapter(this@RaisePollActivity, keepsDropDownsList)

            dropDownRecyclerView.adapter = keepPollForAdapter
            keepPollForAdapter.setCallbackComplaintType(this@RaisePollActivity)
        }
        keepPollPopupWindow!!.elevation = 10F
        keepPollPopupWindow!!.showAsDropDown(binding.keepPollForTxt, 0, 0, Gravity.CENTER)
    }

    fun setCallbackComplaintType(keepPollFor: PollsKeepDropdownModel.Data) {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            }
        }
        if (keepPollFor != null) {
            binding.keepPollForTxt.text = keepPollFor.keepFor
            // selectedCategoryId = ComplaintStatus

            pollFor = keepPollFor.keepId.toString()

            if (keepPollFor.equals("Custom")) {

                binding.tvDate.visibility = View.VISIBLE
                binding.llDate.visibility = View.VISIBLE
            } else {
                binding.tvDate.visibility = View.GONE
                binding.llDate.visibility = View.GONE
            }
        }
        Log.d(HistoryFragment.TAG, "$keepPollFor")
    }

    private fun setKeepPollData() {
        keepPollList.add("1 Day")
        keepPollList.add("3 Days")
        keepPollList.add("1 Week")
        keepPollList.add("2 Weeks")
        keepPollList.add("1 Month")
        keepPollList.add("3 Months")
        keepPollList.add("6 Months")
        keepPollList.add("Always")
        keepPollList.add("Custom")
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
            binding.endDateTxt!!.text = sdf.format(cal_endDate.getTime())
        }
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


}