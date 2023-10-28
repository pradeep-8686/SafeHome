package com.example.safehome.meetings

import android.app.Dialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityMeetingMinutesBinding
import com.example.safehome.model.UpcomingMeetingsModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class MeetingMinutesActivity : AppCompatActivity() {

    private var Meetingdata: UpcomingMeetingsModel.Data.MeetingData?= null
    private var meetingsShareOptionsDialog: Dialog?= null
    private lateinit var binding: ActivityMeetingMinutesBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private var sharePopupWindow: PopupWindow? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingMinutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        Meetingdata = (intent.getSerializableExtra("Meetingdata") as UpcomingMeetingsModel.Data.MeetingData?)
        binding.backBtnClick.setOnClickListener {
            finish()
        }
        binding.okBtn.setOnClickListener {
            finish()
        }
        binding.shareImage.setOnClickListener {
//            showShareOptionsDialog()

            complaintType()

        }

        binding.tvComplaintType.text = Meetingdata!!.topicName
        binding.textView21.text = "Meeting Date : ${Utils.formatDateMonthYear(Meetingdata!!.meetingDate)}"
        if (Meetingdata!!.startTime != null && Meetingdata!!.endTime!= null) {
            binding.textView20.text = "Time : ${Meetingdata!!.startTime} - ${Meetingdata!!.endTime}"
        }
    }
    private fun complaintType() {
        if (sharePopupWindow != null) {
            if (sharePopupWindow!!.isShowing) {
                sharePopupWindow!!.dismiss()
            } else {
                shareDropDown()
            }
        } else {
            shareDropDown()
        }
    }
    private fun shareDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.share_down_layout, null)

        sharePopupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        sharePopupWindow!!.elevation = 10F
        sharePopupWindow!!.showAsDropDown(binding.shareImage, 0, 0, Gravity.CENTER)
    }
    private fun showShareOptionsDialog() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.meetings_share_view_dialog, null)
        meetingsShareOptionsDialog = Dialog(this@MeetingMinutesActivity, R.style.CustomAlertDialog)
        meetingsShareOptionsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        meetingsShareOptionsDialog!!.setContentView(view)
        meetingsShareOptionsDialog!!.setCanceledOnTouchOutside(true)
        meetingsShareOptionsDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(meetingsShareOptionsDialog!!.window!!.attributes)

//        lp.width = (Utils.screenWidth * 0.9).toInt()
//        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        meetingsShareOptionsDialog!!.window!!.attributes = lp

        meetingsShareOptionsDialog!!.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (meetingsShareOptionsDialog!!.isShowing){
            meetingsShareOptionsDialog!!.dismiss()
        }else{
            finish()
        }
    }
}