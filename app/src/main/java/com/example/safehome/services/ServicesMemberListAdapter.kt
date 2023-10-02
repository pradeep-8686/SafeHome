package com.example.safehome.services

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
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.ServiceDataList

class ServicesMemberListAdapter(
    var context: Context,
    private var servicesDataMemberList: ArrayList<ServiceDataList.Data>
) :
    RecyclerView.Adapter<ServicesMemberListAdapter.MyViewHolder>() {
    private lateinit var servicesMemberListActivity: ServicesMemberListActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.services_members_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val serviceDataList = servicesDataMemberList[position]
        holder.daily_help_booking_item_list_layout.tag = serviceDataList
        holder.book_now_text.tag = serviceDataList

        if (serviceDataList.personName != null && serviceDataList.personName.isNotEmpty()) {
            holder.name_tv.text = serviceDataList.personName
        }
        if (serviceDataList.companyName != null && serviceDataList.companyName.isNotEmpty()) {
            holder.company_name_tv.text = "Company Name: "+ serviceDataList.companyName
        }

        if (serviceDataList.availableOn!= null && serviceDataList.availableOn.isNotEmpty()){
            holder.available_on_tv.text = "Available on: "+serviceDataList.availableOn
        }

        var sortList = ArrayList<ServiceDataList.Data.ServiceProvide>()
        for (model in serviceDataList.serviceProvides){
            if (model.serviceProvideName != null){
                sortList.add(model)
            }
        }
        if (serviceDataList.serviceProvides != null && serviceDataList.serviceProvides.isNotEmpty()) {
            holder.service_provided_tv.text = "Service Provided: "+sortList.joinToString(", ") { it1 ->"${it1.serviceProvideName!!} " }
        }else{
            holder.service_provided_tv.text = "Service Provided: "+" "

        }


//        if (dailyHelpMemberList.works_in != null && dailyHelpMemberList.works_in.isNotEmpty()) {
   //     holder.service_provided_tv.text = "Service Provided: "+ "Deep Clean Service"

     //   }

//        holder.daily_help_booking_item_list_layout.setOnClickListener {
//            dailyHelpMemberListActivity.selectedMember(it.tag as DailyHelpMemberList)
//        }
        holder.book_now_text.setOnClickListener {
            servicesMemberListActivity.selectedMember(it.tag as ServiceDataList.Data)
        }

    }

    override fun getItemCount(): Int {
        if (servicesDataMemberList.isNotEmpty()) {
            return servicesDataMemberList.size
        }
        return 0
    }

    fun setCallback(servicesMemberListActivity: ServicesMemberListActivity) {
        this.servicesMemberListActivity = servicesMemberListActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val company_name_tv: TextView = itemView.findViewById(R.id.company_name_tv)
        val service_provided_tv: TextView = itemView.findViewById(R.id.service_provided_tv)
        val available_on_tv: TextView = itemView.findViewById(R.id.available_on_tv)
        val book_now_text: TextView = itemView.findViewById(R.id.book_now_text)
        val daily_help_booking_item_list_layout: LinearLayout = itemView.findViewById(R.id.daily_help_member_list_layout)
    }

    fun filterList(servicesDataMemberList: ArrayList<ServiceDataList.Data>) {
        this.servicesDataMemberList = servicesDataMemberList;
        notifyDataSetChanged();
    }


}