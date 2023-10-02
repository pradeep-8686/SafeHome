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
import com.example.safehome.model.ServiceDataList
import com.example.safehome.model.ServiceTypesList

class ServiceListAdapter(
    var context: Context,
    private var serviceTypesList: ArrayList<ServiceTypesList.ServicesListItem>
) :
    RecyclerView.Adapter<ServiceListAdapter.MyViewHolder>() {
    private lateinit var servicesListFragment: ServicesListFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.daily_help_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val serviceType = serviceTypesList[position]
        holder.daily_help_list_layout.tag = serviceType

        if (serviceType.serviceTypeName != null && serviceType.serviceTypeName.isNotEmpty()) {
            holder.member_designation.text = serviceType.serviceTypeName
        }
        if (serviceType.staffCount != null && serviceType.staffCount.toString().isNotEmpty()) {
            holder.count.text = serviceType.staffCount.toString()
        }

        holder.daily_help_list_layout.setOnClickListener {
            servicesListFragment.selectedRole(it.tag as ServiceTypesList.ServicesListItem)
        }

    }

    override fun getItemCount(): Int {
        if (serviceTypesList.isNotEmpty()) {
            return serviceTypesList.size
        }
        return 0
    }

    fun setCallback(dailyHelpListFragment: ServicesListFragment) {
        this.servicesListFragment = dailyHelpListFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_designation: TextView = itemView.findViewById(R.id.member_designation)
        val count: TextView = itemView.findViewById(R.id.count)
        val daily_help_list_layout: LinearLayout = itemView.findViewById(R.id.daily_help_list_layout)
    }

    fun filterList(serviceTypesList: ArrayList<ServiceTypesList.ServicesListItem>) {
        this.serviceTypesList = serviceTypesList;
        notifyDataSetChanged();
    }

}