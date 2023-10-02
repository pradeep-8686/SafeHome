package com.example.safehome.dailyhelp

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.DailyHelpBookingListModel
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.DailyHelpRoles

class DailyHelpBookingListAdapter(
    var context: Context,
    private var dailyHelpMemberList: ArrayList<DailyHelpBookingListModel.Data>
) :
    RecyclerView.Adapter<DailyHelpBookingListAdapter.MyViewHolder>() {
    private lateinit var dailyHelpBookingsFragment: DailyHelpBookingsFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.daily_help_booking_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dailyHelpMemberList = dailyHelpMemberList[position]
        holder.daily_help_booking_item_list_layout.tag = dailyHelpMemberList
        holder.pay_now_btn.tag = dailyHelpMemberList

        if (dailyHelpMemberList.staffName != null && dailyHelpMemberList.staffName.isNotEmpty()) {
            holder.name_tv.text = dailyHelpMemberList.staffName
        }
        if (dailyHelpMemberList.staffTypeName != null && dailyHelpMemberList.staffTypeName.isNotEmpty()) {
            holder.role_tv.text = dailyHelpMemberList.staffTypeName
        }
        holder.pay_now_btn.setOnClickListener {
            dailyHelpBookingsFragment.selectedBookingPayUsing(it.tag as DailyHelpBookingListModel.Data)
        }

        holder.daily_help_booking_item_list_layout.setOnClickListener {
            dailyHelpBookingsFragment.selectedBookingItem(it.tag as DailyHelpBookingListModel.Data)
        }

    }

    override fun getItemCount(): Int {
        if (dailyHelpMemberList.isNotEmpty()) {
            return dailyHelpMemberList.size
        }
        return 0
    }

    fun setCallback(dailyHelpBookingsFragment: DailyHelpBookingsFragment) {
        this.dailyHelpBookingsFragment = dailyHelpBookingsFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val role_tv: TextView = itemView.findViewById(R.id.role_tv)
        val pay_now_btn: TextView = itemView.findViewById(R.id.pay_now_btn)

        val daily_help_booking_item_list_layout: LinearLayout = itemView.findViewById(R.id.daily_help_booking_item_list_layout)
    }
}