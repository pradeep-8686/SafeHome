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
import com.example.safehome.model.DailyHelpStaffModel

class DailyHelpMemberListAdapter(
    var context: Context,
    private var dailyHelpMemberList: ArrayList<DailyHelpStaffModel.Data>
) :
    RecyclerView.Adapter<DailyHelpMemberListAdapter.MyViewHolder>() {
    private lateinit var dailyHelpMemberListActivity: DailyHelpMemberListActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.daily_help_members_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dailyHelpMemberList = dailyHelpMemberList[position]
        holder.daily_help_booking_item_list_layout.tag = dailyHelpMemberList
        holder.book_now_text.tag = dailyHelpMemberList

        if (dailyHelpMemberList.staffName != null && dailyHelpMemberList.staffName.isNotEmpty()) {
            holder.name_tv.text = dailyHelpMemberList.staffName
        }
        if (dailyHelpMemberList.availableOn != null && dailyHelpMemberList.availableOn.isNotEmpty()) {
            holder.available_on_tv.text = "Availabile On: "+dailyHelpMemberList.availableOn
        }
        var sortList = ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>()
        var distinctSortedList = ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>()

        for (model in dailyHelpMemberList.staffworkingDetails){
            if (model.residentdetais != null){
                sortList.add(model)
            }
        }
        distinctSortedList = sortList.distinctBy { it.residentdetais!!.flatNo} as ArrayList<DailyHelpStaffModel.Data.StaffworkingDetail>
        if (dailyHelpMemberList.staffworkingDetails != null && dailyHelpMemberList.staffworkingDetails.isNotEmpty()) {
            holder.work_in_tv.text = "Works In: "+distinctSortedList.joinToString(", ") { it1 ->"${it1.residentdetais!!.block} ${it1.residentdetais!!.flatNo}" }
        }

//        holder.daily_help_booking_item_list_layout.setOnClickListener {
//            dailyHelpMemberListActivity.selectedMember(it.tag as DailyHelpMemberList)
//        }
        holder.book_now_text.setOnClickListener {
            dailyHelpMemberListActivity.selectedMember(it.tag as DailyHelpStaffModel.Data)
        }

    }

    override fun getItemCount(): Int {
        if (dailyHelpMemberList.isNotEmpty()) {
            return dailyHelpMemberList.size
        }
        return 0
    }

    fun setCallback(dailyHelpMemberListActivity: DailyHelpMemberListActivity) {
        this.dailyHelpMemberListActivity = dailyHelpMemberListActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val available_on_tv: TextView = itemView.findViewById(R.id.available_on_tv)
        val work_in_tv: TextView = itemView.findViewById(R.id.work_in_tv)
        val book_now_text: TextView = itemView.findViewById(R.id.book_now_text)
        val daily_help_booking_item_list_layout: LinearLayout = itemView.findViewById(R.id.daily_help_member_list_layout)
    }


    fun filterList(dailyHelpMemberList: ArrayList<DailyHelpStaffModel.Data>) {
        this.dailyHelpMemberList = dailyHelpMemberList;
        notifyDataSetChanged();
    }

}