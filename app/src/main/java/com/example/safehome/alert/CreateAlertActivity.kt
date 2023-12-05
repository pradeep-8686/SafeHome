package com.example.safehome.alert

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
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityCreateAlertBinding
import com.example.safehome.databinding.ActivityRaiseForumBinding
import com.example.safehome.forums.AddUpdateForumResponse
import com.example.safehome.forums.ForumsListActivity
import com.example.safehome.forums.GetAllForumDetailsModel
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.model.PollsKeepDropdownModel
import com.example.safehome.model.PollsPostedDropDownModel
import com.example.safehome.polls.KeepPollForAdapter
import com.example.safehome.polls.PollsPostedToAdapter
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CreateAlertActivity : AppCompatActivity() {

    private var pollsPostedDropdownList: ArrayList<PollsPostedDropDownModel.Data> = ArrayList()
    private lateinit var pollsPostedDropdownNetworkCall: Call<PollsPostedDropDownModel>
    private var keepsDropDownsList: ArrayList<EmergencyTypeModel.Data> = ArrayList()
    private lateinit var getKeepsDropdownCall: Call<EmergencyTypeModel>
    private lateinit var binding: ActivityCreateAlertBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var addForumServiceCall: Call<AlertDeleteResponse>


    private var availabilityTimePopWindow: PopupWindow? = null
    private var logoutConfirmationDialog: Dialog? = null
    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var selectedItems: String? = ""
    private var selectedPostToList = ArrayList<String>()
    private var topic: String = ""
    private var description: String = ""
    private var keepForumFor: String = ""
    private var forumTo: String = ""
    private var keepPollPopupWindow: PopupWindow? = null

    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private lateinit var alertItem: AlertResponse.Data.Forum
    private var isUpdate: Boolean = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")


        dataFromUpdateForum()
        addAssignToList()


        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, AlertActivity::class.java)
            intent.putExtra("ScreenFrom", "MenuScreenFrag")
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            );

            startActivity(intent)

            onBackPressed()
        }
        binding.btnCancel.setOnClickListener {
            binding.etPollTo.text = ""
            binding.etDescription.text.clear()
            binding.etTopic.text.clear()
            binding.keepPollForTxt.text = ""

        }

        binding.btnPost.setOnClickListener {


            if (validateData()) {

                if (isUpdate) {
                    updatePollNetworkCall()
                } else {

                    addPollNetworkCall()
                }

            }


        }

        binding.etTopic.setOnTouchListener { v, event ->
            if (keepPollPopupWindow != null) {
                if (keepPollPopupWindow!!.isShowing) {
                    keepPollPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }


        binding.etPollTo.setOnClickListener {
            assignToPopup()
        }

        binding.clTvComplaintType.setOnClickListener {
            keepPoll()
        }



        binding.mainLayout.setOnClickListener {

            if (keepPollPopupWindow?.isShowing == true) {
                keepPollPopupWindow?.dismiss()
            }

        }

    }

    private fun dataFromUpdateForum() {
        if (intent.extras != null) {
            if (intent.extras!!.getSerializable("alertItem") != null) {

                isUpdate = true
                binding.tittleTxt.text = "Update Alert"
                binding.btnPost.text = "Update Alert"

                if (intent != null) {
                    alertItem = intent.getSerializableExtra("alertItem") as AlertResponse.Data.Forum
                }

                Log.d("forumItem", alertItem.toString())

                /*
                                if (alertItem.topic != null) {

                                    topic = alertItem.topic

                                    binding.etTopic.text = Editable.Factory.getInstance().newEditable(topic)
                                }*/
                if (alertItem.emergencyTypeName != null) {
                    keepForumFor = alertItem.emergencyTypeId.toString()
                    binding.keepPollForTxt.text = alertItem.emergencyTypeName
                }

                if (alertItem.description != null) {

                    description = alertItem.description

                    binding.etDescription.text =
                        Editable.Factory.getInstance().newEditable(description)
                }



                if (alertItem.notifyTo != null) {

                    var selectedPostName = ArrayList<String>()
                    for (model in alertItem.notifyTo) {
                        selectedPostToList.add(model.postedToId.toString())
                        selectedPostName.add(model.name)
                    }
                    selectedItems = selectedPostName.joinToString()
                    binding.etPollTo.text = selectedPostName.joinToString()
                }


            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addPollNetworkCall() {
        customProgressDialog.progressDialogShow(
            this@CreateAlertActivity,
            this.getString(R.string.loading)
        )
        val notifyTo = ArrayList<Int>()
        for (ids in selectedPostToList) {
            notifyTo.add(ids.toInt())
        }
        val alertRequest = AlertRequest(
            Comments = "Alert", Description = description,
            EmergencyTypeId = keepForumFor.toInt(),
            NotifyToIds = notifyTo
        )
/*
        val forumOptionsMap: MutableMap<String, Any> = HashMap()

        forumOptionsMap["EmergencyTypeId"] = keepForumFor
        forumOptionsMap["Description"] = description
        forumOptionsMap["Comments"] = "Alert"

        forumOptionsMap["NotifyToIds"] = "[${selectedPostToList.joinToString()}]"

        Log.d(
            "pollOptionsMap", forumOptionsMap.toString()
        )  */

        Log.d(
            "pollOptionsMap", alertRequest.toString()
        )

        addForumServiceCall = apiInterface.SendAlertDetails(
            "bearer $Auth_Token", alertRequest
        )
        addForumServiceCall.enqueue(object : Callback<AlertDeleteResponse> {
            override fun onResponse(
                call: Call<AlertDeleteResponse>,
                response: Response<AlertDeleteResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@CreateAlertActivity,
                                        response.body()!!.message.toString()
                                    )
                                    moveToFacilityActivity()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@CreateAlertActivity,
                                        response.body()!!.message.toString()
                                    )
                                    moveToFacilityActivity()

                                }
                            }
                        }
                    } else {
                        customProgressDialog.progressDialogDismiss()
                        moveToFacilityActivity()
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    // Utils.showToast(this@ListBookNowActivity, "your booking is not successful")
                    moveToFacilityActivity()
                }
            }

            override fun onFailure(call: Call<AlertDeleteResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()

                moveToFacilityActivity()
                Utils.showToast(this@CreateAlertActivity, t.message.toString())
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updatePollNetworkCall() {
        customProgressDialog.progressDialogShow(
            this@CreateAlertActivity,
            this.getString(R.string.loading)
        )

       /* val forumOptionsMap: MutableMap<String, Any> = HashMap()

        forumOptionsMap["AlertId"] = alertItem.alertId

        forumOptionsMap["EmergencyTypeId"] = keepForumFor
        forumOptionsMap["Description"] = description
        forumOptionsMap["Comments"] = "Alert"


        forumOptionsMap["NotifyToIds"] = "[${selectedPostToList.joinToString()}]"


        Log.d(
            "pollOptionsMap", forumOptionsMap.toString()
        )
*/

        val notifyTo = ArrayList<Int>()
        for (ids in selectedPostToList) {
            notifyTo.add(ids.toInt())
        }
        val alertRequest = AlertRequest(
            AlertId = alertItem.alertId,
            Comments = "Alert", Description = description,
            EmergencyTypeId = keepForumFor.toInt(),
            NotifyToIds = notifyTo
        )


        Log.d(
            "pollOptionsMap", alertRequest.toString()
        )

        addForumServiceCall = apiInterface.UpdateAlertDetails(
            "bearer $Auth_Token", alertRequest
        )
        addForumServiceCall.enqueue(object : Callback<AlertDeleteResponse> {
            override fun onResponse(
                call: Call<AlertDeleteResponse>,
                response: Response<AlertDeleteResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@CreateAlertActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@CreateAlertActivity,
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

            override fun onFailure(call: Call<AlertDeleteResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()

                //         moveToFacilityActivity()
                Utils.showToast(this@CreateAlertActivity, t.message.toString())
            }
        })
    }

    private fun moveToFacilityActivity() {
        val intent = Intent(this@CreateAlertActivity, AlertActivity::class.java)
        intent.putExtra("from", "bookingsPage")
        startActivity(intent)
        finish()
    }

    private fun validateData(): Boolean {

        topic = binding.etTopic.text.toString().trim()
        description = binding.etDescription.text.toString().trim()
        forumTo = binding.etPollTo.text.toString().trim()

        /*   if (topic.isEmpty()) {
               binding.etTopic.error = "Question is required"
               return false
           }*/


        if (keepForumFor.isEmpty()) {
            Toast.makeText(this, "Emergency Type field is required", Toast.LENGTH_SHORT).show()
            return false
        }


        if (forumTo.isEmpty()) {
            Toast.makeText(this, "Notify To field is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            return false
        }
        return true

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
            this@CreateAlertActivity,
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
                                        this@CreateAlertActivity,
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
                Utils.showToast(this@CreateAlertActivity, t.message.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun assignToPopupDialog(pollsPostedDropdownList: ArrayList<PollsPostedDropDownModel.Data>) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.assign_to_layout, null)
        logoutConfirmationDialog = Dialog(this@CreateAlertActivity, R.style.CustomAlertDialog)
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
            this@CreateAlertActivity,
            this.getString(R.string.loading)
        )
        getKeepsDropdownCall =
            apiInterface.GetAllEmergencyTypeDetailsDropDown("bearer " + Auth_Token)
        getKeepsDropdownCall.enqueue(object : Callback<EmergencyTypeModel> {
            override fun onResponse(
                call: Call<EmergencyTypeModel>,
                response: Response<EmergencyTypeModel>
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
                                        response.body()!!.data as ArrayList<EmergencyTypeModel.Data>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    keepPollDropDown(keepsDropDownsList)
                                }
                                //  populatePollsData(totalPollsList)
                                //   viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@CreateAlertActivity,
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

            override fun onFailure(call: Call<EmergencyTypeModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@CreateAlertActivity, t.message.toString())
            }

        })
    }


    private fun keepPollDropDown(keepsDropDownsList: ArrayList<EmergencyTypeModel.Data>) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        keepPollPopupWindow = PopupWindow(
            view,
            binding.keepPollForTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (keepsDropDownsList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@CreateAlertActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val keepPollForAdapter =
                EmergencyTypeAdapter(this@CreateAlertActivity, keepsDropDownsList)

            dropDownRecyclerView.adapter = keepPollForAdapter
            keepPollForAdapter.setCallback(this@CreateAlertActivity)
        }
        keepPollPopupWindow!!.elevation = 10F
        keepPollPopupWindow!!.showAsDropDown(binding.keepPollForTxt, 0, 0, Gravity.CENTER)
    }

    fun setCallbackComplaintType(keepPollFor: EmergencyTypeModel.Data) {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            }
        }
        if (keepPollFor != null) {
            binding.keepPollForTxt.text = keepPollFor.emergencyTypeName
            // selectedCategoryId = ComplaintStatus

            keepForumFor = keepPollFor.emergencyTypeId.toString()

            if (keepPollFor.emergencyTypeName.equals("Other")) {

                binding.etTopic.visibility = View.VISIBLE
                binding.tvTopic.visibility = View.VISIBLE
            } else {
                binding.etTopic.visibility = View.GONE
                binding.tvTopic.visibility = View.GONE
            }

        }
        Log.d(HistoryFragment.TAG, "$keepPollFor")
    }
}
