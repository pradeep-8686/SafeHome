package com.example.safehome.dailyhelp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.SignInActivity
import com.example.safehome.adapter.AvailabilityTimeAdapter
import com.example.safehome.adapter.AvailablityTimeAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityDailyHelpMemberListBinding
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.model.DailyHelpStaffModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class DailyHelpMemberListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDailyHelpMemberListBinding
    private var dailyHelpMembersList: ArrayList<DailyHelpStaffModel.Data> = ArrayList()
    private lateinit var dailyHelpMemberListAdapter: DailyHelpMemberListAdapter
    private var MemeberRole: String? = ""
    private var MemberRoleName: String? = ""

    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var availabilityTimePopWindow: PopupWindow? = null

    private var avilabilityOnList: ArrayList<String> = ArrayList()
    private var availabilityOnPopupWindow: PopupWindow? = null

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var dailyHelpStaffModel : Call<DailyHelpStaffModel>
    private var logoutConfirmationDialog: Dialog? = null
    private var selectedItems : String? = ""
    private var seleteAvailableOn : String? = ""


    var User_Id: String? = ""
    var Auth_Token: String? = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyHelpMemberListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        MemeberRole = intent.getStringExtra("MemberRole")
        MemberRoleName = intent.getStringExtra("MemberRoleName")
        binding.tittleTxt.setText(MemberRoleName)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "residentId", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, DailyHelpActivity::class.java)
            startActivity(intent)
            finish()
        }

//      addDailyHelpMemberListData()
        getDailyHelpList("", "")
        populateData()
        addAvailableOnList()
        addAvailableTimeList()
        dropdowns()


        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (dailyHelpMembersList.isNotEmpty()) {
                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    fun filter(text: String) {

        val myDuesList = ArrayList<DailyHelpStaffModel.Data>()
        val courseAry: ArrayList<DailyHelpStaffModel.Data> = dailyHelpMembersList

        for (eachCourse in courseAry) {
            var sortList = ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>()
            var distinctSortedList = ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>()

            for (model in eachCourse.staffworkingDetails){
                if (model.residentdetais != null){
                    sortList.add(model)
                }
            }
            distinctSortedList = sortList.distinctBy { it.residentdetais!!.flatNo} as ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>
            val flatNo = distinctSortedList.joinToString(", ") { it1 ->"${it1.residentdetais!!.block} ${it1.residentdetais!!.flatNo}" }
            if (eachCourse.staffName!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault())) ||
                eachCourse.availableOn!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))||
                flatNo!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        dailyHelpMemberListAdapter.filterList(myDuesList);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDailyHelpList( availableOn : String? = null, availableTime : String? = null) {
        //progressbar
        customProgressDialog.progressDialogShow(
            this,
            this.getString(R.string.loading)
        )

        // here sign up service call
        dailyHelpStaffModel = apiInterface.getDailyHelpStaffList(
            "Bearer " + Auth_Token, MemeberRole!!, availableOn!!,availableTime!!
        )
        dailyHelpStaffModel.enqueue(object : Callback<DailyHelpStaffModel> {
            override fun onResponse(
                call: Call<DailyHelpStaffModel>,
                response: Response<DailyHelpStaffModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (dailyHelpMembersList.isNotEmpty()) {
                            dailyHelpMembersList.clear()
                        }
                        val  dailyHelpRoles = response.body() as DailyHelpStaffModel
                        dailyHelpMembersList = dailyHelpRoles.data as ArrayList<DailyHelpStaffModel.Data>

                    } else {
                        // vehilceModelDropDown()
                        if (dailyHelpMembersList.isNotEmpty()) {
                            dailyHelpMembersList.clear()
                        }
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<DailyHelpStaffModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@DailyHelpMemberListActivity, t.message.toString())
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addAvailableTimeList() {

        availabiltyTimeList.add(AvailabilityTime("5A.M-6A.M"))
        availabiltyTimeList.add(AvailabilityTime("6A.M-7A.M"))
        availabiltyTimeList.add(AvailabilityTime("7A.M-8A.M"))

        availabiltyTimeList.add(AvailabilityTime("8A.M-9A.M"))
        availabiltyTimeList.add(AvailabilityTime("9A.M-10A.M"))
        availabiltyTimeList.add(AvailabilityTime("10A.M-11A.M"))

        availabiltyTimeList.add(AvailabilityTime("11A.M-12P.M"))
        availabiltyTimeList.add(AvailabilityTime("12P.M-1P.M"))
        availabiltyTimeList.add(AvailabilityTime("1P.M-2P.M"))

        availabiltyTimeList.add(AvailabilityTime("2P.M-3P.M"))
        availabiltyTimeList.add(AvailabilityTime("3P.M-4P.M"))
        availabiltyTimeList.add(AvailabilityTime("4P.M-5P.M"))

        availabiltyTimeList.add(AvailabilityTime("5P.M-6P.M"))
        availabiltyTimeList.add(AvailabilityTime("6P.M-7P.M"))
        availabiltyTimeList.add(AvailabilityTime("7P.M-8P.M"))

        availabiltyTimeList.add(AvailabilityTime("8P.M-9P.M"))
        availabiltyTimeList.add(AvailabilityTime("9P.M-10P.M"))
        availabiltyTimeList.add(AvailabilityTime("10P.M-11P.M"))

        /*
                 availabiltyTimeList.add(AvailabilityTime("5 A.M - 6 A.M"))
                availabiltyTimeList.add(AvailabilityTime("6 A.M - 7 A.M"))
                availabiltyTimeList.add(AvailabilityTime("7 A.M - 8 A.M"))

                availabiltyTimeList.add(AvailabilityTime("8 A.M - 9 A.M"))
                availabiltyTimeList.add(AvailabilityTime("9 A.M - 10 A.M"))
                availabiltyTimeList.add(AvailabilityTime("10 A.M - 11 A.M"))

                availabiltyTimeList.add(AvailabilityTime("11 A.M - 12 P.M"))
                availabiltyTimeList.add(AvailabilityTime("12 P.M - 1 P.M"))
                availabiltyTimeList.add(AvailabilityTime("1 P.M - 2 P.M"))

                availabiltyTimeList.add(AvailabilityTime("2 P.M - 3 P.M"))
                availabiltyTimeList.add(AvailabilityTime("3 P.M - 4 P.M"))
                availabiltyTimeList.add(AvailabilityTime("4 P.M - 5 P.M"))

                availabiltyTimeList.add(AvailabilityTime("5 P.M - 6 P.M"))
                availabiltyTimeList.add(AvailabilityTime("6 P.M - 7 P.M"))
                availabiltyTimeList.add(AvailabilityTime("7 P.M - 8 P.M"))

                availabiltyTimeList.add(AvailabilityTime("8 P.M - 9 P.M"))
                availabiltyTimeList.add(AvailabilityTime("9 P.M - 10 P.M"))
                availabiltyTimeList.add(AvailabilityTime("10 P.M - 11 P.M"))
                 */
    }

    private fun addAvailableOnList() {
        avilabilityOnList.add("Weekdays")
        avilabilityOnList.add("Weekends")
        avilabilityOnList.add("AllDays")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun dropdowns() {
        binding.availabilityCl.setOnClickListener{
            if (availabilityOnPopupWindow != null) {
                if (availabilityOnPopupWindow!!.isShowing) {
                    availabilityOnPopupWindow!!.dismiss()
                } else {
                    availabilityOnDropDown()
                }
            } else {
                availabilityOnDropDown()
            }
        }

        binding.availableTimeCl.setOnClickListener {
            if (availabilityOnPopupWindow != null) {
                if (availabilityOnPopupWindow!!.isShowing) {
                    availabilityOnPopupWindow!!.dismiss()
                }
            }

            if (availabilityTimePopWindow != null) {
                if (availabilityTimePopWindow!!.isShowing) {
                    availabilityTimePopWindow!!.dismiss()
                } else {
//                    availabilityTimeDropDown()
                    availableTimePopup()
                }
            } else {
//                availabilityTimeDropDown()
                availableTimePopup()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun availableTimePopup() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.available_time_layout, null)
        logoutConfirmationDialog = Dialog(this@DailyHelpMemberListActivity, R.style.CustomAlertDialog)
        logoutConfirmationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutConfirmationDialog!!.setContentView(view)
        logoutConfirmationDialog!!.setCanceledOnTouchOutside(true)
        logoutConfirmationDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(logoutConfirmationDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        logoutConfirmationDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val yes_btn: TextView = view.findViewById(R.id.yes_btn)


        val recyclerView: RecyclerView = view.findViewById(R.id.rv_available_time)
        val availabilityTimeList =  availabiltyTimeList

        val adapter = AvailabilityTimeAdapter(this, availabilityTimeList, selectedItems!!)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter

        yes_btn.setOnClickListener{
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }

            selectedItems = adapter.getSelectedItems().joinToString ()
            binding.availableTimeText.text = selectedItems
            Log.d("SelectedIds", selectedItems!!)
            getDailyHelpList(seleteAvailableOn ,selectedItems)
        }


        close.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }
        }

        logoutConfirmationDialog!!.show()
    }

    private fun availabilityTimeDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        availabilityTimePopWindow = PopupWindow(
            view,
            binding.availableTimeText.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (availabiltyTimeList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val availableTimeAdapter = AvailablityTimeAdapter(this, availabiltyTimeList)

            dropDownRecyclerView.adapter = availableTimeAdapter
            availableTimeAdapter.setCallbackDailyHelpMemberListActivity(this@DailyHelpMemberListActivity)
        }
        availabilityTimePopWindow!!.elevation = 10F
        availabilityTimePopWindow!!.showAsDropDown(binding.availableTimeText, 0, 0, Gravity.CENTER)
    }

    private fun availabilityOnDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        availabilityOnPopupWindow = PopupWindow(
            view,
            binding.availableOnTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (avilabilityOnList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearAdapter = YearAdapter(this, avilabilityOnList)

            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setCallbackDailyHelpMemberListActivity(this@DailyHelpMemberListActivity)
        }
        availabilityOnPopupWindow!!.elevation = 10F
        availabilityOnPopupWindow!!.showAsDropDown(binding.availableOnTxt, 0, 0, Gravity.CENTER)
    }

    /* private fun addDailyHelpMemberListData() {
         dailyHelpMembersList.add(
             DailyHelpMemberList(
                 "Shanthi", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                 "9876543210", "4.1", "", "", "", "", ""
             )
         )
         dailyHelpMembersList.add(
             DailyHelpMemberList(
                 "Suresh", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                 "9876543210", "4.1", "", "", "", "", ""
             )
         )
         dailyHelpMembersList.add(
             DailyHelpMemberList(
                 "Harish", "5 A.M-6 A.M", "Weekend", "B104,B102",
                 "9876543210", "4.1", "", "", "", "", ""
             )
         )
     }*/

    private fun populateData() {
        if (dailyHelpMembersList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.dailyHelpMemberListRecyclerView.layoutManager = LinearLayoutManager(this)
            dailyHelpMemberListAdapter = DailyHelpMemberListAdapter(this, dailyHelpMembersList)
            binding.dailyHelpMemberListRecyclerView.adapter = dailyHelpMemberListAdapter
            dailyHelpMemberListAdapter.setCallback(this@DailyHelpMemberListActivity)
        }
    }

    fun selectedMember(dailyHelpStaffModel: DailyHelpStaffModel.Data) {
        val eIntent = Intent(this, SelectedMemberInfoActivity::class.java)
        eIntent.putExtra("dailyHelpStaffModel", dailyHelpStaffModel)
        eIntent.putExtra("MemberRoleName", dailyHelpStaffModel.staffTypeName)
        eIntent.putExtra("MemeberRole", MemeberRole)
        eIntent.putExtra("availableOn", seleteAvailableOn)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(eIntent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, DailyHelpActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCallbackDailyHelpMemberListActivity(avilabilityOn: String) {
        if (availabilityOnPopupWindow != null) {
            if (availabilityOnPopupWindow!!.isShowing) {
                availabilityOnPopupWindow!!.dismiss()
            }
        }

        if(avilabilityOn!= null){
            binding.availableOnTxt.text = avilabilityOn
            seleteAvailableOn = avilabilityOn
            getDailyHelpList(seleteAvailableOn ,selectedItems)

        }
    }

    fun setCallbackDailyHelpAvilabilityTime(availabilityTime: AvailabilityTime) {
        if (availabilityTimePopWindow != null) {
            if (availabilityTimePopWindow!!.isShowing) {
                availabilityTimePopWindow!!.dismiss()
            }
        }

//        for (availabiltyTimeList in availabiltyTimeList) {
//            availabiltyTimeList.add(AvailabilityTime("5A.M - 6A.M", "false"))
//        }
        if(availabilityTime!= null){
            binding.availableTimeText.text = availabilityTime.Time
        }
    }
}