package com.example.safehome.notice

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.maintenance.MaintenanceActivity

class NoticeDropdownAdapter(noticeActivity: NoticeActivity, private var noticesList: List<String>) :
    RecyclerView.Adapter<NoticeDropdownAdapter.MyviewHolder>() {

    private lateinit var noticeActivity: NoticeActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.notice_dropdown_item_view, parent, false)
        return MyviewHolder(view)
    }

    override fun getItemCount(): Int {
        if (noticesList.isNotEmpty()) {
            return noticesList.size
        }
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        val selectedDropDownText:String = noticesList[position]
        holder.selectNoticeTxt.tag=selectedDropDownText
        if (selectedDropDownText.isNotEmpty()) {
            holder.selectNoticeTxt.text = selectedDropDownText
        }
        holder.selectNoticeTxt.setOnClickListener {
            noticeActivity.clickNoticeDropdown(it.tag as String)
        }
    }

    fun setCallback(noticeActivity: NoticeActivity) {
        this.noticeActivity = noticeActivity
    }

    class MyviewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val selectNoticeTxt: TextView = itemView.findViewById(R.id.select_notice_dropdown_text)
    }
}