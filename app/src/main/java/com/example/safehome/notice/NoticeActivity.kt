package com.example.safehome.notice

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityNoticeLayoutBinding
import com.example.safehome.model.GetAllHistoryServiceTypes
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.Notice
import com.example.safehome.model.NoticesListModel
import com.example.safehome.model.ServicesStaffHistoryList
import com.example.safehome.model.YearModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeActivity: Activity() {
    var option: List<String> = ArrayList<String>()
    private var noticePopupWindow: PopupWindow?= null
    private lateinit var noticeBinding: ActivityNoticeLayoutBinding
    private var noticeAlertDialog: Dialog? = null
    private var noticesItemsList: ArrayList<NoticesListModel> = ArrayList()
    private var unreadNoticesList: ArrayList<NoticesListModel> = ArrayList()
    private lateinit var noticesListAdapter: NoticesAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var Auth_Token: String? = ""
    private var residentId: String?= null
    private lateinit var getAllNoticeCall: Call<GetAllNoticeStatus>
    private var getAllNoticeStatusList: ArrayList<Notice> = ArrayList()
    private var noticeViewList: ArrayList<String> = ArrayList()
    private lateinit var yearModel : Call<YearModel>
    private var yearList: ArrayList<YearModel.Data> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    var ScreenFrom: String? = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noticeBinding = ActivityNoticeLayoutBinding.inflate(layoutInflater)
        setContentView(noticeBinding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@NoticeActivity)
        residentId = Utils.getStringPref(this@NoticeActivity, "residentId", "")
        Auth_Token = Utils.getStringPref(this@NoticeActivity, "Token", "")

        noticeBinding.yearCl.setOnClickListener {
            if (noticePopupWindow != null) {
                if (noticePopupWindow!!.isShowing) {
                    noticePopupWindow!!.dismiss()
                }
            }
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                } else {
                    yearDropDown()
                }
            } else {
                yearDropDown()
            }
        }

        inIt()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        noticeBinding.selectNoticeTxt.text = "All"
        /*  noticeBinding.noticesClearAllTxt.paintFlags =
              noticeBinding.noticesClearAllTxt.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG*/
        //    showNoticePopupWindow()
        // addNoticesListData()
        // populateData()
        addYearList()
        addNoticeViewList()
        getAllnoticeApiCall("","2023")
        clickEvents()
    }

    private fun addNoticeViewList() {
        noticeViewList.add("All")
        noticeViewList.add("Read")
        noticeViewList.add("Unread")


    }

    private fun clickEvents() {
        /*    noticeBinding.noticesClearAllTxt.setOnClickListener{
                ToamakeText(applicationContext, "clecked", Toast.LENGTH_LONG).show()
                noticesItemsList.clear()
            }
    */
        noticeBinding.backBtnClick.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        noticeBinding.noticeDropDownLayout.setOnClickListener {
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (noticePopupWindow != null) {
                if (noticePopupWindow!!.isShowing) {
                    noticePopupWindow!!.dismiss()
                } else {
                    showNoticePopupWindow()
                }
            } else {
                showNoticePopupWindow()
            }
        }
    }

    private fun addNoticesListData() {
        noticesItemsList.add(
            NoticesListModel(
                "Admin", "6m ago", "General Announcements","read"
            )
        )
        noticesItemsList.add(
            NoticesListModel(
                "Admin", "10m ago", "Committee Meeting","read"
            )
        )

        unreadNoticesList.add(
            NoticesListModel("Admin", "6m ago", "Committee Meeting", "Unread")
        )
    }
    private fun populateData() {
        noticeBinding.noticesRecyclerview.layoutManager = LinearLayoutManager(this@NoticeActivity)
//        noticesListAdapter = if (noticeBinding.selectNoticeTxt.text.toString().equals("Unread")) {
//            NoticesAdapter(this@NoticeActivity, unreadNoticesList)
//        }else{
//            NoticesAdapter(this@NoticeActivity, noticesItemsList)
//        }
        noticesListAdapter = NoticesAdapter(this@NoticeActivity, getAllNoticeStatusList)
        noticeBinding.noticesRecyclerview.adapter = noticesListAdapter
        noticesListAdapter.setCallback(this@NoticeActivity)
    }

    private fun showNoticePopupWindow() {
        val popUpView = layoutInflater.inflate(R.layout.drop_down_layout, null)

        noticePopupWindow = PopupWindow(
            popUpView,
            noticeBinding.selectNoticeTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (noticeViewList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this@NoticeActivity)
            val dropDownRecyclerView = popUpView.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val noticesAdapter = NoticeDropdownAdapter(this@NoticeActivity, noticeViewList)

            dropDownRecyclerView.adapter = noticesAdapter
            noticesAdapter.setCallback(this@NoticeActivity)
        }
        noticePopupWindow!!.elevation = 10F
        noticePopupWindow!!.showAsDropDown(noticeBinding.selectNoticeTxt, 0, 0, Gravity.CENTER)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickNoticeDropdown(selectedDropdownTxt: String) {
        if (noticePopupWindow != null) {
            if (noticePopupWindow!!.isShowing) {
                noticePopupWindow!!.dismiss()
            }
        }
        if (selectedDropdownTxt!= null) {
            noticeBinding.selectNoticeTxt.text = selectedDropdownTxt
        }
        noticePopupWindow!!.dismiss()
        var readStatus: String?= ""
        when(selectedDropdownTxt){
            "Read" ->{
                readStatus = "true"
            }
            "Unread" ->{
                readStatus = "false"
            }
            "All" ->{
                readStatus = ""
            }
        }
        //   showNoticePopupWindow()
        // populateData()
        getAllnoticeApiCall(readStatus, "2023")
    }


    @SuppressLint("MissingInflatedId")
    private fun noticeAlertDialog(noticesListModel: NoticesListModel) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.notice_dialog_layout, null)
        noticeAlertDialog = Dialog(this@NoticeActivity, R.style.CustomAlertDialog)
        noticeAlertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        noticeAlertDialog!!.setContentView(view)
        noticeAlertDialog!!.setCanceledOnTouchOutside(true)
        noticeAlertDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(noticeAlertDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        noticeAlertDialog!!.window!!.attributes = lp

        val closeImg = view.findViewById<ImageView>(R.id.notice_dialog_closeImg)
        val postedByName: TextView = view.findViewById(R.id.posted_dialog_notice_nameTxt)
        val postedByTime: TextView = view.findViewById(R.id.posted_dialog_notice_timeTxt)
        val postedByDate: TextView = view.findViewById(R.id.posted_dialog_notice_dateTxt)

        closeImg.setOnClickListener {
            if (noticeAlertDialog!!.isShowing) {
                noticeAlertDialog!!.dismiss()
            }
        }

        noticeAlertDialog!!.show()
    }

    fun noticesItemClick(noticesListModel: Notice) {
//        noticeAlertDialog(noticesListModel)
        val intent = Intent(this, NoticeDetailsActivity::class.java)
        intent.putExtra("NoticeData", noticesListModel)
        startActivity(intent)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllnoticeApiCall(noticeView: String?, year: String) {
        Log.e("Token", Auth_Token.toString())
        customProgressDialog.progressDialogShow(this@NoticeActivity, this.getString(R.string.loading))

        getAllNoticeCall = apiInterface.getallNoticesStatus("bearer " + Auth_Token,10,1,noticeView,year)
        getAllNoticeCall.enqueue(object : Callback<GetAllNoticeStatus> {
            override fun onResponse(
                call: Call<GetAllNoticeStatus>,
                response: Response<GetAllNoticeStatus>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (getAllNoticeStatusList.isNotEmpty()) {
                                        getAllNoticeStatusList.clear()
                                    }
                                    getAllNoticeStatusList =
                                        response.body()!!.data.noticedata as ArrayList<Notice>
                                    populateData()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@NoticeActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@NoticeActivity, response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<GetAllNoticeStatus>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@NoticeActivity, t.message.toString())
            }
        })
    }

    private fun addYearList() {
        yearModel = apiInterface.yearList(
            "Bearer " + Auth_Token
        )
        yearModel.enqueue(object : Callback<YearModel> {
            override fun onResponse(
                call: Call<YearModel>,
                response: Response<YearModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (yearList.isNotEmpty()) {
                            yearList.clear()
                        }
                        val facilitiesModel = response.body() as YearModel
                        yearList = facilitiesModel.data as ArrayList<YearModel.Data>
                        noticeBinding.yearTxt.text = yearList[0].year.toString()
                    } else {
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<YearModel>, t: Throwable) {
                Utils.showToast(this@NoticeActivity, t.message.toString())
            }
        })

    }

    private fun yearDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        yearPopupWindow = PopupWindow(
            view,
            noticeBinding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this@NoticeActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearsArray = yearList.map { it.year.toString() }
            val yearAdapter = YearAdapter(this@NoticeActivity, yearsArray)
            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setCallbackNoticeYear(this@NoticeActivity)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(noticeBinding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectNoticeHistoeyYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if(year!= null){
            noticeBinding.yearTxt.text = year
            getAllnoticeApiCall("", year)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (noticePopupWindow != null) {
            if (noticePopupWindow!!.isShowing) {
                noticePopupWindow!!.dismiss()
            }
        }
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (noticePopupWindow != null) {
            if (noticePopupWindow!!.isShowing) {
                noticePopupWindow!!.dismiss()
            }
        }
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}