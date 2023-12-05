package com.example.safehome.enews

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.databinding.ENewsDetialActivityBinding
import com.example.safehome.policies.PoliciesActivity

class ENewsDetailActivity :BaseActivity() {
    private lateinit var binding: ENewsDetialActivityBinding
    private lateinit var eNewItem: UserENewsListModel.Data.ENew
    private var sharePopupWindow: PopupWindow? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ENewsDetialActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inIt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun inIt() {
        if (intent!= null){
            eNewItem = intent.getSerializableExtra("eNewItem") as UserENewsListModel.Data.ENew
        }
        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, ENewsActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (!eNewItem.topIcName.isNullOrEmpty()) {
            binding.newsTopicTxt.text = eNewItem.topIcName
        }
        if (!eNewItem.eNews.isNullOrEmpty()) {
            binding.newsContentTxt.text = eNewItem.eNews
        }
        if (!eNewItem.createdByRole.isNullOrEmpty()) {
            binding.enewsCreatorTxt.text = eNewItem.createdByRole
        }
        if (eNewItem.eNewsImages.isNotEmpty()) {
            binding.eNewsItemImg.visibility = View.VISIBLE
            for (i in 0 until eNewItem.eNewsImages.size) {
                Glide.with(this@ENewsDetailActivity)
                    .load("https://qa.msafehome.com/${eNewItem.eNewsImages[i].attachementPath}")
                    .fitCenter()
                    .into(binding.eNewsItemImg)
            }
        }else{
            binding.eNewsItemImg.visibility = View.GONE
        }

        if (!eNewItem.timeStamp.isNullOrEmpty()){
            val times = eNewItem.postedTime.split(":")
            val hour = times[0]
            val minute = times[1]
            val postedTime = "$hour:$minute"
            val pMorAM = if (hour > 11.toString()) "PM" else  "AM"
            binding.newsTimeStamp.text = Utils.formatMonthAndDateYear(eNewItem.postedDate) + " "+ postedTime + " "+pMorAM
        }

        binding.shareIconImg.setOnClickListener {
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
        sharePopupWindow!!.showAsDropDown(binding.shareIconImg, 0, 0, Gravity.CENTER)
    }
}